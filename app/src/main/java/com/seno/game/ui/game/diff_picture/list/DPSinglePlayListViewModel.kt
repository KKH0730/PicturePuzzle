package com.seno.game.ui.game.diff_picture.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seno.game.R
import com.seno.game.extensions.getArrays
import com.seno.game.extensions.getDrawableResourceId
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.game.diff_picture.list.model.DPSingleGame
import com.seno.game.ui.game.diff_picture.list.model.DPSingleGameLevel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

val TOTAL_GAME_ROUND = getArrays(R.array.diff_picture_single_game_images).size

@HiltViewModel
class DiffPictureSingleGameViewModel @Inject constructor() : ViewModel() {
    private val _gameList = MutableStateFlow(singleGameList)
    val gameList = _gameList.asStateFlow()

    private val _currentGameRound = MutableSharedFlow<DPSingleGame>()
    val currentGameRound = _currentGameRound.asSharedFlow()

    private val imageList: List<Pair<Int, Int>>
        get() {
            val diffImages = getArrays(R.array.diff_picture_single_game_images)
            val diffCopyImages = getArrays(R.array.diff_picture_single_game_copy_images)
            return diffImages.mapIndexed { index, s ->
                diffImages[index].getDrawableResourceId() to diffCopyImages[index].getDrawableResourceId()
            }
        }

    private val singleGameList: List<DPSingleGame>
        get() {
            val completeGameList = PrefsManager.diffPictureCompleteGameRound.split(",").toMutableList()
            return (0 until TOTAL_GAME_ROUND).mapIndexed { index, i ->
                val dpSingleGameLevel = when {
                    i < 25 -> DPSingleGameLevel.LOW
                    i < 50 -> DPSingleGameLevel.MIDDLE
                    i < 75 -> DPSingleGameLevel.HIGH
                    else -> DPSingleGameLevel.HELL
                }

                DPSingleGame(id = i, level = dpSingleGameLevel, thumbnail = imageList[index].first).apply {
                    if (index == 0 || completeGameList.contains(i.toString())) {
                        this.isComplete = true
                    }
                }
            }
        }



    fun startGame(selectedItem: DPSingleGame) {
        viewModelScope.launch { _currentGameRound.emit(selectedItem) }
    }

    fun startNextGame(currentGameRound: Int) {
        viewModelScope.launch {
            if(currentGameRound <= TOTAL_GAME_ROUND - 2) {
                _currentGameRound.emit(_gameList.value[currentGameRound + 1])
            }
            _gameList.value.toMutableList().let {
                it[currentGameRound] = it[currentGameRound].copy(isComplete = true)
                it
            }.run {
                _gameList.value = this
            }
        }
    }

    fun refreshGameList() {
        singleGameList
            .map { it.copy() }
            .run { _gameList.value = this }
    }
}