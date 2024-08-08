package com.seno.game.ui.main.home.game.diff_picture.single

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.core.view.children
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.SimpleItemAnimator
import com.airbnb.lottie.LottieAnimationView
import com.fondesa.recyclerviewdivider.RecyclerViewDivider
import com.google.android.gms.ads.AdRequest
import com.seno.game.R
import com.seno.game.base.BaseActivity
import com.seno.game.databinding.ActivityDiffPictureSinglePlayBinding
import com.seno.game.extensions.*
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.main.home.game.diff_picture.list.TOTAL_STAGE
import com.seno.game.ui.main.home.game.diff_picture.single.adapter.AnswerMarkAdapter
import com.seno.game.util.AnimationUtils
import com.seno.game.util.ad.AdmobRewardedAdUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.abs

@AndroidEntryPoint
class DPSinglePlayActivity : BaseActivity<ActivityDiffPictureSinglePlayBinding>(
    layoutResId = R.layout.activity_diff_picture_single_play
) {
    private val viewModel by viewModels<DPSinglePlayViewModel>()
    private val admobRewardedAdUtil: AdmobRewardedAdUtil by lazy { AdmobRewardedAdUtil(this@DPSinglePlayActivity) }
    private val currentStagePosition: Int by lazy { intent.getIntExtra(STAGE_POSITION, -1) }
    private val currentRoundPosition: Int by lazy { intent.getIntExtra(CURRENT_ROUND_POSITION, -1) }
    private val finalRoundPosition: Int by lazy { intent.getIntExtra(FINAL_ROUND_POSITION, -1) }
    private var animatorSet: AnimatorSet? = null

    private var isShowHint = false
    private var isContinueFailGame = false

    // 이미지의 길이가 긴쪽을 채우고 짧은 쪽의 리사이즈 된 길이를 구함
    private val resizedLength: Float
        get() = ((copyIntrinsicHeight.toFloat() * fitXYHeightCorrection) * binding.ivOrigin.width.toFloat()) / copyIntrinsicWidth.toFloat()

    // 이미지 뷰의 width 혹은 height 와 리사이즈된 실제 이미지의 width 혹은 height 의 차이
    private val diff: Float
        get() = abs(binding.ivOrigin.height.toFloat() - resizedLength)

    private val copyIntrinsicWidth: Int
        get() = binding.ivCopy.drawable.intrinsicWidth

    private val copyIntrinsicHeight: Int
        get() = binding.ivCopy.drawable.intrinsicHeight

    private val fitXYHeightCorrection: Float
        get() {
            val originWidth = binding.ivOrigin.drawable.intrinsicWidth.toFloat()
            val originHeight = binding.ivOrigin.drawable.intrinsicHeight.toFloat()
            return if (originHeight == binding.ivOrigin.height.toFloat()) {
                1f
            } else {
                val stretchedHeight = (originWidth * binding.ivOrigin.height.toFloat()) / screenWidth.toFloat()
                val correction = stretchedHeight / originHeight
                correction
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initSetting()

        setPrepareView()
        setGameCompleteDialog()
        setGameFailDialog()
        setTimerView()
        setRecyclerView()

        loadAD()
        setImageTouchListener()
        observeFlow()
    }

    private fun observeFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.diffImagePair.collect {
                        setDiffPictureResource(resource1 = it.first, resource2 = it.second)
                    }
                }

                launch {
                    viewModel.answerMarkList.collect {
                        (binding.rvAnswerMark.adapter as AnswerMarkAdapter).submitList(it)
                    }
                }

                launch {
                    viewModel.onClearAnswer.collect { binding.clAnswerMark.removeAllViews() }
                }

                launch {
                    viewModel.onShowCompleteGameDialog.collect {
                        animatorSet
                            ?.takeIf { it.isRunning }
                            .let { AnimationUtils.stopAnimation(it) }
                        binding.cvTimerView.stopTimer()

                        delay(1000)
                        binding.cvGameCompleteDialog.show()
                    }
                }

                launch {
                    viewModel.drawRightAnswerMark.collect { point ->
                        val answerCenterX = (binding.ivOrigin.width.toFloat() * point.centerX / point.srcWidth)
                        val answerCenterY = (diff / 2f) + (resizedLength * point.centerY / point.srcHeight)

                        (this@DPSinglePlayActivity).drawLottieAnswerCircle(
                            x = binding.ivOrigin.x + answerCenterX - (point.answerRadius / 2),
                            y = binding.ivOrigin.y + answerCenterY - (point.answerRadius / 2),
                            imageContainerY = binding.llPictureContainer.y.toInt(),
                            rawRes = R.raw.right_answer_mark,
                            speed = 2f,
                            maxProgress = 1f,
                            radius = point.answerRadius.toInt(),
                            isWrongAnswer = false
                        ).also {
                            it.playAnimation()
                            binding.clAnswerMark.addView(it)
                        }

                        (this@DPSinglePlayActivity).drawLottieAnswerCircle(
                            x = binding.ivCopy.x + answerCenterX - (point.answerRadius / 2),
                            y = binding.ivCopy.y + answerCenterY - (point.answerRadius / 2),
                            imageContainerY = binding.llPictureContainer.y.toInt(),
                            rawRes = R.raw.right_answer_mark,
                            speed = 2f,
                            maxProgress = 1f,
                            radius = point.answerRadius.toInt(),
                            isWrongAnswer = false
                        ).also {
                            it.playAnimation()
                            binding.clAnswerMark.addView(it)
                        }

                        val list = (binding.rvAnswerMark.adapter as AnswerMarkAdapter).currentList.toMutableList()
                        for (i in list.size - 1 downTo 0) {
                            if (!list[i].isAnswer) {
                                val copyAnswerMark = list[i].copy()
                                copyAnswerMark.isAnswer = true
                                list[i] = copyAnswerMark
                                break
                            }
                        }
                        (binding.rvAnswerMark.adapter as AnswerMarkAdapter).submitList(list.toList())
                    }
                }

                launch {
                    viewModel.drawWrongAnswerMark.collect {
                        var lottieAnimationView: LottieAnimationView? = null
                        lottieAnimationView = (this@DPSinglePlayActivity).drawLottieAnswerCircle(
                            x = it.first,
                            y = it.second,
                            imageContainerY = binding.llPictureContainer.y.toInt(),
                            rawRes = R.raw.wrong_answer_mark,
                            speed = 5f,
                            maxProgress = 0.85f,
                            radius = 60,
                            isWrongAnswer = true,
                            onAnimationEnd = { binding.clAnswerMark.removeView(lottieAnimationView) }
                        ).also { view ->
                            view.playAnimation()
                            binding.clAnswerMark.addView(view)
                        }

                        binding.cvTimerView.onClickWrongAnswer.invoke()
                    }
                }

                launch {
                    viewModel.drawAnswerHint.collect { point ->
                        val answerCenterX = (binding.ivOrigin.width.toFloat() * point.centerX / point.srcWidth)
                        val answerCenterY = (diff / 2f) + (resizedLength * point.centerY / point.srcHeight)

                        var lottieAnimationView1: LottieAnimationView? = null
                        lottieAnimationView1 = (this@DPSinglePlayActivity).drawLottieAnswerCircle(
                            x = binding.ivOrigin.x + answerCenterX - (point.answerRadius / 2),
                            y = binding.ivOrigin.y + answerCenterY - (point.answerRadius / 2),
                            imageContainerY = binding.llPictureContainer.y.toInt(),
                            rawRes = R.raw.right_answer_mark,
                            speed = 2f,
                            maxProgress = 1f,
                            radius = point.answerRadius.toInt(),
                            isWrongAnswer = false,
                            onAnimationEnd = {
                                binding.clAnswerMark.removeView(lottieAnimationView1)
                            }
                        ).also {
                            it.playAnimation()
                            binding.clAnswerMark.addView(it)
                        }

                        var lottieAnimationView2: LottieAnimationView? = null
                        lottieAnimationView2 = (this@DPSinglePlayActivity).drawLottieAnswerCircle(
                            x = binding.ivCopy.x + answerCenterX - (point.answerRadius / 2),
                            y = binding.ivCopy.y + answerCenterY - (point.answerRadius / 2),
                            imageContainerY = binding.llPictureContainer.y.toInt(),
                            rawRes = R.raw.right_answer_mark,
                            speed = 2f,
                            maxProgress = 1f,
                            radius = point.answerRadius.toInt(),
                            isWrongAnswer = false,
                            onAnimationEnd = {
                                binding.clAnswerMark.removeView(lottieAnimationView2)
                            }
                        ).also {
                            it.playAnimation()
                            binding.clAnswerMark.addView(it)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        binding.clAnswerMark.children.forEach {
            if (it is LottieAnimationView) {
                if (it.isAnimating) {
                    it.cancelAnimation()
                }
                it.removeAllAnimatorListeners()
            }
        }

        if (animatorSet != null) {
            AnimationUtils.stopAnimation(animatorSet)
            animatorSet = null
        }
        admobRewardedAdUtil.release()
        binding.cvTimerView.release()
        super.onDestroy()
    }

    @SuppressLint("SetTextI18n")
    private fun initSetting() {
        binding.apply {
            activity = this@DPSinglePlayActivity
            isShowAd = PrefsManager.isShowAD
        }

        val minute = String.format("%02d", binding.cvTimerView.maxTime / 60)
        val second = String.format("%02d", binding.cvTimerView.maxTime % 60)
        binding.tvRemainingTime.text = "$minute:$second"
    }

    private fun setPrepareView() {
        binding.cvPrepareView.apply {
            setStage("ROUND ${String.format("%02d", currentRoundPosition + 1)}")
            onGameStart = { binding.cvTimerView.timerStart() }
        }.run {
            startMoveBottomAnimation()
        }
    }

    private fun setGameCompleteDialog() {
        binding.cvGameCompleteDialog.apply {
            if (currentRoundPosition != -1 && finalRoundPosition != -1) {
                handleButtonUI(
                    isFinalGame = currentRoundPosition == finalRoundPosition
                            && currentStagePosition == TOTAL_STAGE - 1
                )
            }

            onClickPositiveButton = {
                this.dismiss()

                currentRoundPosition.saveCompleteDPGameRound(currentStagePosition)
                Intent()
                    .apply {
                        putExtra(CURRENT_ROUND_POSITION, currentRoundPosition)
                        putExtra(FINAL_ROUND_POSITION, finalRoundPosition)
                        putExtra("isStartNextGame", true)
                    }
                    .also { setResult(RESULT_OK, it) }
                    .run { finish() }
            }
            onClickNegativeButton = {
                this.dismiss()
                currentRoundPosition.saveCompleteDPGameRound(currentStagePosition)

                Intent()
                    .apply {
                        putExtra(CURRENT_ROUND_POSITION, currentRoundPosition)
                        putExtra(FINAL_ROUND_POSITION, finalRoundPosition)
                        putExtra("isStartNextGame", false)
                    }
                    .also { setResult(RESULT_OK, it) }
                    .run { finish() }
            }
        }
    }

    private fun setGameFailDialog() {
        binding.cvGameFailDialog.apply {
            onClickGiveUp = {
                dismiss()
                finish()
            }
            onClickShowAD = onClickShowAD@{
                if (binding.clLoadingView.visibility == View.VISIBLE) {
                    return@onClickShowAD
                }
                binding.clLoadingView.visibility = View.VISIBLE
                binding.cvTimerView.stopTimer()

                admobRewardedAdUtil.loadRewardedAd(
                    onAdFailedToLoad = {
                        binding.clLoadingView.visibility = View.GONE
                        isContinueFailGame = false
                    },
                    onAdLoaded = {
                        isContinueFailGame = false
                        admobRewardedAdUtil.showRewardedAd(
                            onRewarded = { isContinueFailGame = true }
                        )
                    },
                    onAdDismissedFullScreenContent = {
                        if (isContinueFailGame) {
                            dismiss()
                            binding.ivTimer.setImageResource(R.drawable.ic_timer_normal)
                            binding.tvRemainingTime.setTextColor(getColor(R.color.white))
                            binding.clLoadingView.visibility = View.GONE
                            binding.cvTimerView.run {
                                resetTimer()
                                timerRestart()
                            }
                        }

                        isContinueFailGame = false
                    },
                    onAdFailedToShowFullScreenContent = {
                        dismiss()
                        binding.clLoadingView.visibility = View.GONE
                        isContinueFailGame = false
                    }
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setTimerView() {
        binding.cvTimerView.post {
            binding.cvTimerView.apply {
                onStartWrongAnswerAnimation = { decreasedTime: Int, prevTime: Int ->
                    binding.cvTimerView.playPenaltyAnimation(
                        rootView = binding.clRoot,
                        yPosition = binding.cvTimerView.y,
                        decreasedTime = decreasedTime,
                        prevTime = prevTime
                    )
                }
                onTimerChanged = { remainingTime ->
                    animatorSet
                        ?.takeIf { it.isRunning }
                        .let { AnimationUtils.stopAnimation(it) }

                    if (remainingTime <= 0) {
                        binding.tvRemainingTime.text = "00:00"
                    } else {
                        if (remainingTime <= 30) {
                            binding.ivTimer.setImageResource(R.drawable.ic_timer_alert)
                            binding.tvRemainingTime.setTextColor(getColor(R.color.color_FF3e96))

                            animatorSet = AnimationUtils.startShakeAnimation(targetView = binding.ivTimer, remainingTime = remainingTime)
                        }

                        val minute = String.format("%02d", remainingTime / 60)
                        val second = String.format("%02d", remainingTime % 60)
                        binding.tvRemainingTime.text = "$minute:$second"
                    }
                }
                onTimerOver = {
                    animatorSet
                        ?.takeIf { it.isRunning }
                        .let { AnimationUtils.stopAnimation(it) }
                    binding.cvTimerView.stopTimer()
                    binding.cvGameFailDialog.show()
                }
            }
        }
    }

    private fun setRecyclerView() {
        binding.rvAnswerMark.adapter = AnswerMarkAdapter()
        binding.rvAnswerMark.itemAnimator.run {
            if (this is SimpleItemAnimator) {
                this.supportsChangeAnimations = false
            }
        }

        RecyclerViewDivider.with(context = this)
            .asSpace()
            .hideLastDivider()
            .size(size = 8.dpToPx())
            .build()
            .addTo(binding.rvAnswerMark)
    }

    private fun loadAD() {
        if (PrefsManager.isShowAD) {
            // 배너 광고 로드
            val adRequest = AdRequest.Builder().build()
            binding.adView.loadAd(adRequest)
        }
    }

    // region setImageTouchListener
    @SuppressLint("ClickableViewAccessibility")
    private fun setImageTouchListener() {
        binding.ivOrigin.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                viewModel.drawAnswerCircle(
                    currentX = event.x,
                    currentY = event.y,
                    viewY = v.y,
                    imageViewWidth = binding.ivOrigin.width.toFloat(),
                    resizedLength = resizedLength,
                    diff = diff,
                    currentStagePosition = currentStagePosition,
                    currentRoundPosition = currentRoundPosition,
                    finalRoundPosition = finalRoundPosition
                )
                if (PrefsManager.isVibrationOn) {
                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                }
            }
            false
        }

        binding.ivCopy.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                viewModel.drawAnswerCircle(
                    currentX = event.x,
                    currentY = event.y,
                    viewY = v.y,
                    imageViewWidth = binding.ivOrigin.width.toFloat(),
                    resizedLength = resizedLength,
                    diff = diff,
                    currentStagePosition = currentStagePosition,
                    currentRoundPosition = currentRoundPosition,
                    finalRoundPosition = finalRoundPosition
                )
                if (PrefsManager.isVibrationOn) {
                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                }
            }
            false
        }
    }
    // endregion TouchListener

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setDiffPictureResource(@DrawableRes resource1: Int, @DrawableRes resource2: Int) {
        binding.ivOrigin.post {
            getDrawable(resource1)?.let {
                binding.ivOrigin.setImageResource(resource1)
            }
        }

        binding.ivCopy.post {
            getDrawable(resource2)?.let {
                binding.ivCopy.setImageResource(resource2)
            }
        }
    }

    fun onClickHint() {
        if (binding.clLoadingView.visibility == View.VISIBLE) {
            return
        }
        binding.clLoadingView.visibility = View.VISIBLE
        binding.cvTimerView.stopTimer()

        admobRewardedAdUtil.loadRewardedAd(
            onAdFailedToLoad = {
                binding.clLoadingView.visibility = View.GONE
                binding.cvTimerView.timerRestart()
                isShowHint = false
            },
            onAdLoaded = {
                isShowHint = false
                Timber.e("onAdLoaded : $isShowHint")
                admobRewardedAdUtil.showRewardedAd(
                    onRewarded = {
                        isShowHint = true
                        Timber.e("onRewarded : $isShowHint")
                    }
                )
            },
            onAdDismissedFullScreenContent = {
                Timber.e("onAdDismissedFullScreenContent : $isShowHint")
                if (isShowHint) {
                    viewModel.drawAnswerHint(
                        imageViewWidth = binding.ivOrigin.width.toFloat(),
                        resizedLength = resizedLength,
                        diff = diff
                    )
                }
                binding.clLoadingView.visibility = View.GONE
                binding.cvTimerView.timerRestart()

                isShowHint = false
            },
            onAdFailedToShowFullScreenContent = {
                binding.clLoadingView.visibility = View.GONE
                binding.cvTimerView.timerRestart()
                isShowHint = false
            }
        )
    }

//    fun onClickResult() {
//        if (binding.ivResult.visibility == View.VISIBLE) {
//            binding.clAnswerMark.visibility = View.VISIBLE
//            binding.ivCopy.visibility = View.VISIBLE
//            binding.ivResult.visibility = View.INVISIBLE
//            binding.ivResult.setImageBitmap(null)
//        } else {
//            binding.clAnswerMark.visibility = View.INVISIBLE
//            binding.ivCopy.visibility = View.INVISIBLE
//            binding.ivResult.visibility = View.VISIBLE
//            binding.ivResult.setImageBitmap(viewModel.getAnswer()?.answerMat.bitmapFrom())
//        }
//    }

    companion object {
        const val CURRENT_ROUND_POSITION = "currentRoundPosition"
        const val FINAL_ROUND_POSITION = "finalRoundPosition"
        const val STAGE_POSITION = "stagePosition"

        fun start(
            context: Context,
            stagePosition: Int,
            currentRoundPosition: Int,
            finalRoundPosition: Int,
            launcher: ActivityResultLauncher<Intent?>
        ) {
            context.startActivity(DPSinglePlayActivity::class.java, launcher) {
                putExtra(STAGE_POSITION, stagePosition)
                putExtra(CURRENT_ROUND_POSITION, currentRoundPosition)
                putExtra(FINAL_ROUND_POSITION, finalRoundPosition)
            }
        }
    }
}