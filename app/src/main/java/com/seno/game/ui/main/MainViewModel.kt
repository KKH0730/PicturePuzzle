package com.seno.game.ui.main

import androidx.lifecycle.viewModelScope
import com.seno.game.domain.GetSavedGameInfoUseCase
import com.seno.game.model.Result
import com.seno.game.model.SavedGameInfo
import com.seno.game.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getSavedGameInfoUseCase: GetSavedGameInfoUseCase
): BaseViewModel() {
    private val _showNetworkErrorEvent = MutableStateFlow<Boolean>(false)
    val showNetworkErrorEvent: StateFlow<Boolean> get() = _showNetworkErrorEvent.asStateFlow()

    private val _savedGameInfoToLocalDB = MutableSharedFlow<SavedGameInfo>()
    val savedGameInfoToLocalDB: SharedFlow<SavedGameInfo> get() = _savedGameInfoToLocalDB.asSharedFlow()

    fun getSavedGameInfo(uid: String) {
        viewModelScope.launch {
            getSavedGameInfoUseCase(params = uid).collect { result: Result<SavedGameInfo> ->
                when (result) {
                    is Result.Success -> { _savedGameInfoToLocalDB.emit(result.data) }
                    is Result.Error -> {  _showNetworkErrorEvent.value = true }
                    else -> {}
                }
            }
        }
    }
}