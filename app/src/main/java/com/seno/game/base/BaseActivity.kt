package com.seno.game.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.seno.game.extensions.getImageDate
import com.seno.game.extensions.parseImageDate
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.view.NewMonthAlertDialog


abstract class BaseActivity<T: ViewDataBinding>(
    @LayoutRes private val layoutResId: Int,
    private val isLightStatusBar: Boolean,
    private val isLightNavigationBar: Boolean
): AppCompatActivity() {
    private var _binding: T? = null
    protected val binding: T
        get() = checkNotNull(_binding) {
            "Activity $this binding cannot be accessed before onCreate()"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = DataBindingUtil.setContentView(this, layoutResId)

        enableEdgeToEdge()
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = isLightStatusBar
            isAppearanceLightNavigationBars = isLightNavigationBar
        }
    }

    protected fun setupWindowInsets(targetView: View, onApplyWindowInsets: (androidx.core.graphics.Insets) -> Unit) {
        ViewCompat.setOnApplyWindowInsetsListener(targetView) { v: View, insets: WindowInsetsCompat? ->
            insets?.getInsets(WindowInsetsCompat.Type.systemBars())?.let { systemBarInsets ->
                onApplyWindowInsets.invoke(systemBarInsets)
            }
            insets ?: WindowInsetsCompat.CONSUMED
        }
    }

    override fun onResume() {
        super.onResume()

        if (PrefsManager.recentSinglePlayDate.parseImageDate() != getImageDate()) {
            NewMonthAlertDialog (
                context = this@BaseActivity,
                onConfirm = {
                    PrefsManager.clearSinglePlayData(currentTimeMillis = System.currentTimeMillis())
                    restartApp(this@BaseActivity)
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