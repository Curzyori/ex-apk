package com.curzyori.exapk.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curzyori.exapk.R
import com.curzyori.exapk.data.model.AppInfo
import com.curzyori.exapk.data.model.BatchExtractProgress
import com.curzyori.exapk.data.model.FilterType
import com.curzyori.exapk.data.model.SortType
import com.curzyori.exapk.data.source.PackageManagerSource
import com.curzyori.exapk.domain.usecase.ExtractApkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed class UiEvent {
    data class ShowSnackbar(val message: String, val actionLabel: String? = null) : UiEvent()
}

data class MainUiState(
    val apps: List<AppInfo> = emptyList(),
    val filteredApps: List<AppInfo> = emptyList(),
    val appIcons: Map<String, Drawable?> = emptyMap(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val searchQuery: String = "",
    val filterType: FilterType = FilterType.USER_APPS,
    val sortType: SortType = SortType.NAME,
    val selectedApps: Set<String> = emptySet(),
    val selectedAppForDetail: AppInfo? = null,
    val isExtracting: Boolean = false,
    val extractProgress: BatchExtractProgress? = null,
    val extractResult: String? = null,
    val showExtractConfirm: Boolean = false,
    val pendingExtractCount: Int = 0,
    val lastExtractedPaths: List<String> = emptyList()
)

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val packageManagerSource: PackageManagerSource,
    private val extractApkUseCase: ExtractApkUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    private var extractionJob: Job? = null

    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadApps()
    }

