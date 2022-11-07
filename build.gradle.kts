buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath(Plugins.ANDROID_GRADLE_PLUGIN)
        classpath(Plugins.KOTLIN)
        classpath(Plugins.HILT_AGP)
        classpath(Plugins.KOTLIN_GRADLE)
        classpath(Plugins.GOOGLE_SERVICE)
        classpath(Plugins.CRASHLYTICS)
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}