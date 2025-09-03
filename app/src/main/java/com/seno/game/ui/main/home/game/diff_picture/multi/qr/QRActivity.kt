package com.seno.game.ui.main.home.game.diff_picture.multi.qr

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.zxing.Result
import com.seno.game.R
import com.seno.game.extensions.createQRCode
import com.seno.game.extensions.startActivity
import com.seno.game.extensions.textDp
import com.seno.game.extensions.toJson
import com.seno.game.manager.AccountManager
import com.seno.game.model.Player
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.main.home.component.QuitDialogNoButton
import com.seno.game.ui.main.home.component.QuitDialogYesButton
import com.seno.game.ui.main.home.game.diff_picture.multi.multi_game.DPMultiPlayActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.dm7.barcodescanner.zxing.ZXingScannerView

@AndroidEntryPoint
class QRScanActivity : ComponentActivity(), ZXingScannerView.ResultHandler {
    private val viewModel by viewModels<QRScanViewModel>()
    private val scannerView: ZXingScannerView by lazy { ZXingScannerView(this@QRScanActivity) }
    private val isScanModel: Boolean by lazy { intent.getBooleanExtra(IS_SCAN_MODE, false) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = false
        }

        if (isScanModel) {
            setContentView(scannerView)
        } else {
            setContent {
                QRScanScreen()
            }
        }

        if (!isScanModel) {
            this.setFinishOnTouchOutside(false)
            setBackPressedEvent()
        }
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
                    viewModel.startMultiGame.collectLatest { DPMultiPlayActivity.start(context = this@QRScanActivity) }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isScanModel) {
            scannerView.setResultHandler(this@QRScanActivity)
            scannerView.startCamera()
        }
    }

    override fun onPause() {
        super.onPause()
        if (isScanModel) scannerView.stopCamera()
    }

    private fun setBackPressedEvent() {
        onBackPressedDispatcher.addCallback(this@QRScanActivity, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        })
    }

    override fun handleResult(result: Result?) {
        val list = result?.text?.split("|") ?: listOf()
        if (list.isEmpty() || list.size != 2) {
            Toast.makeText(this@QRScanActivity, getString(R.string.please_retry), Toast.LENGTH_SHORT).show()
            scannerView.resumeCameraPreview(this@QRScanActivity)
        } else {
            viewModel.createMultiGame(list[0], list[1])
        }
    }

    @Composable
    fun QRScanScreen() {
        var isShowQuitDialog by remember { mutableStateOf(false) }

        if (isShowQuitDialog) {
            QuitDialog(
                onClickYes = { finish() },
                onClickNo = { isShowQuitDialog = false },
                onDismissed = {}
            )
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(color = colorResource(id = R.color.transparent))
        ) {
            Box(modifier = Modifier.Companion.size(200.dp)) {
                val player = Player(
                    uid = AccountManager.firebaseUid,
                    nickName = PrefsManager.nickname,
                    profileUri = PrefsManager.profileUri
                )

                "${AccountManager.firebaseUid}_${intent.getStringExtra(CURRENT_TIME_MILLIS)}|${player.toJson()}".createQRCode()?.let {
                    BitmapImageDisplay(it)
                }
            }
            Spacer(modifier = Modifier.height(height = 24.dp))
            Text(
                stringResource(id = R.string.qr_guide_message),
                style = TextStyle(
                    fontSize = 18.textDp,
                    fontWeight = FontWeight.W500,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            )
            Spacer(modifier = Modifier.height(height = 72.dp))
            Box(
                modifier = Modifier
                    .size(size = 48.dp)
                    .border(
                        border = BorderStroke(
                            width = 2.dp,
                            color = Color.White
                        ),
                        shape = CircleShape
                    )
                    .clickable { isShowQuitDialog = true }
            ) {
                Icon(
                    Icons.Filled.Close,
                    tint = Color.White,
                    contentDescription = null,
                    modifier = Modifier
                        .size(size = 36.dp)
                        .align(alignment = Alignment.Center)
                )
            }
        }
    }

    @Composable
    fun BitmapImageDisplay(bitmap: Bitmap) {
        Image(
            painter = BitmapPainter(bitmap.asImageBitmap()),
            contentDescription = "Bitmap Image",
            modifier = Modifier.Companion
                .width(200.dp)
                .height(200.dp),
            contentScale = ContentScale.Companion.Crop // 필요에 따라 변경 가능
        )
    }
    @Composable
    fun QuitDialog(
        onClickYes: () -> Unit,
        onClickNo: () -> Unit,
        onDismissed: () -> Unit
    ) {
        Dialog(onDismissRequest = onDismissed) {
            Card(
                backgroundColor = Color.White,
                shape = RoundedCornerShape(size = 30.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                ) {
                    Spacer(modifier = Modifier.height(height = 35.dp))
                    Image(
                        painter = painterResource(id = R.drawable.ic_dialog_cat_crying),
                        contentDescription = null,
                        modifier = Modifier
                            .width(width = 56.dp)
                            .height(height = 59.dp)
                    )
                    Spacer(modifier = Modifier.height(height = 23.dp))
                    Text(
                        text = stringResource(id = R.string.qr_dialog_message),
                        color = colorResource(id = R.color.color_b8c0ff),
                        style = TextStyle(
                            fontSize = 16.textDp,
                            textAlign = TextAlign.Center
                        ),
                        softWrap = true

                    )
                    Spacer(modifier = Modifier.height(height = 28.dp))
                    Row {
                        Spacer(modifier = Modifier.width(width = 10.dp))
                        QuitDialogNoButton(text = stringResource(id = R.string.home_quit_n),onClick = onClickNo)
                        QuitDialogYesButton(text = stringResource(id = R.string.home_logout_y), onClick = onClickYes)
                        Spacer(modifier = Modifier.width(width = 10.dp))
                    }
                    Spacer(modifier = Modifier.height(height = 25.dp))
                }
            }
        }
    }


    companion object {
        const val IS_SCAN_MODE = "isScanMode"
        const val CURRENT_TIME_MILLIS = "currentTimeMillis"

        fun start(context: Context, isScanMode: Boolean, currentTimeMillis: String) {
            context.startActivity(QRScanActivity::class.java) {
                putExtra(IS_SCAN_MODE, isScanMode)
                putExtra(CURRENT_TIME_MILLIS, currentTimeMillis)
            }
        }
    }
}