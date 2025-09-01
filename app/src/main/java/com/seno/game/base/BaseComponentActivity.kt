package com.seno.game.base

import android.app.Activity
import android.content.Intent
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
import com.seno.game.extensions.getImageDate
import com.seno.game.extensions.parseImageDate
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.view.NewMonthAlertDialog
import timber.log.Timber

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

    override fun onResume() {
        super.onResume()

        if (PrefsManager.recentSinglePlayDate.parseImageDate() != getImageDate()) {
            NewMonthAlertDialog (
                context = this@BaseComposeActivity,
                onConfirm = {
                    PrefsManager.clearSinglePlayData(currentTimeMillis = System.currentTimeMillis())
                    restartApp(this@BaseComposeActivity)
                }).show()
        }
    }
    protected fun restartApp(activity: Activity) {
        val intent = activity.packageManager.getLaunchIntentForPackage(activity.packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        activity.startActivity(intent)
        activity.finish()
        Runtime.getRuntime().exit(0)
    }
}