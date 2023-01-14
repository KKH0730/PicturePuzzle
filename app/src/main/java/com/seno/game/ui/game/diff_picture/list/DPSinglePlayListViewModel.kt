package com.seno.game.ui.game.diff_picture.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seno.game.R
import com.seno.game.extensions.getArrays
import com.seno.game.extensions.getDrawableResourceId
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.game.diff_picture.list.model.DPSingleGame
import com.seno.game.ui.game.diff_picture.list.model.DPSingleGameLevel
import com.seno.game.ui.game.diff_picture.single.model.StartGameModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiffPictureSingleGameViewModel @Inject constructor() : ViewModel() {
    private val _gameList = MutableStateFlow(singleGameList)
    val gameList = _gameList.asStateFlow()

    private val _currentGameRound = MutableSharedFlow<StartGameModel>()
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
            return (imageList.indices).mapIndexed { index, i ->
                val dpSingleGameLevel = when {
                    i < 25 -> DPSingleGameLevel.LOW
                    i < 50 -> DPSingleGameLevel.MIDDLE
                    i < 75 -> DPSingleGameLevel.HIGH
                    else -> DPSingleGameLevel.HELL
                }

                DPSingleGame(id = i, level = dpSingleGameLevel, thumbnail = imageList[index].first).apply {
                    if (completeGameList.contains(i.toString())) {
                        this.isComplete = true
                    }
                }
            }
        }

    fun startGame(selectedItem: DPSingleGame, currentRoundPosition: Int, finalGameRoundPosition: Int) {
        viewModelScope.launch {
            _currentGameRound.emit(
                StartGameModel(
                    currentGameModel = selectedItem,
                    currentRoundPosition = currentRoundPosition,
                    finalRoundPosition = finalGameRoundPosition
                )
            )
        }
    }

    fun startGame() {
        viewModelScope.launch {
            run {
                _gameList.value.forEachIndexed { index, dpSingleGame ->
                    if (!dpSingleGame.isComplete) {
                        _currentGameRound.emit(
                            StartGameModel(
                                currentGameModel = dpSingleGame,
                                currentRoundPosition = index,
                                finalRoundPosition = _gameList.value.size - 1
                            )
                        )
                        return@run
                    }

                    if (index == _gameList.value.size - 1) {
                        _currentGameRound.emit(
                            StartGameModel(
                                currentGameModel = dpSingleGame,
                                currentRoundPosition = index,
                                finalRoundPosition = index
                            )
                        )
                    }
                }
            }
        }
    }

    fun startNextGame(currentRoundPosition: Int, finalRoundPosition: Int) {
        viewModelScope.launch {
            if(currentRoundPosition <= finalRoundPosition - 1) {
                _currentGameRound.emit(
                    StartGameModel(
                        currentGameModel = _gameList.value[currentRoundPosition + 1],
                        currentRoundPosition = currentRoundPosition + 1,
                        finalRoundPosition = finalRoundPosition
                    )
                )
            }
        }
    }

    fun refreshGameList() {
        singleGameList
            .map { it.copy() }
            .run { _gameList.value = this }
    }
}