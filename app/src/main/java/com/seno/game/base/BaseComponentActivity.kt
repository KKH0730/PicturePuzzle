package com.seno.game.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.WindowInsetsControllerCompat

abstract class BaseComposeActivity(
    private val isLightStatusBar: Boolean,
    private val isLightNavigationBar: Boolean
) : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = isLightStatusBar
            isAppearanceLightNavigationBars = isLightNavigationBar
        }
        setContent {
            BaseContent {
                ComposeContent()
            }
        }
    }

    @Composable
    private fun BaseContent(content: @Composable () -> Unit) {
        val insets = WindowInsets.systemBars.asPaddingValues()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
//                    top = insets.calculateTopPadding(),
//                    bottom = insets.calculateBottomPadding(),
                    start = insets.calculateStartPadding(LayoutDirection.Ltr),
                    end = insets.calculateEndPadding(LayoutDirection.Ltr)
                )
        ) {
            content()
        }
    }

    @Composable
    abstract fun ComposeContent()
}