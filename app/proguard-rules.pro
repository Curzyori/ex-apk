# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep data classes
-keep class com.curzyori.exapk.data.model.** { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Hilt & Dagger
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Compose
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

