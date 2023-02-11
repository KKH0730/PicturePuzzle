package com.seno.game.ui.game.diff_picture.single

import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seno.game.R
import com.seno.game.di.DiffOpenCv
import com.seno.game.domain.DiffPictureUseCase
import com.seno.game.extensions.getArrays
import com.seno.game.extensions.getDrawable
import com.seno.game.extensions.getDrawableResourceId
import com.seno.game.ui.game.diff_picture.model.Answer
import com.seno.game.ui.game.diff_picture.model.DiffGameInfo
import com.seno.game.ui.game.diff_picture.model.Point
import com.seno.game.ui.game.diff_picture.multi.ANSWER_CORRECTION
import com.seno.game.ui.game.diff_picture.single.DPSinglePlayActivity.Companion.CURRENT_ROUND_POSITION
import com.seno.game.ui.game.diff_picture.single.model.AnswerMark
import com.seno.game.util.DiffPictureOpencvUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.sqrt

@HiltViewModel
class DPSinglePlayViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val diffPictureUseCase: DiffPictureUseCase,
    @DiffOpenCv private val opencvUtil: DiffPictureOpencvUtil,
) : ViewModel() {

    var roundPosition: Int? = savedStateHandle[CURRENT_ROUND_POSITION]

    private val images: Pair<Int, Int>
    get() {
        val diffImages = getArrays(R.array.diff_picture_stage1)
        val diffCopyImages = getArrays(R.array.diff_picture_copy_stage1)
        return diffImages[roundPosition!!].getDrawableResourceId() to diffCopyImages[roundPosition!!].getDrawableResourceId()
    }

    private var gameInfo: DiffGameInfo = DiffGameInfo(
        answer = opencvUtil.getDiffAnswer(
            srcBitmap = getDrawable(images.first)?.toBitmap(),
            copyBitmap = getDrawable(images.second)?.toBitmap()
        )
    )

    private val answerHashMap: HashMap<Float, Float> = HashMap()

    private val _diffImagePair = MutableStateFlow(images.first to images.second)
    val diffImagePair = _diffImagePair.asStateFlow()

    private val _answerMarkList = MutableStateFlow(initialAnswerMarkList)
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

    private val initialAnswerMarkList: List<AnswerMark>?
        get() {
            val size = gameInfo.answer?.answerPointList?.size
            return if (size == null) {
                null
            } else {
                return (0 until size).map { id -> AnswerMark(id = id, isAnswer = false) }
            }
        }

    private val modifiedAnswerMarkList: List<AnswerMark>?
        get() {
            val list = _answerMarkList.value?.toMutableList()
            if (list != null) {
                for (i in list.size - 1 downTo 0) {
                    if (!list[i].isAnswer) {
                        val copyAnswerMark = list[i].copy()
                        copyAnswerMark.isAnswer = true
                        list[i] = copyAnswerMark
                        break
                    }
                }
            }
            return list
        }

    private fun onClickRightAnswer() {
        viewModelScope.launch {
            launch(Dispatchers.Main) {
                gameInfo.answer?.answerPointList?.let {
                    if (currentAnswerCount == it.size - 1) {
                        _onShowCompleteGameDialog.emit(Any())
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
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val isFindAnswer = gameInfo.answer?.answerPointList?.any { point ->
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

                            onClickRightAnswer()
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
                gameInfo.answer?.answerPointList?.forEach { point ->
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