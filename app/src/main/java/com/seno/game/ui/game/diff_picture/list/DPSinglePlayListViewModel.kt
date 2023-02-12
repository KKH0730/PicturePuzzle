package com.seno.game.ui.game.diff_picture.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seno.game.R
import com.seno.game.extensions.getArrays
import com.seno.game.extensions.getDrawableResourceId
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.game.diff_picture.list.model.DPSingleGame
import com.seno.game.ui.game.diff_picture.single.model.StartGameModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TOTAL_STAGE = 5

@HiltViewModel
class DiffPictureSingleGameViewModel @Inject constructor() : ViewModel() {
    private val _currentStage = MutableStateFlow(PrefsManager.diffPictureStage)
    val currentStage = _currentStage.asStateFlow()

    private val _gameList = MutableStateFlow(singleGameList)
    val gameList = _gameList.asStateFlow()

    private val _currentGameRound = MutableSharedFlow<StartGameModel>()
    val currentGameRound = _currentGameRound.asSharedFlow()

    private val stageInfos: List<List<Pair<Int, Int>>>
        get() {
            val diffImages = getArrays(R.array.diff_picture_stage1)
            val diffCopyImages = getArrays(R.array.diff_picture_copy_stage1)
            return (1..TOTAL_STAGE).map {
                diffImages.mapIndexed { index, s ->
                    diffImages[index].getDrawableResourceId() to diffCopyImages[index].getDrawableResourceId()
                }
            }
        }

    private val singleGameList: List<List<DPSingleGame>>
        get() {
            var id = 0
            val completeGameList =
                "${PrefsManager.diffPictureCompleteGameRound.split(",").toMutableList()}"
            val stageInfos: List<List<DPSingleGame>> = stageInfos.mapIndexed { stage, list ->
                list.map {
                    DPSingleGame(id = id, stage = stage).apply {
                        if (completeGameList.contains("$stage-$id")) {
                            this.isComplete = true
                        }
                        id += 1
                    }
                }
            }

            try {
                stageInfos[_currentStage.value].first { !it.isComplete }
            } catch (e: Exception) {
                null
            }.also {
                it?.isSelect = true
                selectedGame = it
            }

            return stageInfos
        }

    private var selectedGame: DPSingleGame? = null

    fun syncGameItem(selectedItem: DPSingleGame) {
        if (selectedGame?.id == selectedItem.id) {
            return
        }
        val duplicatedGameList = _gameList.value[_currentStage.value].toMutableList()
        duplicatedGameList.indexOf(selectedItem)
            .takeIf { it != -1 }
            ?.let {
                selectedGame?.isSelect = false
                duplicatedGameList[it] = duplicatedGameList[it].copy().apply { isSelect = true }
                selectedGame = duplicatedGameList[it]
            }

        val newGameList = _gameList.value.mapIndexed { index, list ->
            if (index == _currentStage.value) {
                duplicatedGameList
            } else {
                list
            }
        }
        _gameList.value = newGameList
    }

    fun startGame() {
        viewModelScope.launch {
            run {
                _gameList.value[_currentStage.value].forEachIndexed { index, dpSingleGame ->
                    if (!dpSingleGame.isComplete) {
                        _currentGameRound.emit(
                            StartGameModel(
                                currentGameModel = dpSingleGame,
                                currentStagePosition = _currentStage.value,
                                currentRoundPosition = index,
                                finalRoundPosition = _gameList.value[_currentStage.value].size - 1
                            )
                        )
                        return@run
                    }

                    if (index == _gameList.value[_currentStage.value].size - 1) {
                        _currentGameRound.emit(
                            StartGameModel(
                                currentGameModel = dpSingleGame,
                                currentStagePosition = _currentStage.value,
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
            if (currentRoundPosition <= finalRoundPosition - 1) {
                _currentGameRound.emit(
                    StartGameModel(
                        currentGameModel = _gameList.value[_currentStage.value][currentRoundPosition + 1],
                        currentStagePosition = _currentStage.value,
                        currentRoundPosition = currentRoundPosition + 1,
                        finalRoundPosition = finalRoundPosition
                    )
                )
            }
        }
    }

    fun refreshGameList() {
        val completeGameList = "${PrefsManager.diffPictureCompleteGameRound.split(",").toMutableList()}"
        var id = 0
        val stageInfos = stageInfos.mapIndexed { stage, list ->
            val gameList = list.map {
                DPSingleGame(id = id, stage = stage).apply {
                    if (completeGameList.contains("$stage-$id")) {
                        this.isComplete = true
                    }
                    id += 1
                }
            }
            try {
                gameList.first { !it.isComplete }.apply {
                    this.isSelect = true
                }.also { selectedGame = it }
            } catch (e: Exception) {
                selectedGame = null
            }

            gameList
        }
        _gameList.value = stageInfos
    }

    fun setNextStage(
        currentRoundPosition: Int,
        finalRoundPosition: Int,
    ) {
        if (currentRoundPosition == finalRoundPosition
            && _currentStage.value < TOTAL_STAGE - 1) {
            _currentStage.value += 1
        }
    }

    fun onChangedPage(stage: Int) {
        _currentStage.value = stage
    }
}