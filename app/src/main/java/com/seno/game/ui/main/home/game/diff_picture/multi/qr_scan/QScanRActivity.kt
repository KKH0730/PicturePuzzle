package com.seno.game.ui.main.home.game.diff_picture.multi.qr_scan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.zxing.Result
import com.seno.game.R
import com.seno.game.extensions.startActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.dm7.barcodescanner.zxing.ZXingScannerView

@AndroidEntryPoint
class QRScanActivity : ComponentActivity(), ZXingScannerView.ResultHandler {
    private val viewModel by viewModels<QRScanViewModel>()
    private val scannerView: ZXingScannerView by lazy { ZXingScannerView(this@QRScanActivity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = false
        }

        setContentView(scannerView)
        startObserve()
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

    override fun onResume() {
        super.onResume()
        scannerView.setResultHandler(this@QRScanActivity)
        scannerView.startCamera()
    }

    override fun onPause() {
        super.onPause()
        scannerView.stopCamera()
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
            context.startActivity(QRScanActivity::class.java, launcher)
        }
    }
}