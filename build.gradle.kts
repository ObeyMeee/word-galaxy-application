// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    val kotlin_version = "1.9.22"
    id("com.android.application") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version kotlin_version apply false
    id("com.google.devtools.ksp") version "${kotlin_version}-1.0.17" apply false
    kotlin("kapt") version kotlin_version
    id("com.google.dagger.hilt.android") version "2.50" apply false
}