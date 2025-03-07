object Version {
    const val COMPOSE = "1.2.0"
}

object Plugins {
    const val ANDROID_GRADLE_PLUGIN = "com.android.tools.build:gradle:7.2.2"
    const val HILT_AGP = "com.google.dagger:hilt-android-gradle-plugin:2.42"
    const val KOTLIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.0"
    const val KOTLIN_GRADLE = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10"
    const val GOOGLE_SERVICE = "com.google.gms:google-services:4.3.13"
    const val CRASHLYTICS = "com.google.firebase:firebase-crashlytics-gradle:2.9.2"
}

object Dependency {
    object AndroidX {
        const val APP_COMPAT = "androidx.appcompat:appcompat:1.5.1"
        const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:2.1.4"
        const val MATERIAL = "com.google.android.material:material:1.6.1"
        const val RECYCLERVIEW = "androidx.recyclerview:recyclerview:1.2.1"
    }

    object JETBRAINS {
        const val COROUTINE_ANDROID = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1"
        const val COROUTINE_PLAY_SERVICE = "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.1"
    }

    object KTX {
        const val CORE = "androidx.core:core-ktx:1.7.0"
        const val ACTIVITY_KTX = "androidx.activity:activity-ktx:1.4.0"
    }

    object Compose {
        private const val VERSION = "1.2.0"
        const val ACTIVITY_COMPOSE = "androidx.activity:activity-compose:1.5.0"

        const val COMPOSE_MATERIAL = "androidx.compose.material:material:$VERSION"
        const val PREVIEW = "androidx.compose.ui:ui-tooling-preview:$VERSION"
        const val RUNTIME = "androidx.compose.runtime:runtime:$VERSION"
        const val UI = "androidx.compose.ui:ui:$VERSION"
        const val UI_TOOLING = "androidx.compose.ui:ui-tooling:$VERSION"
        const val COMPOSE_CONSTRAINT = "androidx.constraintlayout:constraintlayout-compose:1.0.1"
        const val NAVIGATION = "androidx.navigation:navigation-compose:2.5.1"
    }

    object Firebase {
        const val FIREBASE_BOM = "com.google.firebase:firebase-bom:30.4.1"
        const val FIREBASE_FIRESTORE = "com.google.firebase:firebase-firestore-ktx"
        const val FIREBASE_ANALYTICS = "com.google.firebase:firebase-crashlytics-ktx"
        const val FIREBASE_AUTH = "com.google.firebase:firebase-auth-ktx"
        const val FIREBASE_CRASHLYTICS = "com.google.firebase:firebase-analytics-ktx"
        const val FIREBASE_MESSAGING = "com.google.firebase:firebase-messaging-ktx"
        const val FIREBASE_STORAGE = "com.google.firebase:firebase-storage-ktx"
    }

    object Lifecycle {
        const val lifecycle = "2.4.1"
        const val VIEWMODEL_COMPOSE = "androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1"
        const val RUNTIME_COMPOSE = "androidx.lifecycle:lifecycle-runtime-compose:2.6.0-alpha01"
        const val lifecycleKtx = "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle"
        const val liveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle"
        const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle"
        const val process = "androidx.lifecycle:lifecycle-process:$lifecycle"
    }

    object Paging {
        const val RUNTIME = "androidx.paging:paging-runtime:3.1.1"
        const val COMPOSE = "androidx.paging:paging-compose:1.0.0-alpha15"
    }

    object Facebook {
        private const val VERSION = "14.1.1"
        const val FACEBOOK_LOGIN = "com.facebook.android:facebook-login:$VERSION"
        const val FACEBOOK_APP_LINK = "com.facebook.android:facebook-applinks:$VERSION"
    }

    object Naver {
        const val NAVER_JDK8 = "com.navercorp.nid:oauth-jdk8:5.2.0"
    }

    object Kakao {
        const val KAKAO_SDK_ALL = "com.kakao.sdk:v2-all:2.11.2"
        const val KAKAO_SDK_ALL_RX = "com.kakao.sdk:v2-all-rx:2.11.2"
    }

    object Google {
        const val PLAY_SERVICE_AUTH = "com.google.android.gms:play-services-auth:18.0.0"
        const val PLAY_SERVICE_ADS = "com.google.android.gms:play-services-ads:21.3.0"
    }

    object Accompanist {
        private const val VERSION = "0.25.1"
        const val SYSTEM_UI_CONTROLLER = "com.google.accompanist:accompanist-systemuicontroller:$VERSION"
        const val PAGER = "com.google.accompanist:accompanist-pager:$VERSION"
    }

    object Hilt {
        private const val VERSION = "2.42"
        const val ANDROID = "com.google.dagger:hilt-android:$VERSION"
        const val COMPILER = "com.google.dagger:hilt-android-compiler:$VERSION"
        const val TESTING = "com.google.dagger:hilt-android-testing:$VERSION"

        const val HILT_NAVIGATION = "androidx.hilt:hilt-navigation-compose:1.0.0"
    }

    object OkHttp {
        const val LOGGING_INTERCEPTOR = "com.squareup.okhttp3:logging-interceptor:4.10.0"
    }

    object Retrofit {
        private const val VERSION = "2.9.0"
        const val RETROFIT = "com.squareup.retrofit2:retrofit:$VERSION"
        const val GSON_CONVERTER = "com.squareup.retrofit2:converter-gson:$VERSION"
    }

    object Rx {
        const val RXJAVA = "io.reactivex.rxjava2:rxjava:2.2.21"
        const val RXANDROID = "io.reactivex.rxjava2:rxandroid:2.1.1"
        const val RXKOTLIN = "io.reactivex.rxjava2:rxkotlin:2.4.0"
    }

    object Timber {
        const val TIMBER = "com.jakewharton.timber:timber:5.0.1"
    }

    object Glide {
        private const val VERSION = "4.12.0"
        const val GLIDE = "com.github.bumptech.glide:glide:$VERSION"
        const val GLIDE_COMPILER = "com.github.bumptech.glide:compiler:$VERSION"

        private const val VERSION_COMPOSE = "1.4.7"
        const val GLIDE_COMPOSE = "com.github.skydoves:landscapist-glide:$VERSION_COMPOSE"
    }

    object Etc {
        const val EASY_PREFS = "com.pixplicity.easyprefs:EasyPrefs:1.10.0"
        const val QR = "com.journeyapps:zxing-android-embedded:3.6.0"
        const val LOTTIE = "com.airbnb.android:lottie:5.2.0"
        const val COMPOSE_LOTTIE = "com.airbnb.android:lottie-compose:5.2.0"
        const val RECYCLERVIEW_DIVIDER = "com.github.fondesa:recycler-view-divider:3.0.0"
    }

    object Test {
        const val JUNIT = "junit:junit:4.13.2"
    }

    object AndroidTest {
        const val TEST_RUNNER = "androidx.test.ext:junit:1.1.3"
        const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:3.4.0"
    }
}