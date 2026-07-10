package com.curzyori.exapk.data.model

data class AppInfo(
    val packageName: String,
    val appName: String,
    val versionName: String,
    val versionCode: Long,
    val apkPath: String,
    val apkSize: Long,
    val installTime: Long,
    val isSystemApp: Boolean
)

enum class FilterType {
    ALL, USER_APPS, SYSTEM_APPS
}

enum class SortType {
    NAME, SIZE, DATE
}

enum class ThemeMode {
    LIGHT, DARK, AUTO
}

enum class Language {
    EN, ID, CN, JP
}

data class ExtractResult(
    val packageName: String,
    val success: Boolean,
    val message: String? = null,
    val outputPath: String? = null,
    val contentUri: String? = null
)

data class BatchExtractProgress(
    val total: Int,
    val current: Int,
    val extracted: Int,
    val skipped: Int,
    val failed: Int,
    val extractedPaths: List<String> = emptyList()
)
