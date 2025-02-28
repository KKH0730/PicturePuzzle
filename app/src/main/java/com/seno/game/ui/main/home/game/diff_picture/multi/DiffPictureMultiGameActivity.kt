package com.seno.game.ui.main.home.game.diff_picture.multi

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.children
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.seno.game.R
import com.seno.game.base.BaseActivity
import com.seno.game.databinding.ActivityDiffPictureMultiGameBinding
import com.seno.game.extensions.drawLottieAnswerCircle
import com.seno.game.extensions.screenWidth
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.main.home.game.component.GamePrepareView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.abs

const val ANSWER_CORRECTION = 20

@AndroidEntryPoint
class DiffPictureGameActivity : BaseActivity<ActivityDiffPictureMultiGameBinding>(
    layoutResId = R.layout.activity_diff_picture_multi_game
) {
    private val viewModel by viewModels<DiffPictureGameViewModel>()
    private var rewardedAd: RewardedAd? = null
    private var isShowHint = false
    private var isLoadingVideoAD = false

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


    private val fullScreenContentCallback = object: FullScreenContentCallback() {
        override fun onAdClicked() {
            super.onAdClicked()
        }

        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()
            if (isShowHint) {
                viewModel.drawAnswerHint(
                    imageViewWidth = binding.ivOrigin.width.toFloat(),
                    resizedLength = resizedLength,
                    diff = diff
                )
            }

            isShowHint = false
            rewardedAd = null
        }

        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
            super.onAdFailedToShowFullScreenContent(p0)
            rewardedAd = null
            binding.clLoadingView.visibility = View.GONE
        }

        override fun onAdImpression() {
            super.onAdImpression()
        }

        override fun onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent()
            binding.clLoadingView.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            activity = this@DiffPictureGameActivity
            isShowAd = PrefsManager.isShowAD
        }

        binding.cvPrepareView.setContent {
            var prepareVisible by remember { mutableStateOf(false) }

            AnimatedVisibility(
                visible = prepareVisible,
                exit = ExitTransition.None
            ) {
                GamePrepareView { prepareVisible = false }
            }

            loadAD()
            setImageTouchListener()
            observeFlow()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun observeFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.diffImagePair.collect {
                        setDiffPictureResource(resource1 = it.first, resource2 = it.second)
                    }
                }

                launch {
                    viewModel.onClearAnswer.collect { binding.clAnswerMark.removeAllViews() }
                }

                launch {
                    viewModel.totalScore.collect { totalScroe ->
                        binding.tvTotalAnswerCount.text = String.format(getString(R.string.diff_total_answer_count), totalScroe.toString())
                    }
                }

                launch {
                    viewModel.enableRewardADButton.collect {
                        binding.btnResult.isEnabled = it
                    }
                }

                launch {
                    viewModel.drawRightAnswerMark.collect { point ->
                        val answerCenterX = (binding.ivOrigin.width.toFloat() * point.centerX / point.srcWidth)
                        val answerCenterY = (diff / 2f) + (resizedLength * point.centerY / point.srcHeight)

                        (this@DiffPictureGameActivity).drawLottieAnswerCircle(
                            x = binding.ivOrigin.x + answerCenterX - (point.answerRadius / 2),
                            y = binding.ivOrigin.y + answerCenterY - (point.answerRadius / 2),
                            rawRes = R.raw.right_answer_mark,
                            speed = 2f,
                            maxProgress = 1f,
                            radius = point.answerRadius.toInt(),
                            isWrongAnswer = false
                        ).also {
                            it.playAnimation()
                            binding.clAnswerMark.addView(it)
                        }

                        (this@DiffPictureGameActivity).drawLottieAnswerCircle(
                            x = binding.ivCopy.x + answerCenterX - (point.answerRadius / 2),
                            y = binding.ivCopy.y + answerCenterY - (point.answerRadius / 2),
                            rawRes = R.raw.right_answer_mark,
                            speed = 2f,
                            maxProgress = 1f,
                            radius = point.answerRadius.toInt(),
                            isWrongAnswer = false
                        ).also {
                            it.playAnimation()
                            binding.clAnswerMark.addView(it)
                        }
                    }
                }

                launch {
                    viewModel.drawWrongAnswerMark.collect {
                        var lottieAnimationView: LottieAnimationView? = null
                        lottieAnimationView = (this@DiffPictureGameActivity).drawLottieAnswerCircle(
                            x = it.first,
                            y = it.second,
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
                    }
                }

                launch {
                    viewModel.drawAnswerHint.collect { point ->
                        val answerCenterX = (binding.ivOrigin.width.toFloat() * point.centerX / point.srcWidth)
                        val answerCenterY = (diff / 2f) + (resizedLength * point.centerY / point.srcHeight)

                        var lottieAnimationView1: LottieAnimationView? = null
                        lottieAnimationView1 = (this@DiffPictureGameActivity).drawLottieAnswerCircle(
                            x = binding.ivOrigin.x + answerCenterX - (point.answerRadius / 2),
                            y = binding.ivOrigin.y + answerCenterY - (point.answerRadius / 2),
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
                        lottieAnimationView2 = (this@DiffPictureGameActivity).drawLottieAnswerCircle(
                            x = binding.ivCopy.x + answerCenterX - (point.answerRadius / 2),
                            y = binding.ivCopy.y + answerCenterY - (point.answerRadius / 2),
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
        rewardedAd?.fullScreenContentCallback = null
        super.onDestroy()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun loadAD() {
        if (PrefsManager.isShowAD) {
            // 배너 광고 로드
            val adRequest = AdRequest.Builder().build()
            binding.adView.loadAd(adRequest)
        }
    }

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
                    diff = diff
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
                    diff = diff
                )
                if (PrefsManager.isVibrationOn) {
                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                }
            }
            false
        }
    }

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

    fun onClickResult() {

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

        if (isLoadingVideoAD) {
            return
        }
        isLoadingVideoAD = true
        binding.clLoadingView.visibility = View.VISIBLE

        val adRequest = AdRequest.Builder().build()
        // 리워드 광고 로드
        RewardedAd.load(
            this@DiffPictureGameActivity,
            getString(R.string.reward_ad_unit_id_for_test),
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    rewardedAd = null
                    isLoadingVideoAD = false
                    binding.clLoadingView.visibility = View.GONE
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    super.onAdLoaded(rewardedAd)
                    this@DiffPictureGameActivity.rewardedAd = rewardedAd
                    this@DiffPictureGameActivity.rewardedAd?.fullScreenContentCallback = fullScreenContentCallback
                    this@DiffPictureGameActivity.rewardedAd?.show(this@DiffPictureGameActivity) {
                        isShowHint = true
                        isLoadingVideoAD = false
                    }
                }
            }
        )
    }
}