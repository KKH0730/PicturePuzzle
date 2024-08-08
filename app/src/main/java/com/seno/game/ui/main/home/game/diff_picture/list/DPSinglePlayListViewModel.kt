package com.seno.game.ui.main.home.game.diff_picture.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seno.game.R
import com.seno.game.domain.usecase.diff_game.DiffPictureUseCase
import com.seno.game.extensions.getArrays
import com.seno.game.extensions.getDrawableResourceId
import com.seno.game.extensions.getString
import com.seno.game.manager.AccountManager
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.main.home.game.diff_picture.list.model.DPSingleGame
import com.seno.game.ui.main.home.game.diff_picture.single.model.StartGameModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

const val TOTAL_STAGE = 5

@HiltViewModel
class DiffPictureSingleGameViewModel @Inject constructor(
    private val diffPictureUseCase: DiffPictureUseCase
) : ViewModel() {
    private val _message = MutableSharedFlow<String>()
    val message get() = _message.asSharedFlow()

    private val _currentStage = MutableStateFlow(PrefsManager.diffPictureStage)
    val currentStage get() = _currentStage.asStateFlow()

    private val _gameList = MutableStateFlow(singleGameList)
    val gameList: StateFlow<List<List<DPSingleGame>>> = _gameList.asStateFlow()

    private val _currentGameRound = MutableSharedFlow<StartGameModel>()
    val currentGameRound get() = _currentGameRound.asSharedFlow()

    private val _enablePlayButton = MutableStateFlow(true)
    val enablePlayButton get() = _enablePlayButton.asStateFlow()
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

    /**
     * SharePreference 이용하여 클리어한 스테이지와 라운드를 저장
     * 클리어한 스테이지와 라운드를 체크하여 게임 리스트를 생성
     **/
    private val singleGameList: List<List<DPSingleGame>>
        get() {
            var id = 0
            var isCheckActiveStage = false
            val completeGameList = "${PrefsManager.diffPictureCompleteGameRound.split(",").toMutableList()}"
            val stageInfos: List<List<DPSingleGame>> = stageInfos.mapIndexed { stageIndex, list ->
                List(list.size) { roundIndex ->
                    val dpSingleGame = DPSingleGame(id = id++, stage = stageIndex).apply {
                        isComplete = completeGameList.contains("$stageIndex-$roundIndex")

                        // 현재 도전해야 할 스테이지 표시
                        isSelect = !isComplete && !isCheckActiveStage
                    }
                    if (dpSingleGame.isSelect && !isCheckActiveStage) {
                        isCheckActiveStage = true
                        selectedGame = dpSingleGame
                    }
                    dpSingleGame
                }
            }

            return stageInfos
        }

    fun refreshGameList() {
        _gameList.value = singleGameList
    }

    private var selectedGame: DPSingleGame? = null

    fun updateEnableUpdateButton(enable: Boolean) {
        _enablePlayButton.value = enable
    }

    fun syncGameItem(selectedItem: DPSingleGame) {
        if (selectedGame?.id == selectedItem.id) {
            return
        }
        val newGameList = _gameList.value[_currentStage.value].toMutableList()

        val previousSelectedGameIndex = newGameList.indexOfFirst { it.id == selectedGame?.id }
        if (previousSelectedGameIndex != -1) {
            newGameList[previousSelectedGameIndex] = newGameList[previousSelectedGameIndex].copy(isSelect = false)
        }

        val currentSelectedGameIndex = newGameList.indexOfFirst { it.id == selectedItem.id }
        if (currentSelectedGameIndex != -1) {
            newGameList[currentSelectedGameIndex] = newGameList[currentSelectedGameIndex].copy(isSelect = true)
            selectedGame = newGameList[currentSelectedGameIndex]
        }

        _gameList.value = _gameList.value.mapIndexed { index, list ->
            if (index == _currentStage.value) {
                newGameList
            } else {
                list
            }
        }
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
        viewModelScope.launch {
            if (PrefsManager.diffPictureHeartCount > 0) {
                updateEnableUpdateButton(enable = false)

                PrefsManager.diffPictureHeartCount -= 1

                if (PrefsManager.diffPictureHeartCount + 1 == 5) {
                    PrefsManager.diffPictureHeartChangedTime = System.currentTimeMillis()
                    reqUpdateSavedGameInfo()
                }

                val gameList = _gameList.value[_currentStage.value]
                val selectedGameIndex = gameList.indexOfFirst { it.id == selectedGame?.id }
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
        viewModelScope.launch {
            if (PrefsManager.diffPictureHeartCount > 0) {
                PrefsManager.diffPictureHeartCount -= 1
                reqUpdateSavedGameInfo()

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
            } else {
                _message.emit(getString(R.string.diff_game_no_heart))
            }
        }
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