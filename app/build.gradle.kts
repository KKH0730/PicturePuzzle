plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "com.seno.game"
        minSdk = 21
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.2.0"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        compose = true
    }
}

dependencies {
    implementation(project(":opencv"))

    //AndroidX
    implementation(Dependency.AndroidX.APP_COMPAT)
    implementation(Dependency.AndroidX.MATERIAL)
    implementation(Dependency.AndroidX.CONSTRAINT_LAYOUT)

    //KTX
    implementation(Dependency.KTX.CORE)

    // Compose
    implementation(Dependency.Compose.ACTIVITY_COMPOSE)
    implementation(Dependency.Compose.COMPOSE_MATERIAL)
    implementation(Dependency.Compose.RUNTIME)
    implementation(Dependency.Compose.UI)
    implementation(Dependency.Compose.PREVIEW)
    implementation(Dependency.Hilt.HILT_NAVIGATION)
    implementation(Dependency.Lifecycle.VIEWMODEL_COMPOSE)
    implementation(Dependency.Lifecycle.RUNTIME_COMPOSE)
    implementation(Dependency.Paging.COMPOSE)
    implementation(Dependency.Compose.COMPOSE_CONSTRAINT)
    implementation(Dependency.Compose.NAVIGATION)
    debugImplementation(Dependency.Compose.UI_TOOLING)

    // Accompanist
    implementation(Dependency.Accompanist.SYSTEM_UI_CONTROLLER)
    implementation(Dependency.Accompanist.PAGER)

    // Retrofit
    implementation(Dependency.Retrofit.RETROFIT)
    implementation(Dependency.Retrofit.GSON_CONVERTER)
    implementation(Dependency.OkHttp.LOGGING_INTERCEPTOR)

    // Timber
    implementation(Dependency.Timber.TIMBER)

    //TEST
    testImplementation(Dependency.Test.JUNIT)
    androidTestImplementation(Dependency.AndroidTest.TEST_RUNNER)
    androidTestImplementation(Dependency.AndroidTest.ESPRESSO_CORE)

    // Hilt
    implementation(Dependency.Hilt.ANDROID)
    kapt(Dependency.Hilt.COMPILER)
    testImplementation(Dependency.Hilt.TESTING)
}