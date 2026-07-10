package com.curzyori.exapk.data.source

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.curzyori.exapk.data.model.AppInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PackageManagerSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val pm: PackageManager = context.packageManager

    fun getInstalledApps(includeSystem: Boolean = true): List<AppInfo> {
        return try {
            val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            packages
                .filter { includeSystem || (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
                .flatMap { appInfo ->
                    try {
                        val packageInfo = pm.getPackageInfo(appInfo.packageName, 0)

                        listOf(AppInfo(
                            packageName = appInfo.packageName,
                            appName = appInfo.loadLabel(pm).toString(),
                            versionName = packageInfo.versionName ?: "Unknown",
                            versionCode = packageInfo.longVersionCode,
                            apkPath = appInfo.sourceDir,
                            apkSize = java.io.File(appInfo.sourceDir).length(),
                            installTime = packageInfo.firstInstallTime,
                            isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                        ))
                    } catch (e: Exception) {
                        emptyList()
                    }
                }
                .sortedBy { it.appName.lowercase() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getAppIcon(packageName: String): Drawable? {
        return try {
            pm.getApplicationIcon(packageName)
        } catch (e: Exception) {
            null
        }
    }
}
