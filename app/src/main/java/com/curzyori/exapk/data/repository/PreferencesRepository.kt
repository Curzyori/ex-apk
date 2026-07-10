package com.curzyori.exapk.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.curzyori.exapk.data.model.Language
import com.curzyori.exapk.data.model.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class PreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val LANGUAGE = stringPreferencesKey("language")
    }
    
    private val sharedPrefs by lazy {
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        val value = prefs[Keys.THEME_MODE] ?: ThemeMode.AUTO.name
        try {
            ThemeMode.valueOf(value)
        } catch (e: Exception) {
            ThemeMode.AUTO
        }
    }

    val language: Flow<Language> = context.dataStore.data.map { prefs ->
        val value = prefs[Keys.LANGUAGE] ?: Language.EN.name
        try {
            Language.valueOf(value)
        } catch (e: Exception) {
            Language.EN
        }
    }
    
    // Synchronous read for attachBaseContext
    fun getLanguageSync(): Language {
        val value = sharedPrefs.getString("language", Language.EN.name) ?: Language.EN.name
        return try {
            Language.valueOf(value)
        } catch (e: Exception) {
            Language.EN
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = mode.name
        }
    }

    suspend fun setLanguage(lang: Language) {
        context.dataStore.edit { prefs ->
            prefs[Keys.LANGUAGE] = lang.name
        }
        // Also save to SharedPreferences for synchronous read in attachBaseContext
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            .edit()
            .putString("language", lang.name)
            .apply()
    }
}
