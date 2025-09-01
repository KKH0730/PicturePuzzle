package com.seno.game.ui.main

import androidx.lifecycle.viewModelScope
import com.seno.game.domain.usecase.user.GameConfigUseCase
import com.seno.game.extensions.getImageDate
import com.seno.game.extensions.getTodayDate
import com.seno.game.extensions.parseImageDate
import com.seno.game.model.Result
import com.seno.game.model.SavedGameInfo
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val configUseCase: GameConfigUseCase
): BaseViewModel() {

    private val _showNetworkErrorEvent = MutableStateFlow(false)
    val showNetworkErrorEvent: StateFlow<Boolean> get() = _showNetworkErrorEvent.asStateFlow()

    private val _savedGameInfoToLocalDB = MutableSharedFlow<SavedGameInfo?>()
    val savedGameInfoToLocalDB: SharedFlow<SavedGameInfo?> get() = _savedGameInfoToLocalDB.asSharedFlow()

    fun getSavedGameInfo(uid: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val savedUserInfoResponse = if (PrefsManager.recentSinglePlayDate.parseImageDate() != getImageDate()) {
                    configUseCase.reqResetAndGetSavedGameInfo(uid = uid, currentTimeMillis = System.currentTimeMillis())
                } else {
                    configUseCase.reqGetSavedGameInfo(uid = uid)
                }

                savedUserInfoResponse.collect { result: Result<SavedGameInfo> ->
                    when (result) {
                        is Result.Success -> {
                            if (PrefsManager.recentSinglePlayDate.parseImageDate() != getImageDate()) {
                                PrefsManager.clearSinglePlayData(currentTimeMillis = System.currentTimeMillis())
                                PrefsManager.recentSinglePlayDate = getTodayDate()
                            }
                            _savedGameInfoToLocalDB.emit(result.data)
                        }
                        is Result.Error -> { _showNetworkErrorEvent.value = true }
                        else -> {}
                    }
                }
            }
        }
    }
}