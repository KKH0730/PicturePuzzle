package com.seno.game.ui.game.diffgame.list

import androidx.lifecycle.ViewModel
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.game.diffgame.list.model.DPSingleGame
import com.seno.game.ui.game.diffgame.list.model.DPSingleGameLevel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

const val TOTAL_GAME_ROUND = 100

@HiltViewModel
class DiffPictureSingleGameViewModel @Inject constructor() : ViewModel() {
    private val _gameList = MutableStateFlow(singleGameList)
    val gameList = _gameList.asStateFlow()

    private val _currentGameRound = MutableStateFlow<Int?>(null)
    val currentGameRound = _currentGameRound.asStateFlow()

    private val singleGameList: List<DPSingleGame>
        get() {
            val completeGameList = PrefsManager.diffPictureCompleteGameSingle.split(",").toMutableList()
            return (0 until TOTAL_GAME_ROUND).map {
                val dpSingleGameLevel = when {
                    it < 25 -> DPSingleGameLevel.LOW
                    it < 50 -> DPSingleGameLevel.MIDDLE
                    it < 75 -> DPSingleGameLevel.HIGH
                    else -> DPSingleGameLevel.HELL
                }

                DPSingleGame(id = it, level = dpSingleGameLevel).apply {
                    if (completeGameList.contains(it.toString())) {
                        this.isComplete = true
                    }
                }
            }
        }

    init {

    }

    fun startGame(position: Int) {
        _currentGameRound.value = position
    }

    fun startNextGame() {
        _currentGameRound.value?.let { currentRound ->
            if(currentRound + 1 <= 99) {
                _currentGameRound.value = _currentGameRound.value?.plus(1)
            }
        }
    }
}