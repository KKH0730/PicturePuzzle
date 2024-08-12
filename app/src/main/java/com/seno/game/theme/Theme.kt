package com.seno.game.theme

import android.content.ContextWrapper
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pixplicity.easyprefs.library.Prefs

private val appColors = lightColors(
    primary = Color.White,
    primaryVariant = Color.White,
    secondary = Color.Black,
    secondaryVariant = Color.Black,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(colors = appColors) {
        val systemUiController = rememberSystemUiController()
        val useDarkIcons = MaterialTheme.colors.isLight

        SideEffect {
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = useDarkIcons
            )
        }


        content()
    }
}

@Composable
fun PreviewAppTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    Prefs.Builder()
        .setContext(context)
        .setMode(ContextWrapper.MODE_PRIVATE)
        .setPrefsName(context.packageName)
        .setUseDefaultSharedPreference(true)
        .build()
    AppTheme(content = content)
}