import com.android.build.api.dsl.Packaging

plugins {
    id("com.android.library")
}

android {
    compileSdk = 26
    namespace = "org.opencv"
    defaultConfig {
        minSdk = 21

        externalNativeBuild {
            cmake {
                arguments("-DANDROID_STL=c++_shared")
                targets("opencv_jni_shared")
            }
        }
    }

    buildTypes {
        getByName("debug") {
            fun Packaging.() {
                jniLibs.keepDebugSymbols.add("**/*.so")
            }
        }
        getByName("release") {
            fun Packaging.() {
                jniLibs.keepDebugSymbols.add("**/*.so")
            }
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.txt"
            )
        }
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("native/libs")
            java.srcDirs("java/src")
            aidl.srcDirs("java/src")
            res.srcDirs("java/res")
            manifest.srcFile("java/AndroidManifest.xml")
        }
    }

    externalNativeBuild {
        cmake {
            path = file("${project.projectDir}/libcxx_helper/CMakeLists.txt")
        }
    }

    buildFeatures {
        aidl = true
        buildConfig = true
    }
}