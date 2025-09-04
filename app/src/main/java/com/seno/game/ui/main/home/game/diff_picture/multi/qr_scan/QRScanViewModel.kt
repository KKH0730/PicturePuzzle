package com.seno.game.ui.main.home.game.diff_picture.multi.qr_scan

import com.seno.game.domain.usecase.diff_game.DiffPictureUseCase
import com.seno.game.model.successData
import com.seno.game.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class QRScanViewModel @Inject constructor(
    private val diffPictureUseCase: DiffPictureUseCase,
) : BaseViewModel() {

    private val _resumeCameraPreview = MutableSharedFlow<Unit>()
    val resumeCameraPreview get() = _resumeCameraPreview.asSharedFlow()

    private val _moveWaitingRoom = MutableSharedFlow<String>()
    val moveWaitingRoom get() = _moveWaitingRoom.asSharedFlow()

    fun checkWaitingRoom(hostUid: String, currentTimeMillis: String) {
        vmScopeJob {
            val result = diffPictureUseCase.checkWaitingRoom(path = "${hostUid}_$currentTimeMillis", hostUid = hostUid)
            if (result.successData() != null) {
                _moveWaitingRoom.emit("${hostUid}_$currentTimeMillis")
            } else {
                _resumeCameraPreview.emit(Unit)
            }
        }
    }
}