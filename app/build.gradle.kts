import java.io.FileInputStream
import java.util.*

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.plugin.compose)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.dagger.hilt)
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreFileInputStream = FileInputStream(keystorePropertiesFile)
val keystoreProperties = Properties()
keystoreProperties.load(keystoreFileInputStream)

android {
    compileSdk = 36
    namespace = "com.seno.game"
    defaultConfig {
        applicationId = "com.seno.game"
        minSdk = 26
        targetSdk = 36
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
            signingConfig = signingConfigs.getByName("debug")

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
        compose = true
    }
}

dependencies {
    implementation(project(":opencv"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.paging.runtime)

    // Compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.preview)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storeage)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)

    // Accompanist
    implementation(libs.accompanist.ui.controller)
    implementation(libs.accompanist.pager)

    // Facebook
    implementation(libs.facebook.login)
    implementation(libs.facebook.applink)

    // Naver
    implementation(libs.naver.jdk)

    // Kakao
    implementation(libs.kakao.sdk.all)

    // Google
    implementation(libs.gms.auth)
    implementation(libs.gms.ads)

    // okhttp3
    implementation (libs.okhttp3)
    implementation (libs.okhttp3.logging.interceptor)

    //retrofit2
    implementation (libs.retrofit2)
    implementation (libs.retrofit2.converter.gson)

    //gson
    implementation(libs.gson)

    // Timber
    implementation(libs.timber)

    // Glide
    implementation(libs.glide)
    implementation(libs.glide.compose)
    kapt(libs.glide.compiler)

    // Lottie
    implementation(libs.lottie)
    implementation(libs.lottie.compose)

    // TEST
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Hilt
    implementation(libs.dagger.hilt)
    implementation(libs.dagger.hilt.navigation.compose)
    kapt(libs.dagger.hilt.compiler)

    // Etc
    implementation(libs.easy.prefs)
    implementation(libs.zxing.android.embedded)
    implementation(libs.barcodescanner.zxing)
    implementation(libs.recyclerview.divider)
    implementation(libs.tedpermission.coroutine)
}