fun loadApps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val apps = withContext(Dispatchers.IO) {
                    packageManagerSource.getInstalledApps()
                }
                // Show apps immediately, load icons in background
                _uiState.update {
                    it.copy(
                        apps = apps,
                        filteredApps = filterAndSort(apps, it.searchQuery, it.filterType, it.sortType),
                        isLoading = false
                    )
                }
                loadAppIconsChunk(apps, 0, 20).also { icons ->
                    _uiState.update { it.copy(appIcons = it.appIcons + icons) }
                }
                loadRemainingIcons(20)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Failed to load apps")
                }
            }
        }
    }

    private fun updateFilteredApps() {
        _uiState.update {
            it.copy(
                filteredApps = filterAndSort(it.apps, it.searchQuery, it.filterType, it.sortType)
            )
        }
    }

    private suspend fun loadRemainingIcons(startIndex: Int) {
        val apps = _uiState.value.apps
        if (startIndex >= apps.size) return
        for (i in startIndex until apps.size step 30) {
            val icons = loadAppIconsChunk(apps, i, 30)
            _uiState.update { it.copy(appIcons = it.appIcons + icons) }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                filteredApps = filterAndSort(it.apps, query, it.filterType, it.sortType)
            )
        }
    }

    fun onFilterChange(filterType: FilterType) {
        _uiState.update {
            it.copy(
                filterType = filterType,
                filteredApps = filterAndSort(it.apps, it.searchQuery, filterType, it.sortType)
            )
        }
    }

    fun onSortChange(sortType: SortType) {
        _uiState.update {
            it.copy(
                sortType = sortType,
                filteredApps = filterAndSort(it.apps, it.searchQuery, it.filterType, sortType)
            )
        }
    }

    fun onAppSelect(packageName: String, selected: Boolean) {
        _uiState.update {
            val newSelected = if (selected) {
                it.selectedApps + packageName
            } else {
                it.selectedApps - packageName
            }
            it.copy(selectedApps = newSelected, filteredApps = filterAndSort(it.apps, it.searchQuery, it.filterType, it.sortType))
        }
    }

    fun onSelectAll() {
        _uiState.update {
            it.copy(selectedApps = it.filteredApps.map { app -> app.packageName }.toSet())
        }
    }

    fun onClearSelection() {
        _uiState.update { it.copy(selectedApps = emptySet()) }
    }

    fun onAppClick(app: AppInfo) {
        _uiState.update { it.copy(selectedAppForDetail = app) }
    }

    fun onDismissDetail() {
        _uiState.update { it.copy(selectedAppForDetail = null) }
    }

    fun onExtract(app: AppInfo) {
        extractApps(listOf(app))
    }

    fun onExtractSelected() {
        val count = _uiState.value.filteredApps.count { it.packageName in _uiState.value.selectedApps }
        if (count > 0) {
            _uiState.update { it.copy(showExtractConfirm = true, pendingExtractCount = count) }
        }
    }

    fun onConfirmExtract() {
        val selectedPackages = _uiState.value.selectedApps
        val apps = _uiState.value.filteredApps.filter { it.packageName in selectedPackages }
        _uiState.update { it.copy(showExtractConfirm = false) }
        extractApps(apps)
        onClearSelection()
    }

    fun onDismissConfirm() {
        _uiState.update { it.copy(showExtractConfirm = false) }
    }

    fun onExtractShare(app: AppInfo) {
        shareApk(app)
    }

    fun onExtractShareSelected() {
        val selectedApps = _uiState.value.filteredApps.filter { it.packageName in _uiState.value.selectedApps }
        if (selectedApps.isEmpty()) return

        shareApk(selectedApps.first())
        onClearSelection()
        _uiState.update { it.copy(extractResult = "Shared ${selectedApps.size} APK(s)") }
    }

    fun cancelExtraction() {
        extractionJob?.cancel()
        extractionJob = null
        _uiState.update {
            it.copy(
                isExtracting = false,
                extractProgress = null,
                extractResult = "Extraction cancelled"
            )
        }
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000)
            _uiState.update { it.copy(extractResult = null) }
        }
    }

    private suspend fun loadAppIconsChunk(apps: List<AppInfo>, startIndex: Int, batchSize: Int): Map<String, Drawable?> {
        val batch = apps.subList(startIndex, minOf(startIndex + batchSize, apps.size))
        return batch.associate { it.packageName to packageManagerSource.getAppIcon(it.packageName) }
    }

    private fun extractApps(apps: List<AppInfo>, share: Boolean = false) {
        if (apps.isEmpty()) return

        if (share && apps.size == 1) {
            shareApk(apps.first())
            return
        }

        extractionJob = viewModelScope.launch {
            _uiState.update { it.copy(isExtracting = true) }

            var finalProgress: BatchExtractProgress? = null
            try {
                extractApkUseCase(apps).collect { progress ->
                    finalProgress = progress
                    _uiState.update { it.copy(extractProgress = progress) }
                }

                val result = if (apps.size == 1) {
                    val app = apps.first()
                    val safePackageName = app.packageName.replace(".", "_")
                    val safeVersionName = app.versionName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
                    context.getString(R.string.extract_success_file, "${safePackageName}_${safeVersionName}.apk")
                } else {
                    finalProgress?.let { p ->
                        context.getString(R.string.extract_result_batch, p.extracted, p.total, p.skipped, p.failed)
                    } ?: context.getString(R.string.extract_complete)
                }

                _uiState.update {
                    it.copy(
                        isExtracting = false,
                        extractProgress = null,
                        lastExtractedPaths = finalProgress?.extractedPaths ?: emptyList()
                    )
                }

                _uiEvent.send(UiEvent.ShowSnackbar(result, context.getString(R.string.share_apk)))
            } catch (e: kotlinx.coroutines.CancellationException) {
                _uiState.update {
                    it.copy(
                        isExtracting = false,
                        extractProgress = null
                    )
                }
                _uiEvent.send(UiEvent.ShowSnackbar(context.getString(R.string.extract_cancelled)))
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isExtracting = false,
                        extractProgress = null
                    )
                }
                _uiEvent.send(UiEvent.ShowSnackbar(context.getString(R.string.extract_failed, e.message ?: "")))
            }
        }
    }

    fun shareApk(app: AppInfo) {
        viewModelScope.launch {
            try {
                val sourceFile = java.io.File(app.apkPath)
                
                if (!sourceFile.exists()) {
                    _uiState.update { it.copy(extractResult = "APK file not found") }
                    kotlinx.coroutines.delay(3000)
                    _uiState.update { it.copy(extractResult = null) }
                    return@launch
                }
                
                val cacheFile = java.io.File(context.cacheDir, "${app.packageName}.apk")

                withContext(Dispatchers.IO) {
                    sourceFile.inputStream().use { input ->
                        cacheFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }

                val uri = androidx.core.content.FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    cacheFile
                )

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/vnd.android.package-archive"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(Intent.createChooser(intent, context.getString(com.curzyori.exapk.R.string.share_apk)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })

                viewModelScope.launch {
                    kotlinx.coroutines.delay(1000)
                    if (cacheFile.exists()) cacheFile.delete()
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Failed to share APK", e)
                _uiState.update { it.copy(extractResult = "Failed to share: ${e.message}") }
                kotlinx.coroutines.delay(3000)
                _uiState.update { it.copy(extractResult = null) }
            }
        }
    }

    private fun filterAndSort(
        apps: List<AppInfo>,
        query: String,
        filterType: FilterType,
        sortType: SortType
    ): List<AppInfo> {
        return apps
            .filter { app ->
                when (filterType) {
                    FilterType.ALL -> true
                    FilterType.USER_APPS -> !app.isSystemApp
                    FilterType.SYSTEM_APPS -> app.isSystemApp
                }
            }
            .filter { app ->
                if (query.isBlank()) true
                else app.appName.contains(query, ignoreCase = true) ||
                        app.packageName.contains(query, ignoreCase = true)
            }
            .sortedWith(
                when (sortType) {
                    SortType.NAME -> compareBy { it.appName.lowercase() }
                    SortType.SIZE -> compareByDescending { it.apkSize }
                    SortType.DATE -> compareByDescending { it.installTime }
                }
            )
    }

    fun shareExtractedApks(context: Context) {
        val paths = _uiState.value.lastExtractedPaths
        if (paths.isEmpty()) return
        val uris = paths.mapNotNull { path ->
            if (path.startsWith("content://")) {
                try { Uri.parse(path) } catch (_: Exception) { null }
            } else {
                try {
                    val file = java.io.File(path)
                    if (file.exists())
                        androidx.core.content.FileProvider.getUriForFile(
                            context, "${context.packageName}.provider", file
                        )
                    else null
                } catch (_: Exception) { null }
            }
        }
        if (uris.isEmpty()) return
        try {
            val intent = if (uris.size == 1) {
                Intent(Intent.ACTION_SEND).apply {
                    type = "application/vnd.android.package-archive"
                    putExtra(Intent.EXTRA_STREAM, uris.first())
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            } else {
                Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                    type = "application/vnd.android.package-archive"
                    putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_apk)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    val uri = Uri.parse("content://com.android.externalstorage.documents/document/primary%3ADownload%2FExAPK")
                    setDataAndType(uri, "vnd.android.document/directory")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (_: Exception) {}
        }
    }
}
