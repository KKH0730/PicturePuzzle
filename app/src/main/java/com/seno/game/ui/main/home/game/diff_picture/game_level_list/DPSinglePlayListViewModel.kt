package com.seno.game.ui.main.home.game.diff_picture.game_level_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seno.game.R
import com.seno.game.domain.usecase.diff_game.DiffPictureUseCase
import com.seno.game.extensions.getArrays
import com.seno.game.extensions.getDrawableResourceId
import com.seno.game.extensions.getString
import com.seno.game.manager.AccountManager
import com.seno.game.prefs.PrefsManager
import com.seno.game.data.model.DPSingleGame
import com.seno.game.ui.main.home.game.diff_picture.single.model.StartGameModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

const val TOTAL_STAGE = 5

@HiltViewModel
class DPSinglePlayListViewModel @Inject constructor(
    private val diffPictureUseCase: DiffPictureUseCase
) : ViewModel() {
    private val _message = MutableSharedFlow<String>()
    val message get() = _message.asSharedFlow()

    private val _currentStage = MutableStateFlow(PrefsManager.diffPictureStage)
    val currentStage get() = _currentStage.asStateFlow()

    private val _gameList = MutableStateFlow(singleGameList)
    val gameList get() = _gameList.asStateFlow()

    private val _currentGameRound = MutableSharedFlow<StartGameModel>()
    val currentGameRound get() = _currentGameRound.asSharedFlow()

    private val _enablePlayButton = MutableStateFlow(true)
    val enablePlayButton get() = _enablePlayButton.asStateFlow()

    private val stageInfos: List<List<Pair<Int, Int>>>
        get() {
            val diffImages = getArrays(R.array.diff_picture_stage1)
            val diffCopyImages = getArrays(R.array.diff_picture_copy_stage1)
            return (1..TOTAL_STAGE).map {
                diffImages.mapIndexed { index, _ ->
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
                list.mapIndexed { index, _ ->

                    DPSingleGame(id = id, stage = stage).apply {
                        if (completeGameList.contains("$stage-$index")) {
                            this.isComplete = true
                        }
                        id += 1
                    }
                }
            }

            kotlin.runCatching {
                stageInfos[_currentStage.value].first { !it.isComplete }
            }.onSuccess {
                it.isSelect = true
                selectedGame = it
            }.onFailure {
                it.printStackTrace()
            }

            return stageInfos
        }

    private var selectedGame: DPSingleGame? = null

    fun updateEnableUpdateButton(enable: Boolean) {
        _enablePlayButton.value = enable
    }

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

    fun reqUpdateSavedGameInfo(heartCount: Int = PrefsManager.diffPictureHeartCount) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                AccountManager.firebaseUid?.let { uid ->
                    diffPictureUseCase.reqUpdateSavedGameInfo(
                        uid = uid,
                        stage = PrefsManager.diffPictureStage,
                        completeGameRound = PrefsManager.diffPictureCompleteGameRound,
                        heartCount = heartCount,
                        heartChangedTime = PrefsManager.diffPictureHeartChangedTime
                    ).collect()
                }
            }
        }
    }

    fun startGame() {
        updateEnableUpdateButton(enable = false)

        viewModelScope.launch {
            if (PrefsManager.diffPictureHeartCount > 0) {
                PrefsManager.diffPictureHeartCount -= 1

                if (PrefsManager.diffPictureHeartCount + 1 == 5) {
                    PrefsManager.diffPictureHeartChangedTime = System.currentTimeMillis()
                    reqUpdateSavedGameInfo()
                }

                val gameList = _gameList.value[_currentStage.value]
                val selectedGameIndex = gameList.indexOf(selectedGame)
                if (selectedGameIndex != -1) {
                    _currentGameRound.emit(
                        StartGameModel(
                            currentGameModel = gameList[selectedGameIndex],
                            currentStagePosition = _currentStage.value,
                            currentRoundPosition = selectedGameIndex,
                            finalRoundPosition = _gameList.value[_currentStage.value].size - 1
                        )
                    )
                }
            } else {
                _message.emit(getString(R.string.diff_game_no_heart))
            }
        }
    }

    fun startNextGame(currentRoundPosition: Int, finalRoundPosition: Int) {
        updateEnableUpdateButton(enable = false)

        viewModelScope.launch {
            if (PrefsManager.diffPictureHeartCount > 0) {
                PrefsManager.diffPictureHeartCount -= 1
                reqUpdateSavedGameInfo()

                if (currentRoundPosition <= finalRoundPosition) {
                    val nextGameRound = if (currentRoundPosition == finalRoundPosition) {
                        0
                    } else {
                        currentRoundPosition + 1
                    }
                    _currentGameRound.emit(
                        StartGameModel(
                            currentGameModel = _gameList.value[_currentStage.value][nextGameRound],
                            currentStagePosition = _currentStage.value,
                            currentRoundPosition = nextGameRound,
                            finalRoundPosition = finalRoundPosition
                        )
                    )
                }
            } else {
                updateEnableUpdateButton(enable = true)
                _message.emit(getString(R.string.diff_game_no_heart))
            }
        }
    }

    fun refreshGameList() {
        val completeGameList = "${PrefsManager.diffPictureCompleteGameRound.split(",").toMutableList()}"
        var id = 0
        val stageInfos = stageInfos.mapIndexed { stage, list ->
            val gameList = List(list.size) { index ->
                DPSingleGame(id = id, stage = stage).apply {
                    if (completeGameList.contains("$stage-$index")) {
                        this.isComplete = true
                    }
                    id += 1
                }
            }
            gameList
        }

        kotlin.runCatching {
            stageInfos[_currentStage.value].first { !it.isComplete }
        }.onSuccess {
            it.isSelect = true
            selectedGame = it
        }.onFailure {
            it.printStackTrace()
        }

        _gameList.value = stageInfos
    }

    fun setNextStage() {
        if (_currentStage.value < stageInfos.size - 1) {
            _currentStage.value += 1
        }
    }

    fun onChangedPage(stage: Int) {
        _currentStage.value = stage
    }
}