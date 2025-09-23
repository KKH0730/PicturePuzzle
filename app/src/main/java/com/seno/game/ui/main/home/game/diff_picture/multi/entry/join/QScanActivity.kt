package com.seno.game.ui.main.home.game.diff_picture.multi.entry.join

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.zxing.Result
import com.seno.game.R
import com.seno.game.extensions.safeStartActivity
import com.seno.game.ui.base.BaseComposeActivity
import com.seno.game.ui.main.home.game.diff_picture.multi.entry.join.component.QRScanFooter
import com.seno.game.ui.main.home.game.diff_picture.multi.entry.join.component.QRScanHeader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.dm7.barcodescanner.zxing.ZXingScannerView
import timber.log.Timber

@AndroidEntryPoint
class QRScanActivity : BaseComposeActivity(
    isLightStatusBar = true,
    isLightNavigationBar = false
), ZXingScannerView.ResultHandler {
    private val viewModel by viewModels<QRScanViewModel>()
    private val scannerView: ZXingScannerView by lazy {
        ZXingScannerView(this@QRScanActivity).apply {
            setLaserEnabled(false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = false
        }

        setBackPressedEvent()
        startObserve()
    }

    @Composable
    override fun ComposeContent() {
        val insets = WindowInsets.systemBars.asPaddingValues()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(R.color.black))
                .padding(top = insets.calculateTopPadding(), bottom = insets.calculateBottomPadding())
        ) {
            QRScanHeader(
                onClickBack = { finish() }
            )
            AndroidView(
                factory = { context -> scannerView },
                update = { scannerView -> scannerView },
                modifier = Modifier.weight(weight = 1f)
            )
            QRScanFooter(
                onClickFlash = { scannerView.toggleFlash() },
                onClickRefocus = { scannerView.resumeCameraPreview(this@QRScanActivity) }
            )
        }
    }

    private fun startObserve() {
        lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.CREATED) {
                launch {
                    viewModel.toast.collectLatest { Toast.makeText(this@QRScanActivity, it, Toast.LENGTH_SHORT).show() }
                }

                launch {
                    viewModel.resumeCameraPreview.collectLatest { scannerView.resumeCameraPreview(this@QRScanActivity) }
                }

                launch {
                    viewModel.moveWaitingRoom.collectLatest { path ->
                        val intent = Intent().apply {
                            putExtra("path", path)
                        }
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                }
            }
        }
    }

    private fun setBackPressedEvent() {
        onBackPressedDispatcher.addCallback(this@QRScanActivity, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            scannerView.setResultHandler(this@QRScanActivity)
            scannerView.startCamera()
            delay(200)
            scannerView.resumeCameraPreview(this@QRScanActivity)
        }
    }

    override fun onPause() {
        super.onPause()
        scannerView.stopCamera()
    }

    override fun onDestroy() {
        scannerView.stopCamera()
        scannerView.setResultHandler(null)
        super.onDestroy()
    }

    override fun handleResult(result: Result?) {
        val list = result?.text?.split("_") ?: listOf()
        if (list.isEmpty() || list.size != 2) {
            Toast.makeText(this@QRScanActivity, getString(R.string.please_retry), Toast.LENGTH_SHORT).show()
            scannerView.resumeCameraPreview(this@QRScanActivity)
        } else {
            viewModel.checkWaitingRoom(list[0], list[1])
        }
    }

    companion object {
        fun start(context: Context, launcher: ActivityResultLauncher<Intent>) {
            context.safeStartActivity(QRScanActivity::class.java, launcher)
        }
    }
}