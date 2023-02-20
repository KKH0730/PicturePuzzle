package com.seno.game.ui.main.home

import androidx.lifecycle.*
import com.seno.game.R
import com.seno.game.domain.usecase.diff_game.DiffPictureUseCase
import com.seno.game.domain.usecase.user.UserInfoUseCase
import com.seno.game.extensions.getString
import com.seno.game.model.DiffPictureGame
import com.seno.game.model.Result
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.base.BaseViewModel
import com.seno.game.util.MusicPlayUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val diffPictureUseCase: DiffPictureUseCase,
    private val userInfoUseCase: UserInfoUseCase
) : BaseViewModel() {

    private val _message = MutableSharedFlow<String>()
    val message = _message.asSharedFlow()

    private val _createRoomFlow = MutableStateFlow<DiffPictureGame?>(null)
    val createRoomFlow = _createRoomFlow.asStateFlow()

    private val _enterRoomFlow = MutableStateFlow<DiffPictureGame?>(null)
    val enterRoomFlow = _enterRoomFlow.asStateFlow()

    private val _exitRoomFlow = MutableStateFlow<DiffPictureGame?>(null)
    val exitRoomFlow = _exitRoomFlow.asStateFlow()

    private val _loadingFlow = MutableStateFlow<Boolean>(false)
    val loadingFlow = _loadingFlow.asStateFlow()

    private val _gameReadySharedFlow = MutableSharedFlow<Unit>()
    val gameReadySharedFlow = _gameReadySharedFlow.asSharedFlow()

    private val _backgroundVolume = MutableStateFlow(PrefsManager.backgroundVolume)
    val backgroundVolume: StateFlow<Float> get() = _backgroundVolume.asStateFlow()

    private val _effectVolume = MutableStateFlow(PrefsManager.effectVolume)
    val effectVolume: StateFlow<Float> get() = _effectVolume.asStateFlow()

    fun reqCreateRoom(date: String, uid: String, roomUid: String, nickName: String) {
        viewModelScope.launch {
            _loadingFlow.value = true
            val result = diffPictureUseCase.createRoom(
                date = date,
                uid = uid,
                roomUid = roomUid,
                nickName = nickName,
            )

            if (result is Result.Success) {
                _createRoomFlow.value = result.data
            } else {
                _message.emit(getString(R.string.network_request_error))
            }

            _loadingFlow.value = false
        }
    }

    fun reqEnterRoom(date: String, uid: String, roomUid: String, nickName: String) {
        viewModelScope.launch {
            val result = diffPictureUseCase.enterRoom(
                date = date,
                uid = uid,
                roomUid = roomUid,
                nickName = nickName,
            )
            if (result is Result.Success) {
                withContext(Dispatchers.Main) {
                    _enterRoomFlow.value = result.data
                }
            } else {
                _message.emit(getString(R.string.network_request_error))
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun reqExitRoom(date: String, uid: String, roomUid: String) {
        GlobalScope.launch {
            val result = diffPictureUseCase.exitRoom(
                date = date,
                uid = uid,
                roomUid = roomUid,
            )
            if (result is Result.Success) {
                _exitRoomFlow.value = result.data
            } else {
                _exitRoomFlow.value = null
            }
        }
    }

    fun reqGameReady(date: String, uid: String, roomUid: String) {
        viewModelScope.launch {
            val result = diffPictureUseCase.readyGamePlay(
                date = date,
                uid = uid,
                roomUid = roomUid
            )

            if (result is Result.Success) {
                _gameReadySharedFlow.emit(Unit)
            } else {
                _gameReadySharedFlow.emit(Unit)
                _message.emit(getString(R.string.network_request_error))
            }
        }
    }

    fun updateBackgroundVolume(volume: Float) {
        PrefsManager.backgroundVolume = volume
        MusicPlayUtil.setVol(leftVol = volume, rightVol = volume, isBackgroundSound = true)

        _backgroundVolume.value = volume
    }

    fun updateEffectVolume(volume: Float) {
        PrefsManager.effectVolume = volume
        MusicPlayUtil.setVol(leftVol = volume, rightVol = volume, isBackgroundSound = false)

        _effectVolume.value = volume
    }

    fun reqUpdateBackgroundVolume(uid: String?, volume: String) {
        viewModelScope.launch {
            uid?.let {
                userInfoUseCase.updateBackgroundVolume(uid = uid, volume = volume).collect { result ->
                    when (result) {
                        is Result.Success -> { _backgroundVolume.emit(result.data) }
                        is Result.Error -> { _message.emit(getString(R.string.network_request_error)) }
                        else -> {}
                    }
                }
            }
        }
    }

    fun reqUpdateEffectVolume(uid: String?, volume: String) {
        viewModelScope.launch {
            uid?.let {
                userInfoUseCase.updateEffectVolume(uid = uid, volume = volume).collect { result ->
                    when (result) {
                        is Result.Success -> { _effectVolume.emit(result.data) }
                        is Result.Error -> { _message.emit(getString(R.string.network_request_error)) }
                        else -> {}
                    }
                }
            }
        }
    }
}