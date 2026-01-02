# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class com.kakao.sdk.**.model.* { <fields>; }
-keep class * extends com.google.gson.TypeAdapter

# Kakao SDK core
-keep class com.kakao.sdk.** { *; }
-dontwarn com.kakao.sdk.**

# OkHttp (Kakao SDK 내부 사용)
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

# Gson (JSON parsing)
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

# Kotlin Metadata (Reflection 이슈 방지)
-keep class kotlin.Metadata { *; }

# Retrofit (사용 중일 경우)
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**