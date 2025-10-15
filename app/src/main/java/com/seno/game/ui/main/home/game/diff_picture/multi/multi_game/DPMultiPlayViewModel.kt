package com.seno.game.ui.main.home.game.diff_picture.multi.multi_game

import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seno.game.R
import com.seno.game.di.DiffOpenCv
import com.seno.game.domain.usecase.diff_game.DiffPictureUseCase
import com.seno.game.extensions.getBitmapFromUrl
import com.seno.game.extensions.getDrawable
import com.seno.game.extensions.getImageDate
import com.seno.game.extensions.getOriginImageUrl
import com.seno.game.extensions.getOtherImageUrl
import com.seno.game.extensions.isNotNullAndNotEmpty
import com.seno.game.extensions.saveOriginImageUrl
import com.seno.game.extensions.saveRoundImageUrl
import com.seno.game.model.successData
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.main.home.game.diff_picture.list.TOTAL_STAGE
import com.seno.game.ui.main.home.game.diff_picture.model.Answer
import com.seno.game.ui.main.home.game.diff_picture.model.DiffGameInfo
import com.seno.game.ui.main.home.game.diff_picture.model.Point
import com.seno.game.ui.main.home.game.diff_picture.single.DPSinglePlayActivity
import com.seno.game.ui.main.home.game.diff_picture.single.model.AnswerMark
import com.seno.game.util.DiffPictureOpencvUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.sqrt

