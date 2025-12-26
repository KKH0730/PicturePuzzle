package com.seno.game.ui.main.screen

import androidx.lifecycle.viewModelScope
import com.seno.game.App
import com.seno.game.domain.usecase.user.GameConfigUseCase
import com.seno.game.extensions.clearDiskCache
import com.seno.game.extensions.clearMemoryCache
import com.seno.game.extensions.getImageDate
import com.seno.game.extensions.getTodayDate
import com.seno.game.extensions.parseImageDate
import com.seno.game.model.Result
import com.seno.game.model.SavedGameInfo
import com.seno.game.model.successData
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
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
                val savedUserInfoResponse = configUseCase.reqGetSavedGameInfo(uid = uid).successData()
                if (savedUserInfoResponse == null) {
                    _showNetworkErrorEvent.value = true
                    return@withContext
                }

                if (App.isServeMonthService) {
                    if (App.isServeMonthService && savedUserInfoResponse.recentSinglePlayDate.parseImageDate() != getImageDate()) {
                        configUseCase.reqResetAndGetSavedGameInfo(uid = uid, currentTimeMillis = System.currentTimeMillis())
                            .collect { result: Result<SavedGameInfo> ->
                                when (result) {
                                    is Result.Success -> {
                                        PrefsManager.clearSinglePlayData(currentTimeMillis = System.currentTimeMillis())
                                        PrefsManager.recentSinglePlayDate = getTodayDate()
                                        withContext(Dispatchers.Main) {
                                            clearMemoryCache()
                                            clearDiskCache()
                                        }

                                        _savedGameInfoToLocalDB.emit(result.data)
                                    }
                                    is Result.Error -> { _showNetworkErrorEvent.value = true }
                                    else -> {}
                                }
                            }
                    } else {
                        PrefsManager.recentSinglePlayDate = savedUserInfoResponse.recentSinglePlayDate
                        _savedGameInfoToLocalDB.emit(savedUserInfoResponse)
                    }
                } else {
                    _savedGameInfoToLocalDB.emit(savedUserInfoResponse)
                }
            }
        }
    }
}