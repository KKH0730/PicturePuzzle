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

const val TOTAL_SATE = 5

@HiltViewModel
class DiffPictureSingleGameViewModel @Inject constructor() : ViewModel() {
    private val _gameList = MutableStateFlow(singleGameList)
    val gameList = _gameList.asStateFlow()

    private val _currentGameRound = MutableSharedFlow<StartGameModel>()
    val currentGameRound = _currentGameRound.asSharedFlow()

//    private val imageList: List<Pair<Int, Int>>
//        get() {
//            val diffImages = getArrays(R.array.diff_picture_stage1)
//            val diffCopyImages = getArrays(R.array.diff_picture_copy_stage1)
//            return diffImages.mapIndexed { index, s ->
//                diffImages[index].getDrawableResourceId() to diffCopyImages[index].getDrawableResourceId()
//            }
//        }

    private val stageInfos: List<List<Pair<Int, Int>>>
        get() {
            val diffImages = getArrays(R.array.diff_picture_stage1)
            val diffCopyImages = getArrays(R.array.diff_picture_copy_stage1)
            return (1..TOTAL_SATE).map {
                diffImages.mapIndexed { index, s ->
                    diffImages[index].getDrawableResourceId() to diffCopyImages[index].getDrawableResourceId()
                }
            }
        }

    private val singleGameList: List<List<DPSingleGame>>
        get() {
            var id = 0
            val completeGameList =
                PrefsManager.diffPictureCompleteGameRound.split(",").toMutableList()
            val stageInfos: List<List<DPSingleGame>> = stageInfos.mapIndexed { stage, list ->
                list.map { pair ->
                    DPSingleGame(id = id++).apply {
                        if (completeGameList.contains(id.toString())) {
                            this.isComplete = true
                        }
                    }
                }
            }

            try {
                stageInfos[PrefsManager.diifPictureStage].first { !it.isComplete }
            } catch (e: Exception) {
                null
            }.also {
                it?.isSelect = true
                selectedGame = it
            }

            return stageInfos
        }

//    private val singleGameList: List<DPSingleGame>
//        get() {
//            val completeGameList = PrefsManager.diffPictureCompleteGameRound.split(",").toMutableList()
//            val gameList = (imageList.indices).mapIndexed { index, i ->
//                DPSingleGame(id = i).apply {
//                    if (completeGameList.contains(i.toString())) {
//                        this.isComplete = true
//                    }
//                }
//            }
//
//            try {
//                gameList.first { !it.isComplete }
//            } catch (e: Exception) {
//                null
//            }.also {
//                it?.isSelect = true
//                selectedGame = it
//            }
//
//            return gameList
//        }

    private var selectedGame: DPSingleGame? = null

    fun syncGameItem(selectedItem: DPSingleGame) {
        if (selectedGame?.id == selectedItem.id) {
            return
        }
        val duplicatedGameList = _gameList.value[PrefsManager.diifPictureStage].toMutableList()
        duplicatedGameList.indexOf(selectedItem)
            .takeIf { it != -1 }
            ?.let {
                selectedGame?.isSelect = false
                duplicatedGameList[it] = duplicatedGameList[it].copy().apply { isSelect = true }
                selectedGame = duplicatedGameList[it]
            }

        val newGameList = _gameList.value.mapIndexed { index, list ->
            if (index == PrefsManager.diifPictureStage) {
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
                _gameList.value[PrefsManager.diifPictureStage].forEachIndexed { index, dpSingleGame ->
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
            if (currentRoundPosition <= finalRoundPosition - 1) {
                _currentGameRound.emit(
                    StartGameModel(
                        currentGameModel = _gameList.value[PrefsManager.diifPictureStage][currentRoundPosition + 1],
                        currentRoundPosition = currentRoundPosition + 1,
                        finalRoundPosition = finalRoundPosition
                    )
                )
            }
        }
    }

    fun refreshGameList() {
        val completeGameList = PrefsManager.diffPictureCompleteGameRound.split(",").toMutableList()
        var id = 0
        val stageInfos = stageInfos.mapIndexed { stage, list ->
            val gameList = list.map { pair ->
                DPSingleGame(id = id++).apply {
                    if (completeGameList.contains(id.toString())) {
                        this.isComplete = true
                    }
                }
            }
            try {
                gameList.first { !it.isComplete }.apply {
                    isSelect = true
                }.also { selectedGame = it }
            } catch (e: Exception) {
                selectedGame = null
            }
            gameList
        }
        _gameList.value = stageInfos

//    fun refreshGameList() {
//        val completeGameList = PrefsManager.diffPictureCompleteGameRound.split(",").toMutableList()
//
//        (imageList.indices).mapIndexed { index, i ->
//            DPSingleGame(id = i).apply {
//                if (completeGameList.contains(i.toString())) {
//                    this.isComplete = true
//                }
//            }
//        }
//            .map { it.copy() }
//            .runCatching {
//                this.first { !it.isComplete}
//                    .let {
//                        it.isSelect = true
//                        selectedGame = it
//                    }
//                this
//            }
//            .onSuccess { gameList -> _gameList.value = gameList }
//            .onFailure { selectedGame = null }
//    }
    }
}