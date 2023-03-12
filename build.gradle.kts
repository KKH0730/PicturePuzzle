buildscript {
    extra.apply {
        set("agp_version", "7.3.0")
    }
}// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id(Plugins.ANDROID_GRADLE_PLUGIN) version("7.2.0") apply false
    id(Plugins.LIBRARY) version("7.2.2") apply false
    id(Plugins.KOTLIN) version("1.7.0") apply false
    id(Plugins.HILT_AGP) version("2.44.2") apply false
    id(Plugins.GOOGLE_SERVICE) version("4.3.1") apply false
    id(Plugins.CRASHLYTICS) version("2.9.4") apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}