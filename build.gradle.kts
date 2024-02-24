// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    val kotlin_version = "1.9.22"
    kotlin("kapt") version kotlin_version
    id("com.android.application") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version kotlin_version apply false
    id("com.google.devtools.ksp") version "${kotlin_version}-1.0.17" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
}


allprojects {
    repositories {
        maven("https://jitpack.io")
    }
}