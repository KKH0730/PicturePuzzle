package com.seno.game.ui.main.home.game.diff_picture.multi.entry.lobby

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.seno.game.domain.usecase.diff_game.DiffPictureUseCase
import com.seno.game.manager.AccountManager
import com.seno.game.model.Player
import com.seno.game.model.successData
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LobbyViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val diffPictureUseCase: DiffPictureUseCase,
) : BaseViewModel() {
    var path: String = savedStateHandle[LobbyActivity.PATH] ?: ""
    val isHost: Boolean
        get() {
        val hostUid = try {
            path.split("_")[0]
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
        return AccountManager.firebaseUid.isNotEmpty() && hostUid.isNotEmpty() && AccountManager.firebaseUid == hostUid
    }

    val players = diffPictureUseCase.observeMultiGameSnapshot(path = path)
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
    )

    private val _isShowQuitDialog = MutableStateFlow(false)
    val isShowQuitDialog get() = _isShowQuitDialog.asStateFlow()

    private val _startMultiGame = MutableSharedFlow<String>()
    val startMultiGame get() = _startMultiGame.asSharedFlow()

    init {
        if (isHost) {
            createMultiGame()
        } else {
            updateMultiGamePlayer(isAdd = true)
        }
    }

    fun showQuitDialog(isShow: Boolean) {
        vmScopeJob { _isShowQuitDialog.emit(isShow) }
    }

    fun createMultiGame() {
        vmScopeJob {
            val hostPlayer = Player(
                uid = AccountManager.firebaseUid,
                nickname = PrefsManager.nickname,
                profileUri = PrefsManager.profileUri
            )
            val result = diffPictureUseCase.createMultiGame(
                path = path,
                hostUid = hostPlayer.uid,
                hostNickname = hostPlayer.nickname,
                hostProfileUri = hostPlayer.profileUri
            )

            val data = result.successData()
            if (data == null) {
                showQuitDialog(true)
            }
        }
    }

    fun updateMultiGamePlayer(isAdd: Boolean) {
        vmScopeJob {
            val result = diffPictureUseCase.updateMultiGamePlayer(
                path = path,
                uid = AccountManager.firebaseUid,
                nickname = PrefsManager.nickname,
                profileUri = PrefsManager.profileUri,
                isAdd = isAdd,
            )

            val data = result.successData()
            if (data == null) {
                showQuitDialog(true)
            }
        }
    }
}