package com.seno.game.ui.main.home

import androidx.lifecycle.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.seno.game.R
import com.seno.game.di.network.DiffDocRef
import com.seno.game.domain.DiffPictureUseCase
import com.seno.game.extensions.getString
import com.seno.game.model.DiffPictureGame
import com.seno.game.model.Player
import com.seno.game.model.Result
import com.seno.game.model.data
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val diffPictureUseCase: DiffPictureUseCase,
) : ViewModel() {

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
}