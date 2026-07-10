package com.curzyori.exapk.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import com.curzyori.exapk.ui.viewmodel.UiEvent
import com.curzyori.exapk.R
import com.curzyori.exapk.ui.components.AppDetailSheet
import com.curzyori.exapk.ui.components.AppItem
import com.curzyori.exapk.ui.components.EmptyState
import com.curzyori.exapk.ui.components.EmptyStateType
import com.curzyori.exapk.ui.components.FilterChips
import com.curzyori.exapk.ui.components.SearchBar
import com.curzyori.exapk.ui.components.ShimmerAppItem
import com.curzyori.exapk.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToSettings: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.actionLabel,
                        duration = if (event.actionLabel != null) SnackbarDuration.Long else SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.shareExtractedApks(context)
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.app_name))
                        if (!uiState.isLoading && uiState.error == null) {
                            Text(
                                text = "${uiState.filteredApps.size} ${stringResource(R.string.filter_all_lower)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    if (!uiState.isLoading && uiState.filteredApps.isNotEmpty()) {
                        if (uiState.selectedApps.isEmpty()) {
                            TextButton(onClick = { viewModel.onSelectAll() }) {
                                Icon(
                                    imageVector = Icons.Default.SelectAll,
                                    contentDescription = stringResource(R.string.select_all),
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                                Text(stringResource(R.string.select_all))
                            }
                        } else {
                            TextButton(onClick = { viewModel.onClearSelection() }) {
                                Icon(
                                    imageVector = Icons.Default.SelectAll,
                                    contentDescription = stringResource(R.string.clear_selection),
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                                Text(stringResource(R.string.clear_selection))
                            }
                        }
                    }
                    IconButton(onClick = { viewModel.loadApps() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.refresh),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.cd_settings))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = uiState.selectedApps.isNotEmpty() && !uiState.isExtracting,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.onExtractSelected() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = stringResource(R.string.cd_extract_selected)
                        )
                    },
                    text = { Text(stringResource(R.string.extract_with_count, uiState.selectedApps.size)) }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange
                )

                FilterChips(
                    selectedFilter = uiState.filterType,
                    onFilterChange = viewModel::onFilterChange,
                    selectedSort = uiState.sortType,
                    onSortChange = viewModel::onSortChange
                )

                when {
                    uiState.isLoading -> {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(10) {
                                ShimmerAppItem()
                            }
                        }
                    }
                    uiState.error != null -> {
                        EmptyState(
                            message = uiState.error!!,
                            type = EmptyStateType.ERROR,
                            showRetry = true,
                            onRetry = { viewModel.loadApps() }
                        )
                    }
                    uiState.filteredApps.isEmpty() -> {
                        val message = if (uiState.searchQuery.isNotEmpty()) {
                            stringResource(R.string.empty_search, uiState.searchQuery)
                        } else {
                            stringResource(R.string.empty_filter)
                        }
                        EmptyState(
                            message = message,
                            type = if (uiState.searchQuery.isNotEmpty()) EmptyStateType.NO_SEARCH else EmptyStateType.NO_FILTER
                        )
                    }
                    else -> {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = uiState.filteredApps,
                                key = { it.packageName }
                            ) { app ->
                                AppItem(
                                    app = app,
                                    icon = uiState.appIcons[app.packageName],
                                    isSelected = app.packageName in uiState.selectedApps,
                                    onSelect = { viewModel.onAppSelect(app.packageName, it) },
                                    onClick = { viewModel.onAppClick(app) }
                                )
                            }
                        }
                    }
                }
            }

            // Extraction progress overlay - pinned to bottom
            androidx.compose.animation.AnimatedVisibility(
                visible = uiState.isExtracting,
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                ExtractionProgress(
                    progress = uiState.extractProgress,
                    onCancel = { viewModel.cancelExtraction() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }

    uiState.selectedAppForDetail?.let { app ->
        AppDetailSheet(
            app = app,
            icon = uiState.appIcons[app.packageName],
            isExtracting = uiState.isExtracting,
            onExtract = { viewModel.onExtract(app) },
            onExtractShare = { viewModel.onExtractShare(app) },
            onDismiss = { viewModel.onDismissDetail() }
        )
    }

    // Batch extract confirmation dialog
    if (uiState.showExtractConfirm) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissConfirm() },
            title = { Text(stringResource(R.string.extract_confirm_title, uiState.pendingExtractCount)) },
            text = { Text(stringResource(R.string.extract_confirm_message, uiState.pendingExtractCount)) },
            confirmButton = {
                TextButton(onClick = { viewModel.onConfirmExtract() }) {
                    Text(stringResource(R.string.confirm_extract))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDismissConfirm() }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun ExtractionProgress(
    progress: com.curzyori.exapk.data.model.BatchExtractProgress?,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = progress?.let { p ->
                    if (p.total > 0) stringResource(R.string.extracting_progress, p.current, p.total)
                    else stringResource(R.string.preparing)
                } ?: stringResource(R.string.preparing),
                style = MaterialTheme.typography.bodyMedium
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (progress != null && progress.total > 0) {
                    Text(
                        text = "${(progress.current * 100 / progress.total)}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                IconButton(
                    onClick = onCancel,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.cd_cancel),
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (progress != null && progress.total > 0) {
            LinearProgressIndicator(
                progress = progress.current.toFloat() / progress.total,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
