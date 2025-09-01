package com.seno.game.base

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
}