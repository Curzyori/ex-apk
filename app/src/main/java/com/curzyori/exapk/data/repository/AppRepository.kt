package com.curzyori.exapk.data.repository

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.curzyori.exapk.data.model.AppInfo
import com.curzyori.exapk.data.model.BatchExtractProgress
import com.curzyori.exapk.data.model.ExtractResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val outputDir = "ExAPK"

    fun extractApkFlow(apps: List<AppInfo>): Flow<BatchExtractProgress> = flow {
        val total = apps.size
        var extracted = 0
        var skipped = 0
        var failed = 0
        val allExtractedPaths = mutableListOf<String>()

        apps.forEachIndexed { index, app ->
            val result = extractSingleApkInternal(app)
            when {
                result.success -> {
                    extracted++
                    if (result.contentUri != null) allExtractedPaths.add(result.contentUri)
                }
                result.message == "skipped" -> skipped++
                else -> failed++
            }

            emit(
                BatchExtractProgress(
                    total = total,
                    current = index + 1,
                    extracted = extracted,
                    skipped = skipped,
                    failed = failed,
                    extractedPaths = allExtractedPaths.toList()
                )
            )
        }
    }.flowOn(Dispatchers.IO)

    private fun generateFileName(app: AppInfo): String {
        val safeAppName = app.appName
            .lowercase()
            .replace(" ", "_")
            .replace(Regex("[^a-zA-Z0-9_]"), "")
        val safeVersionName = app.versionName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        return "${safeAppName}_${safeVersionName}.apk"
    }

    private fun extractSingleApkInternal(app: AppInfo): ExtractResult {
        return try {
            val sourceFile = File(app.apkPath)
            
            // Check if file exists and is readable
            if (!sourceFile.exists()) {
                return ExtractResult(
                    packageName = app.packageName,
                    success = false,
                    message = "skipped"
                )
            }
            
            if (!sourceFile.canRead()) {
                return ExtractResult(
                    packageName = app.packageName,
                    success = false,
                    message = "skipped"
                )
            }

            val fileName = generateFileName(app)

            val (outputPath, contentUri) = saveToDownloads(sourceFile, fileName)

            ExtractResult(
                packageName = app.packageName,
                success = true,
                outputPath = outputPath,
                contentUri = contentUri
            )
        } catch (e: SecurityException) {
            ExtractResult(
                packageName = app.packageName,
                success = false,
                message = "Permission denied: ${e.message}"
            )
        } catch (e: Exception) {
            ExtractResult(
                packageName = app.packageName,
                success = false,
                message = e.message ?: "Unknown error"
            )
        }
    }

    private fun saveToDownloads(sourceFile: File, fileName: String): Pair<String, String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Use MediaStore for Android 10+
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/vnd.android.package-archive")
                put(MediaStore.Downloads.RELATIVE_PATH, "Download/$outputDir")
            }

            val uri = context.contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                contentValues
            ) ?: throw Exception("Failed to create file")

            val outputStream = context.contentResolver.openOutputStream(uri)
                ?: throw Exception("Cannot open output stream for $fileName")

            outputStream.use { os ->
                FileInputStream(sourceFile).use { input ->
                    input.copyTo(os)
                }
            }

            Pair("Download/$outputDir/$fileName", uri.toString())
        } else {
            // Legacy approach
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val exapkDir = File(downloadsDir, outputDir)
            if (!exapkDir.exists() && !exapkDir.mkdirs()) {
                throw Exception("Failed to create ExAPK directory")
            }

            val destFile = File(exapkDir, fileName)
            if (!destFile.exists() && !destFile.createNewFile()) {
                throw Exception("Failed to create APK file")
            }

            sourceFile.copyTo(destFile, overwrite = true)

            Pair(destFile.absolutePath, destFile.absolutePath)
        }
    }
}
