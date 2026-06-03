// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}
buildscript {
    repositories {
        maven(url = "https://maven.zohodl.com/")
        mavenLocal()

    }
    dependencies {
        classpath(libs.apptics.plugin)
    }
}