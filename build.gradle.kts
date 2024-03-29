buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.0")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.35")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")

    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
    id("com.google.devtools.ksp") version "1.9.0-1.0.12" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
    id ("com.google.dagger.hilt.android") version "2.44" apply false
    id ("androidx.navigation.safeargs.kotlin") version "2.5.0"  apply false
}