object Version {
    const val COMPOSE = "1.2.0"
}

object Plugins {
    const val ANDROID_GRADLE_PLUGIN = "com.android.tools.build:gradle:7.2.2"
    const val HILT_AGP = "com.google.dagger:hilt-android-gradle-plugin:2.42"
    const val KOTLIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.0"
}

object Dependency {
    object AndroidX {
        const val APP_COMPAT = "androidx.appcompat:appcompat:1.5.1"
        const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:2.1.4"
        const val MATERIAL = "com.google.android.material:material:1.6.1"
    }

    object KTX {
        const val CORE = "androidx.core:core-ktx:1.7.0"
    }

    object Compose {
        private const val VERSION = "1.2.0"
        const val ACTIVITY_COMPOSE = "androidx.activity:activity-compose:1.5.0"
        const val ACTIVITY_KTX = "androidx.activity:activity-ktx:1.4.0"

        const val COMPOSE_MATERIAL = "androidx.compose.material:material:$VERSION"
        const val PREVIEW = "androidx.compose.ui:ui-tooling-preview:$VERSION"
        const val RUNTIME = "androidx.compose.runtime:runtime:$VERSION"
        const val UI = "androidx.compose.ui:ui:$VERSION"
        const val UI_TOOLING = "androidx.compose.ui:ui-tooling:$VERSION"
        const val COMPOSE_CONSTRAINT = "androidx.constraintlayout:constraintlayout-compose:1.0.1"
        const val NAVIGATION = "androidx.navigation:navigation-compose:2.5.1"
    }

    object Lifecycle {
        const val VIEWMODEL_COMPOSE = "androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1"
        const val RUNTIME_COMPOSE = "androidx.lifecycle:lifecycle-runtime-compose:2.6.0-alpha01"
    }

    object Paging {
        const val RUNTIME = "androidx.paging:paging-runtime:3.1.1"
        const val COMPOSE = "androidx.paging:paging-compose:1.0.0-alpha15"
    }

    object Accompanist {
        const val SYSTEM_UI_CONTROLLER = "com.google.accompanist:accompanist-systemuicontroller:0.25.1"
        const val PAGER = "com.google.accompanist:accompanist-pager:0.25.1"
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

    object Timber {
        const val TIMBER = "com.jakewharton.timber:timber:5.0.1"
    }

    object Test {
        const val JUNIT = "junit:junit:4.13.2"
    }

    object AndroidTest {
        const val TEST_RUNNER = "androidx.test.ext:junit:1.1.3"
        const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:3.4.0"
    }
}