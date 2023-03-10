package com.seno.game.ui.main.home

import androidx.lifecycle.*
import com.seno.game.R
import com.seno.game.domain.usecase.diff_game.DiffPictureUseCase
import com.seno.game.domain.usecase.user.GameConfigUseCase
import com.seno.game.extensions.getString
import com.seno.game.model.DiffPictureGame
import com.seno.game.model.Result
import com.seno.game.model.SavedGameInfo
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.base.BaseViewModel
import com.seno.game.util.SoundUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val diffPictureUseCase: DiffPictureUseCase,
    private val configUseCase: GameConfigUseCase,
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

    private val _savedGameInfoToLocalDB = MutableStateFlow(SavedGameInfo())
    val savedGameInfoToLocalDB: StateFlow<SavedGameInfo> get() = _savedGameInfoToLocalDB.asStateFlow()

    private val _vibrationSwitchOnOff = MutableStateFlow(PrefsManager.isVibrationOn)
    val vibrationSwitchOnOff: StateFlow<Boolean> get() = _vibrationSwitchOnOff.asStateFlow()

    private val _pushSwitchOnOff = MutableStateFlow(PrefsManager.isVibrationOn)
    val pushSwitchOnOff: StateFlow<Boolean> get() = _pushSwitchOnOff.asStateFlow()

    init {
//        AccountManager.addAuthStateListener(
//            onSignedIn = {
//                if (isFirstLogin) {
//                    isFirstLogin = false
//                    return@addAuthStateListener
//                }
//
//                if (AccountManager.isAnonymous) {
//
//                } else {

//                }
//            },
//            onSignedOut = {
//                // 로그 아웃 후 익명으로 강제 로그인 시키기 때문에 onSignedOut { } 블럭은 사용 하지 않음
//            }
//        )
    }
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
        SoundUtil.setVol(leftVol = volume, rightVol = volume, isBackgroundSound = true)

        _savedGameInfoToLocalDB.value = _savedGameInfoToLocalDB.value.copy().apply {
            backgroundVolume = volume
        }
    }

    fun updateEffectVolume(volume: Float) {
        PrefsManager.effectVolume = volume
        SoundUtil.setVol(leftVol = volume, rightVol = volume, isBackgroundSound = false)

        _savedGameInfoToLocalDB.value = _savedGameInfoToLocalDB.value.copy().apply {
            effectVolume = volume
        }
    }

    fun reqUpdateBackgroundVolume(uid: String?, volume: String) {
        viewModelScope.launch {
            uid?.let {
                configUseCase.reqUpdateBackgroundVolume(uid = uid, volume = volume).collect()
            }
        }
    }

    fun reqUpdateEffectVolume(uid: String?, volume: String) {
        viewModelScope.launch {
            uid?.let {
                configUseCase.reqUpdateEffectVolume(uid = uid, volume = volume).collect()
            }
        }
    }

    fun reqUpdateVibrationOnOff(uid: String?, isVibrationOn: Boolean) {
        uid?.let {
            viewModelScope.launch {
                configUseCase.reqUpdateVibrationOnOff(
                    uid = uid,
                    isVibrationOn = isVibrationOn
                ).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            PrefsManager.isVibrationOn = result.data
                            _savedGameInfoToLocalDB.value = _savedGameInfoToLocalDB.value.copy().apply {
                                this.isVibrationOn = result.data
                            }
                        }
                        is Result.Error -> { _message.emit(getString(R.string.network_request_error)) }
                        else -> {}
                    }
                }
            }
        }
    }

    fun reqUpdatePushOnOff(uid: String?, isPushOn: Boolean) {
        uid?.let {
            viewModelScope.launch {
                configUseCase.reqUpdatePushOnOff(
                    uid = uid,
                    isPushOn = isPushOn
                ).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            PrefsManager.isPushOn = result.data
                            _savedGameInfoToLocalDB.value = _savedGameInfoToLocalDB.value.copy().apply {
                                this.isPushOn = result.data
                            }
                        }
                        is Result.Error -> { _message.emit(getString(R.string.network_request_error)) }
                        else -> {}
                    }
                }
            }
        }
    }
}