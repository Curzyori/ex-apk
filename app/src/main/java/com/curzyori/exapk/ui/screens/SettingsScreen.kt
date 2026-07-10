package com.curzyori.exapk.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.curzyori.exapk.R
import com.curzyori.exapk.data.model.Language
import com.curzyori.exapk.data.model.ThemeMode
import com.curzyori.exapk.ui.components.DonateSheet
import com.curzyori.exapk.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val themeMode by viewModel.themeMode.collectAsState()
    val language by viewModel.language.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var languageExpanded by remember { mutableStateOf(false) }
    var themeExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Language Section
            SectionTitle(stringResource(R.string.language))
            LanguageDropdown(
                currentLanguage = language,
                expanded = languageExpanded,
                onExpandedChange = { languageExpanded = it },
                onLanguageSelect = {
                    viewModel.setLanguage(it)
                    languageExpanded = false
                    findActivity(context)?.recreate()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Appearance Section
            SectionTitle(stringResource(R.string.appearance))
            ThemeDropdown(
                currentTheme = themeMode,
                expanded = themeExpanded,
                onExpandedChange = { themeExpanded = it },
                onThemeSelect = {
                    viewModel.setThemeMode(it)
                    themeExpanded = false
                    findActivity(context)?.recreate()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // About Section
            SectionTitle(stringResource(R.string.about))
            AboutItem(
                icon = Icons.Default.Code,
                title = stringResource(R.string.github),
                onClick = { viewModel.openGitHub() }
            )
            AboutItem(
                icon = Icons.Default.Web,
                title = stringResource(R.string.website),
                onClick = { viewModel.openWebsite() }
            )
            AboutItem(
                icon = Icons.Default.Coffee,
                title = stringResource(R.string.support_project),
                onClick = { viewModel.showDonateSheet() }
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(16.dp))

            // Version at bottom
            Text(
                text = "${stringResource(R.string.version_label)} ${stringResource(R.string.app_version)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp),
            )
        }
    }

    // Donate Sheet
    if (uiState.showDonateSheet) {
        DonateSheet(
            onDismiss = { viewModel.hideDonateSheet() },
            onDonateClick = { viewModel.openDonatePage() }
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun LanguageDropdown(
    currentLanguage: Language,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onLanguageSelect: (Language) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Language,
            contentDescription = stringResource(R.string.language),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = stringResource(R.string.language),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Box {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onExpandedChange(true) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (currentLanguage) {
                        Language.EN -> "\uD83C\uDDFA\uD83C\uDDF8 " + stringResource(R.string.language_english)
                        Language.ID -> "\uD83C\uDDEE\uD83C\uDDE7 " + stringResource(R.string.language_indonesian)
                        Language.CN -> "\uD83C\uDDE8\uD83C\uDDF3 " + stringResource(R.string.language_chinese)
                        Language.JP -> "\uD83C\uDDEF\uD83C\uDDF5 " + stringResource(R.string.language_japanese)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) }
            ) {
                DropdownMenuItem(
                    text = { Text("\uD83C\uDDFA\uD83C\uDDF8 " + stringResource(R.string.language_english)) },
                    onClick = { onLanguageSelect(Language.EN) },
                    trailingIcon = {
                        if (currentLanguage == Language.EN) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = stringResource(R.string.cd_selected),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
                DropdownMenuItem(
                    text = { Text("\uD83C\uDDEE\uD83C\uDDE7 " + stringResource(R.string.language_indonesian)) },
                    onClick = { onLanguageSelect(Language.ID) },
                    trailingIcon = {
                        if (currentLanguage == Language.ID) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = stringResource(R.string.cd_selected),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
                DropdownMenuItem(
                    text = { Text("\uD83C\uDDE8\uD83C\uDDF3 " + stringResource(R.string.language_chinese)) },
                    onClick = { onLanguageSelect(Language.CN) },
                    trailingIcon = {
                        if (currentLanguage == Language.CN) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = stringResource(R.string.cd_selected),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
                DropdownMenuItem(
                    text = { Text("\uD83C\uDDEF\uD83C\uDDF5 " + stringResource(R.string.language_japanese)) },
                    onClick = { onLanguageSelect(Language.JP) },
                    trailingIcon = {
                        if (currentLanguage == Language.JP) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = stringResource(R.string.cd_selected),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ThemeDropdown(
    currentTheme: ThemeMode,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onThemeSelect: (ThemeMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.DarkMode,
            contentDescription = stringResource(R.string.dark_mode),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = stringResource(R.string.dark_mode),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Box {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onExpandedChange(true) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (currentTheme) {
                        ThemeMode.LIGHT -> stringResource(R.string.dark_mode_light)
                        ThemeMode.DARK -> stringResource(R.string.dark_mode_dark)
                        ThemeMode.AUTO -> stringResource(R.string.dark_mode_auto)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.dark_mode_light)) },
                    onClick = { onThemeSelect(ThemeMode.LIGHT) },
                    trailingIcon = {
                        if (currentTheme == ThemeMode.LIGHT) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = stringResource(R.string.cd_selected),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.dark_mode_dark)) },
                    onClick = { onThemeSelect(ThemeMode.DARK) },
                    trailingIcon = {
                        if (currentTheme == ThemeMode.DARK) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = stringResource(R.string.cd_selected),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.dark_mode_auto)) },
                    onClick = { onThemeSelect(ThemeMode.AUTO) },
                    trailingIcon = {
                        if (currentTheme == ThemeMode.AUTO) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = stringResource(R.string.cd_selected),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AboutItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

private fun findActivity(context: android.content.Context): android.app.Activity? {
    var ctx = context
    while (ctx is android.content.ContextWrapper) {
        if (ctx is android.app.Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}