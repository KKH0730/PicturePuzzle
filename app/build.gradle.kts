import java.io.FileInputStream
import java.util.*

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("dagger.hilt.android.plugin")
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreFileInputStream = FileInputStream(keystorePropertiesFile)
val keystoreProperties = Properties()
keystoreProperties.load(keystoreFileInputStream)

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "com.seno.game"
        minSdk = 23
        targetSdk = 32
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    signingConfigs {
        create("release") {
            storeFile = keystoreProperties["storeFile"]?.let { file(it) }
            storePassword = keystoreProperties["storePassword"] as String?
            keyAlias = keystoreProperties["keyAlias"] as String?
            keyPassword = keystoreProperties["keyPassword"] as String?
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            manifestPlaceholders["enableCrashReporting"] = false
            configure<com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension> {
                // If you don't need crash reporting for your debug build,
                // you can speed up your build by disabling mapping file uploading.
                mappingFileUploadEnabled = false
            }
            // crashlytics 플러그인을 사용하지 않음
            extra.set("enableCrashlytics", false)
            // crashlytics 빌드 ID 업데이트 막기
            extra.set("alwaysUpdateBuildId", false)
        }

        getByName("release") {
            signingConfig = signingConfigs.getByName("release")

            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            manifestPlaceholders["enableCrashReporting"] = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=compatibility")
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

    // AndroidX
    implementation(Dependency.AndroidX.APP_COMPAT)
    implementation(Dependency.AndroidX.MATERIAL)
    implementation(Dependency.AndroidX.CONSTRAINT_LAYOUT)
    implementation(Dependency.AndroidX.RECYCLERVIEW)

    // KTX
    implementation(Dependency.KTX.CORE)
    implementation(Dependency.KTX.ACTIVITY_KTX)

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

    // Firebase
    implementation(platform(Dependency.Firebase.FIREBASE_BOM))
    implementation(Dependency.Firebase.FIREBASE_FIRESTORE)
    implementation(Dependency.Firebase.FIREBASE_ANALYTICS)
    implementation(Dependency.Firebase.FIREBASE_AUTH)
    implementation(Dependency.Firebase.FIREBASE_CRASHLYTICS)
    implementation(Dependency.Firebase.FIREBASE_MESSAGING)
    implementation(Dependency.Firebase.FIREBASE_STORAGE)

    // Jetbrains
    implementation(Dependency.JETBRAINS.COROUTINE_ANDROID)
    implementation(Dependency.JETBRAINS.COROUTINE_PLAY_SERVICE)

    // Accompanist
    implementation(Dependency.Accompanist.SYSTEM_UI_CONTROLLER)
    implementation(Dependency.Accompanist.PAGER)

    // Facebook
    implementation(Dependency.Facebook.FACEBOOK_LOGIN)
    implementation(Dependency.Facebook.FACEBOOK_APP_LINK)

    // Naver
    implementation(Dependency.Naver.NAVER_JDK8)

    // Kakao
    implementation(Dependency.Kakao.KAKAO_SDK_ALL_RX)

    // Google
    implementation(Dependency.Google.PLAY_SERVICE_AUTH)
    implementation(Dependency.Google.PLAY_SERVICE_ADS)

    // Retrofit
    implementation(Dependency.Retrofit.RETROFIT)
    implementation(Dependency.Retrofit.GSON_CONVERTER)
    implementation(Dependency.OkHttp.LOGGING_INTERCEPTOR)

    // RX
    implementation(Dependency.Rx.RXJAVA)
    implementation(Dependency.Rx.RXANDROID)
    implementation(Dependency.Rx.RXKOTLIN)

    // Timber
    implementation(Dependency.Timber.TIMBER)

    // Glide
    implementation(Dependency.Glide.GLIDE)
    implementation(Dependency.Glide.GLIDE_COMPILER)
    implementation(Dependency.Glide.GLIDE_COMPOSE)

    // Etc
    implementation(Dependency.Etc.EASY_PREFS)
    implementation(Dependency.Etc.QR)
    implementation(Dependency.Etc.LOTTIE)
    implementation(Dependency.Etc.COMPOSE_LOTTIE)
    implementation(Dependency.Etc.RECYCLERVIEW_DIVIDER)

    // TEST
    testImplementation(Dependency.Test.JUNIT)
    androidTestImplementation(Dependency.AndroidTest.TEST_RUNNER)
    androidTestImplementation(Dependency.AndroidTest.ESPRESSO_CORE)

    // Hilt
    implementation(Dependency.Hilt.ANDROID)
    kapt(Dependency.Hilt.COMPILER)
    testImplementation(Dependency.Hilt.TESTING)
}