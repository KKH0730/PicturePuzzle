package com.seno.game.ui.main.home.game.diff_picture.multi.qr

import androidx.lifecycle.SavedStateHandle
import com.seno.game.R
import com.seno.game.domain.usecase.diff_game.DiffPictureUseCase
import com.seno.game.extensions.fromJson
import com.seno.game.extensions.getString
import com.seno.game.extensions.toMap
import com.seno.game.manager.AccountManager
import com.seno.game.model.Player
import com.seno.game.model.isSuccess
import com.seno.game.model.successData
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.base.BaseViewModel
import com.seno.game.ui.main.home.game.diff_picture.multi.model.MultiGameProfileInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class QRScanViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val diffPictureUseCase: DiffPictureUseCase,
) : BaseViewModel() {
    var isScanMode: Boolean = savedStateHandle[QRScanActivity.IS_SCAN_MODE] ?: false
    var currentTimeMillis: String = savedStateHandle[QRScanActivity.CURRENT_TIME_MILLIS] ?: ""

    private val _resumeCameraPreview = MutableSharedFlow<Unit>()
    val resumeCameraPreview get() = _resumeCameraPreview.asSharedFlow()

    private val _startMultiGame = MutableSharedFlow<MultiGameProfileInfo>()
    val startMultiGame get() = _startMultiGame.asSharedFlow()

    private val path: String get() = "${AccountManager.firebaseUid}_${currentTimeMillis}"

    init {
        vmScopeJob {
            if (!isScanMode) {
                diffPictureUseCase.observeMultiGameSnapshot(path = path)
                    .collect { result ->
                        val data = result.successData()
                        if (data != null) {
                            val ready = data.first
                            val multiGameProfileInfo = data.second

                            if (ready) {
                                _startMultiGame.emit(multiGameProfileInfo)
                            } else {
                                diffPictureUseCase.updateMultiGame(path = path)
                            }
                        } else {
                            showToast(getString(R.string.qr_network_error))
                        }
                    }
            }
        }
    }

    fun resumeCamera() {
        vmScopeJob { _resumeCameraPreview.emit(Unit) }
    }

    fun createMultiGame(path: String, jsonString: String) {
        val player = jsonString.fromJson(Player::class.java)

        if (player == null) {
            showToast(getString(R.string.network_error))
            resumeCamera()
            return
        }

        observeGuestMultiGame(path = path)

        vmScopeJob {
            val result = diffPictureUseCase.createMultiGame(
                path = path,
                hostUid = player.uid,
                hostNickName = player.nickName,
                hostProfileUri = player.profileUri,
                guestUid = AccountManager.firebaseUid,
                guestNickName = PrefsManager.nickname,
                guestProfileUri = PrefsManager.profileUri
            )

            if (result.isSuccess()) {
                if (result.successData() == true) {
//                    _startMultiGame.emit(
//                        MultiGameProfileInfo(
//                            path = "${AccountManager.firebaseUid}_${currentTimeMillis}",
//                            hostUid = player.uid,
//                            hostNickName = player.nickName,
//                            hostProfileUri = player.profileUri,
//                            guestUid = AccountManager.firebaseUid,
//                            guestNickName = PrefsManager.nickname,
//                            guestProfileUri = PrefsManager.profileUri
//                        )
//                    )
                } else {
                    showToast(getString(R.string.qr_network_error))
                    resumeCamera()
                }
            } else {
                showToast(getString(R.string.qr_network_error))
                resumeCamera()
            }
        }
    }

    fun observeGuestMultiGame(path: String) {
        Timber.e("observeGuestMultiGame : $path")
        vmScopeJob {
            diffPictureUseCase.observeMultiGameSnapshot(path = path)
                .collect { result ->
                    val data = result.successData()
                    if (data != null) {
                        val ready = data.first
                        val multiGameProfileInfo = data.second

                        if (ready) {
                            _startMultiGame.emit(multiGameProfileInfo)
                        }
                    } else {
                        showToast(getString(R.string.qr_network_error))
                    }
                }
        }
    }
}