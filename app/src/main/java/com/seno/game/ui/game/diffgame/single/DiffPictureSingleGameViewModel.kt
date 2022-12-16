package com.seno.game.ui.game.diffgame.single

import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seno.game.R
import com.seno.game.di.DiffOpenCv
import com.seno.game.domain.DiffPictureUseCase
import com.seno.game.extensions.getDrawable
import com.seno.game.ui.game.diffgame.model.Answer
import com.seno.game.ui.game.diffgame.model.DiffGameInfo
import com.seno.game.ui.game.diffgame.model.Point
import com.seno.game.ui.game.diffgame.multi.ANSWER_CORRECTION
import com.seno.game.ui.game.diffgame.single.model.AnswerMark
import com.seno.game.util.DiffPictureOpencvUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.sqrt

@HiltViewModel
class DiffPictureSingleGameViewModel @Inject constructor(
    private val diffPictureUseCase: DiffPictureUseCase,
    @DiffOpenCv private val opencvUtil: DiffPictureOpencvUtil,
) : ViewModel() {
    private val imageList = arrayListOf(
        R.drawable.diff_image1 to R.drawable.diff_image1_copy,
        R.drawable.diff_image2 to R.drawable.diff_image2_copy
    )

    private var currentRound = MutableStateFlow(0)

    private var gameInfo: DiffGameInfo = DiffGameInfo(
        answer = opencvUtil.getDiffAnswer(
            srcBitmap = getDrawable(imageList[currentRound.value].first)?.toBitmap(),
            copyBitmap = getDrawable(imageList[currentRound.value].second)?.toBitmap()
        )
    )

    private val answerHashMap: HashMap<Float, Float> = HashMap()

    private val _diffImagePair = MutableStateFlow(imageList[0].first to imageList[0].second)
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

    private val _enableRewardADButton = MutableStateFlow(true)
    val enableRewardADButton = _enableRewardADButton.asStateFlow()

    private val _totalScore = MutableStateFlow(0)
    val totalScore = _totalScore.asStateFlow()

    private val _currentAnswerCount = MutableStateFlow(0)
    val currentAnswerCount = _currentAnswerCount.asStateFlow()

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

    init {
        viewModelScope.launch {
            launch {
                currentRound
                    .distinctUntilChanged { old, new -> old == new }
                    .collect {
                        if (it <= imageList.size - 1) {
                            gameInfo = DiffGameInfo(
                                answer = opencvUtil.getDiffAnswer(
                                    srcBitmap = getDrawable(imageList[it].first)?.toBitmap(),
                                    copyBitmap = getDrawable(imageList[it].second)?.toBitmap()
                                )
                            )

                            _answerMarkList.emit(initialAnswerMarkList)
                            _diffImagePair.value = imageList[it].first to imageList[it].second
                        } else {
                            _onShowCompleteGameDialog.emit(Any())
                        }
                    }
            }
        }
    }

    private fun onClickRightAnswer() {
        _totalScore.value += 1

        gameInfo.answer?.answerPointList?.let {
            if (_currentAnswerCount.value == it.size - 1) {
                _currentAnswerCount.value = 0
                startNextRound()
            } else {
                _currentAnswerCount.value += 1
            }
        }
    }

    private fun startNextRound() {
        viewModelScope.launch {
            launch(Dispatchers.IO) {
                delay(1000)
                _onClearAnswer.emit(Any())
                currentRound.value += 1
            }
        }
    }

    fun onClickRewardAdButton() {
//        isLoadingRewardAD.emit()
    }

    fun getAnswer(): Answer? = gameInfo.answer

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