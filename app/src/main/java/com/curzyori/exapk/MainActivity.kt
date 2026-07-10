package com.curzyori.exapk

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.curzyori.exapk.data.model.Language
import com.curzyori.exapk.ui.screens.MainScreen
import com.curzyori.exapk.ui.screens.SettingsScreen
import com.curzyori.exapk.ui.theme.ExAPKTheme
import com.curzyori.exapk.ui.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        // Apply saved language locale before activity is created
        val language = getSavedLanguage(newBase)
        val context = applyLocale(newBase, language)
        super.attachBaseContext(context)
    }
    
    private fun getSavedLanguage(context: Context): Language {
        return try {
            val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            val value = prefs.getString("language", Language.EN.name) ?: Language.EN.name
            Language.valueOf(value)
        } catch (e: Exception) {
            Language.EN
        }
    }
    
    private fun applyLocale(context: Context, language: Language): Context {
        val locale = when (language) {
            Language.EN -> java.util.Locale.US
            Language.ID -> java.util.Locale("id", "ID")
            Language.CN -> java.util.Locale("zh", "CN")
            Language.JP -> java.util.Locale("ja", "JP")
        }
        
        java.util.Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        
        val resources = context.resources
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
        
        return context.createConfigurationContext(config)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val themeMode by settingsViewModel.themeMode.collectAsState()

            ExAPKTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "main"
                    ) {
                        composable("main") {
                            MainScreen(
                                onNavigateToSettings = {
                                    navController.navigate("settings")
                                }
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
