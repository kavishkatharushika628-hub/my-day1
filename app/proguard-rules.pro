# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Room Database keep rules
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep class * extends androidx.room.RoomDatabase {
    <methods>;
}
-keep class com.example.data.** { *; }

# Kotlin coroutines and reflection
-dontwarn kotlinx.coroutines.**

