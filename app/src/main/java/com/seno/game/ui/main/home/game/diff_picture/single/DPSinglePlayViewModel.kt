package com.seno.game.ui.main.home.game.diff_picture.single

import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.seno.game.App
import com.seno.game.R
import com.seno.game.di.DiffOpenCv
import com.seno.game.domain.usecase.diff_game.DiffPictureUseCase
import com.seno.game.extensions.getArrays
import com.seno.game.extensions.getBitmapFromUrl
import com.seno.game.extensions.getDrawable
import com.seno.game.extensions.getDrawableResourceId
import com.seno.game.model.successData
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.main.home.game.diff_picture.list.TOTAL_STAGE
import com.seno.game.ui.main.home.game.diff_picture.model.DiffGameInfo
import com.seno.game.ui.main.home.game.diff_picture.model.Point
import com.seno.game.ui.main.home.game.diff_picture.multi.ANSWER_CORRECTION
import com.seno.game.ui.main.home.game.diff_picture.single.model.AnswerMark
import com.seno.game.util.DiffPictureOpencvUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.sqrt

@HiltViewModel
class DPSinglePlayViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val diffPictureUseCase: DiffPictureUseCase,
    @DiffOpenCv private val opencvUtil: DiffPictureOpencvUtil,
) : ViewModel() {

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
                        srcBitmap = _diffImagePair.value?.first,
                        copyBitmap = _diffImagePair.value?.second
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
            val data = diffPictureUseCase
                .reqRoundDiffPicture((roundPosition + 1).toString())
                .successData()

            if (data != null) {
                val bitmap1 = data.first.getBitmapFromUrl()
                val bitmap2 = data.second.getBitmapFromUrl()
                if (bitmap1 == null || bitmap2 == null) return@repeat

                gameInfo = DiffGameInfo(
                    answer = opencvUtil.getDiffAnswer(
                        srcBitmap = _diffImagePair.value?.first,
                        copyBitmap = _diffImagePair.value?.second
                    )
                ).also {
                    val size = it.answer?.answerPointList?.size ?: 0
                    _answerMarkList.value = (0 until size).map { id -> AnswerMark(id = id, isAnswer = false) }
                }

                _diffImagePair.update { bitmap1 to bitmap2 }
                return   // 함수 전체 종료
            }

            if (times == 4) {
                _finishGame.emit(Unit)
            }
            delay(1000)
        }
    }

    fun getAnswer() = gameInfo?.answer

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
                    val isRightAnswer = distance <= (point.answerRadius / 2) + ANSWER_CORRECTION
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
                }

                if (isFindAnswer != null && !isFindAnswer) {
                    _drawWrongAnswerMark.emit(currentX to currentY + viewY)
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