@HiltViewModel
class DPMultiPlayViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val diffPictureUseCase: DiffPictureUseCase,
    @DiffOpenCv private val opencvUtil: DiffPictureOpencvUtil,
) : ViewModel() {

    var currentStage: Int = savedStateHandle[DPSinglePlayActivity.STAGE_POSITION] ?: 0
    var roundPosition: Int = savedStateHandle[DPSinglePlayActivity.CURRENT_ROUND_POSITION] ?: 0
    var image1: String = savedStateHandle[DPSinglePlayActivity.IMAGE1] ?: ""
    var image2: String = savedStateHandle[DPSinglePlayActivity.IMAGE2] ?: ""

    private val answerHashMap: HashMap<Float, Float> = HashMap()

    private val _finishGame = MutableSharedFlow<Unit>()
    val finishGame get() = _finishGame.asSharedFlow()

    private val _diffImagePair = MutableStateFlow<Pair<Bitmap, Bitmap>?>(null)
    val diffImagePair = _diffImagePair.asStateFlow()

    private val _answerMarkList = MutableStateFlow<List<AnswerMark>>(listOf())
    val answerMarkList = _answerMarkList.asStateFlow()

    private val _drawRightAnswerMark = MutableSharedFlow<Point>()
    val drawRightAnswerMark = _drawRightAnswerMark.asSharedFlow()

    private val _drawWrongAnswerMark = MutableSharedFlow<Pair<Float, Float>>()
    val drawWrongAnswerMark = _drawWrongAnswerMark.asSharedFlow()

    private val _drawAnswerHint = MutableSharedFlow<Point>()
    val drawAnswerHint = _drawAnswerHint.asSharedFlow()

    private val _onClearAnswer = MutableSharedFlow<Any>()
    val onClearAnswer = _onClearAnswer.asSharedFlow()

    private var currentAnswerCount = 0

    private val _onShowCompleteGameDialog = MutableSharedFlow<Any>()
    val onShowCompleteGameDialog = _onShowCompleteGameDialog.asSharedFlow()

    private var gameInfo: DiffGameInfo? = null

    val answer: Answer? get() = gameInfo?.answer

    private val modifiedAnswerMarkList: List<AnswerMark>
        get() {
            val list = _answerMarkList.value.toMutableList()
            for (i in list.size - 1 downTo 0) {
                if (!list[i].isAnswer) {
                    val copyAnswerMark = list[i].copy()
                    copyAnswerMark.isAnswer = true
                    list[i] = copyAnswerMark
                    break
                }
            }
            return list
        }

    init {
        viewModelScope.launch {
            if (image1.isNotEmpty() && image2.isNotEmpty()) {
                val bitmap1 = image1.getBitmapFromUrl()
                val bitmap2 = image2.getBitmapFromUrl()

                if (bitmap1 == null || bitmap2 == null) return@launch

                gameInfo = DiffGameInfo(
                    answer = opencvUtil.getDiffAnswer(
                        srcBitmap = bitmap1,
                        copyBitmap = bitmap2
                    )
                ).also {
                    val size = it.answer?.answerPointList?.size ?: 0
                    _answerMarkList.value = (0 until size).map { id -> AnswerMark(id = id, isAnswer = false) }
                }

                _diffImagePair.update { bitmap1 to bitmap2 }
                return@launch
            }

            loadDiffPicture()
        }
    }

    suspend fun loadDiffPicture() {
        repeat(5) { times ->
            val urlPair = getDiffPictures()
            if (urlPair != null) {
                val bitmap1 = urlPair.first.getBitmapFromUrl()
                val bitmap2 = urlPair.second.getBitmapFromUrl()
                if (bitmap1 == null || bitmap2 == null) return@repeat

                gameInfo = DiffGameInfo(
                    answer = opencvUtil.getDiffAnswer(
                        srcBitmap = bitmap1,
                        copyBitmap = bitmap2
                    )
                ).also {
                    val size = it.answer?.answerPointList?.size ?: 0
                    _answerMarkList.value = (0 until size).map { id -> AnswerMark(id = id, isAnswer = false) }
                }

                _diffImagePair.update { bitmap1 to bitmap2 }
                return
            }

            if (times == 4) {
                _finishGame.emit(Unit)
            }
            delay(1000)
        }
    }

    suspend fun getDiffPictures(): Pair<String, String>? {
        return withContext(Dispatchers.IO) {
            val imageDate = getImageDate()

            if ("${currentStage + 1}-${(roundPosition + 1)}".getOriginImageUrl().contains(imageDate) && "${currentStage + 1}-${(roundPosition + 1)}".getOtherImageUrl().contains(imageDate)) {
                (roundPosition + 1).toString().getOriginImageUrl() to (roundPosition + 1).toString().getOtherImageUrl()
            } else {
                val imagePair = diffPictureUseCase.reqRoundDiffPicture(stage = (roundPosition + 1).toString(), round = (roundPosition + 1).toString()).successData()
                if (imagePair?.first.isNotNullAndNotEmpty() && imagePair.second.isNotNullAndNotEmpty()) {
                    imagePair.first.saveOriginImageUrl(stage = (currentStage + 1).toString(), round = (roundPosition + 1).toString())
                    imagePair.second.saveRoundImageUrl(stage = (currentStage + 1).toString(), round = (roundPosition + 1).toString())
                }
                imagePair
            }
        }
    }

    private fun onClickRightAnswer(
        currentStagePosition: Int,
        currentRoundPosition: Int,
        finalRoundPosition: Int
    ) {
        viewModelScope.launch {
            launch(Dispatchers.Main) {
                gameInfo?.answer?.answerPointList?.let {
                    if (currentAnswerCount == it.size - 1) {
                        _onShowCompleteGameDialog.emit(Any())

                        if (PrefsManager.diffPictureStage == currentStagePosition
                            && currentRoundPosition == finalRoundPosition) {
                            if (PrefsManager.diffPictureStage < TOTAL_STAGE - 2) {
                                PrefsManager.diffPictureStage += 1
                            }
                        }
                    } else {
                        currentAnswerCount += 1
                    }
                }
            }
        }
    }

    fun drawAnswerCircle(
        currentX: Float,
        currentY: Float,
        viewX: Float,
        viewY: Float,
        imageViewWidth: Float,
        resizedLength: Float,
        diff: Float,
        currentStagePosition: Int,
        currentRoundPosition: Int,
        finalRoundPosition: Int
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val isFindAnswer = gameInfo?.answer?.answerPointList?.any { point ->
                    val answerCenterX = (imageViewWidth * point.centerX / point.srcWidth)
                    val answerCenterY = (diff / 2f) + (resizedLength * point.centerY / point.srcHeight)

                    // 두 점 사이의 거리를 구함
                    val xLength = (currentX - answerCenterX).toDouble().pow(2.0)
                    val yLength = (currentY - answerCenterY).toDouble().pow(2.0)
                    val distance = sqrt(xLength + yLength)

                    // Todo(point.answerRadius / 2 << 검증 필요)
//                    val isRightAnswer = distance <= (point.answerRadius / 2) + ANSWER_CORRECTION
                    val isRightAnswer = distance <= point.answerRadius
                    if (isRightAnswer) {
                        if (answerHashMap[answerCenterX] == null || answerHashMap[answerCenterX] != answerCenterY) {
                            answerHashMap[answerCenterX] = answerCenterY
                            _drawRightAnswerMark.emit(point)
                            _answerMarkList.value = modifiedAnswerMarkList

                            onClickRightAnswer(
                                currentStagePosition = currentStagePosition,
                                currentRoundPosition = currentRoundPosition,
                                finalRoundPosition = finalRoundPosition
                            )
                        }
                    }
                    isRightAnswer
                } ?: false

                if (!isFindAnswer) {
                    _drawWrongAnswerMark.emit(currentX + viewX to currentY + viewY)
                }
            }
        }
    }

    fun drawAnswerHint(
        imageViewWidth: Float,
        resizedLength: Float,
        diff: Float,
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                gameInfo?.answer?.answerPointList?.forEach { point ->
                    val answerCenterX = (imageViewWidth * point.centerX / point.srcWidth)
                    val answerCenterY = (diff / 2f) + (resizedLength * point.centerY / point.srcHeight)

                    if (answerHashMap[answerCenterX] == null || answerHashMap[answerCenterX] != answerCenterY) {
                        _drawAnswerHint.emit(point)
                        return@withContext
                    }
                }
            }
        }
    }
}