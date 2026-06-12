# AndroidX
-keep class androidx.** { *; }
-dontwarn androidx.**

# CameraX
-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

# Lifecycle
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.lifecycle.**

# Kotlin
-keep class kotlin.** { *; }
-dontwarn kotlin.**

# Timber Logging
-keep class timber.log.Timber { *; }

# Myra AI App
-keep class com.myra.ai.** { *; }
-keepclassmembers class com.myra.ai.** { *; }

# Keep all service classes
-keep class com.myra.ai.service.** { *; }
-keep class com.myra.ai.receiver.** { *; }
-keep class com.myra.ai.util.** { *; }

# GSON
-keepclassmembers class * { *; }
-keep class com.google.gson.** { *; }
