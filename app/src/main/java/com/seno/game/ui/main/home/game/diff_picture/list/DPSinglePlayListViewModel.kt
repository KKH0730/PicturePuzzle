package com.seno.game.ui.main.home.game.diff_picture.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seno.game.R
import com.seno.game.domain.usecase.diff_game.DiffPictureUseCase
import com.seno.game.extensions.getArrays
import com.seno.game.extensions.getDrawableResourceId
import com.seno.game.extensions.getImageDate
import com.seno.game.extensions.getOriginImageUrl
import com.seno.game.extensions.getOtherImageUrl
import com.seno.game.extensions.getString
import com.seno.game.extensions.getTodayDate
import com.seno.game.extensions.isNotNullAndNotEmpty
import com.seno.game.extensions.saveOriginImageUrl
import com.seno.game.extensions.saveRoundImageUrl
import com.seno.game.manager.AccountManager
import com.seno.game.model.Result
import com.seno.game.model.successData
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.main.home.game.diff_picture.list.model.DPSingleGame
import com.seno.game.ui.main.home.game.diff_picture.single.model.StartGameModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.collections.firstOrNull

const val TOTAL_STAGE = 5

@HiltViewModel
class DiffPictureSingleGameViewModel @Inject constructor(
    private val diffPictureUseCase: DiffPictureUseCase
) : ViewModel() {
    private val _message = MutableSharedFlow<String>()
    val message get() = _message.asSharedFlow()

    private val _isShowAdPopup = MutableStateFlow(false)
    val isShowAdPopup get() = _isShowAdPopup.asStateFlow()

    private val _isLockGameStart = MutableStateFlow(false)
    val isLockGameStart get() = _isLockGameStart.asStateFlow()

    private val _currentStage = MutableStateFlow(PrefsManager.diffPictureStage)
    val currentStage get() = _currentStage.asStateFlow()

    private val _gameList = MutableStateFlow(singleGameList)
    val gameList: StateFlow<List<List<DPSingleGame>>> = _gameList.asStateFlow()

    private val _currentGameRound = MutableSharedFlow<StartGameModel>()
    val currentGameRound get() = _currentGameRound.asSharedFlow()

    private var selectedGame: DPSingleGame? = null

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

                        isSelect = !isComplete  && !isCheckActiveStage
                    }
                    if (dpSingleGame.isSelect) {
                        isCheckActiveStage = true
                        selectedGame = dpSingleGame
                    }
                    dpSingleGame
                }
            }

            return stageInfos
        }

    suspend fun reqRoundDiffPictures(stage: String, round: String): Pair<String, String>? {
        return withContext(Dispatchers.IO) {
            val imageDate = getImageDate()
            if ("${stage}-$round".getOriginImageUrl().contains(imageDate) && "${stage}-$round".getOtherImageUrl().contains(imageDate)) {
                "${stage}-$round".getOriginImageUrl() to "${stage}-$round".getOtherImageUrl()
            } else {
                val urlPair = diffPictureUseCase.reqRoundDiffPicture(stage = stage, round = round).successData()
                if (urlPair?.first.isNotNullAndNotEmpty() && urlPair.second.isNotNullAndNotEmpty()) {
                    urlPair.first.saveOriginImageUrl(stage = stage, round = round)
                    urlPair.second.saveRoundImageUrl(stage = stage, round = round)
                }
                urlPair
            }
        }
    }

    fun lockGameStart(isLock: Boolean) = _isLockGameStart.update { isLock }

    fun showAdPopup(isShow: Boolean) = _isShowAdPopup.update { isShow }

    fun showSnackMessage(message: String) {
        viewModelScope.launch { _message.emit(message) }
    }

    fun refreshGameList() {
        _gameList.value = singleGameList
    }

    suspend fun reqUpdateSavedGameInfo(
        heartCount: Int = PrefsManager.diffPictureHeartCount,
        heartChargedTime: Long = PrefsManager.diffPictureHeartChargedTime,
        recentSinglePlayDate: String = getTodayDate()
    ): Boolean {
        return withContext(Dispatchers.IO) {
            if (AccountManager.isUser) {
                val result = diffPictureUseCase.reqUpdateSavedGameInfo(
                    uid = AccountManager.firebaseUid,
                    stage = PrefsManager.diffPictureStage,
                    completeGameRound = PrefsManager.diffPictureCompleteGameRound,
                    heartCount = heartCount,
                    heartChargedTime = heartChargedTime,
                    recentSinglePlayDate = recentSinglePlayDate
                )
                result is Result.Success
            } else {
                true
            }
        }
    }

    fun startGame(isCheckHeartCount: Boolean = true) {
        viewModelScope.launch {
            if (isCheckHeartCount && PrefsManager.diffPictureHeartCount <= 0) {
                showAdPopup(isShow = true)
                return@launch
            }

            if (isLockGameStart.value) return@launch
            lockGameStart(isLock = true)

            val gameList = _gameList.value[_currentStage.value]
            val selectedGameIndex = gameList.indexOfFirst { it.id == selectedGame?.id }
            if (selectedGameIndex != -1) {
                val tempHeartCount = PrefsManager.diffPictureHeartCount - 1
                val tempHeartChargedTime = if (PrefsManager.diffPictureHeartCount == 5) System.currentTimeMillis() else PrefsManager.diffPictureHeartChargedTime
                val isSuccess = reqUpdateSavedGameInfo(tempHeartCount, tempHeartChargedTime)
                if (isSuccess) {
                    PrefsManager.diffPictureHeartCount = tempHeartCount
                    PrefsManager.diffPictureHeartChargedTime = tempHeartChargedTime

                    _currentGameRound.emit(
                        StartGameModel(
                            currentGameModel = gameList[selectedGameIndex],
                            currentStagePosition = _currentStage.value,
                            currentRoundPosition = selectedGameIndex,
                            finalRoundPosition = _gameList.value[_currentStage.value].size - 1,
                            images = reqRoundDiffPictures((_currentStage.value + 1).toString(), (selectedGameIndex + 1).toString())
                        )
                    )
                } else {
                    _message.emit(getString(R.string.network_request_error))
                }
            }

            lockGameStart(isLock = false)
        }
    }

    fun startNextGame(currentRoundPosition: Int, finalRoundPosition: Int) {
        viewModelScope.launch {
            if (PrefsManager.diffPictureHeartCount <= 0) {
                showAdPopup(isShow = true)
                return@launch
            }

            if (isLockGameStart.value) return@launch
            lockGameStart(isLock = true)

            val tempHeartCount = PrefsManager.diffPictureHeartCount - 1
            val tempHeartChargedTime = if (PrefsManager.diffPictureHeartCount == 5) System.currentTimeMillis() else PrefsManager.diffPictureHeartChargedTime
            val isSuccess = reqUpdateSavedGameInfo(tempHeartCount, tempHeartChargedTime)
            if (isSuccess) {
                PrefsManager.diffPictureHeartCount = tempHeartCount
                PrefsManager.diffPictureHeartChargedTime = tempHeartChargedTime

                if (currentRoundPosition <= finalRoundPosition - 1) {
                    _currentGameRound.emit(
                        StartGameModel(
                            currentGameModel = _gameList.value[_currentStage.value][currentRoundPosition + 1],
                            currentStagePosition = _currentStage.value,
                            currentRoundPosition = currentRoundPosition + 1,
                            finalRoundPosition = finalRoundPosition,
                            images = reqRoundDiffPictures((_currentStage.value + 1).toString(), (currentRoundPosition + 2).toString())
                        )
                    )
                }
            } else {
                _message.emit(getString(R.string.network_request_error))
            }

            lockGameStart(isLock = false)
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