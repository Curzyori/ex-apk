package com.curzyori.exapk.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curzyori.exapk.data.model.Language
import com.curzyori.exapk.data.model.ThemeMode
import com.curzyori.exapk.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val showDonateSheet: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = preferencesRepository.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.AUTO)

    val language: StateFlow<Language> = preferencesRepository.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Language.EN)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(mode)
        }
    }

    fun setLanguage(lang: Language) {
        viewModelScope.launch {
            preferencesRepository.setLanguage(lang)
        }
    }

    fun openGitHub() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Curzyori/ex-apk"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            android.util.Log.e("SettingsViewModel", "Failed to open GitHub", e)
        }
    }

    fun openWebsite() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://ex-apk.curzy.dev/"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            android.util.Log.e("SettingsViewModel", "Failed to open website", e)
        }
    }

    fun showDonateSheet() {
        _uiState.value = _uiState.value.copy(showDonateSheet = true)
    }

    fun hideDonateSheet() {
        _uiState.value = _uiState.value.copy(showDonateSheet = false)
    }

    fun openDonatePage() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://donate.curzy.dev/"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            hideDonateSheet()
        } catch (e: Exception) {
            android.util.Log.e("SettingsViewModel", "Failed to open donate page", e)
            hideDonateSheet()
        }
    }
}
