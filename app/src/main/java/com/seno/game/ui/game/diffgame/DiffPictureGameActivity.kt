package com.seno.game.ui.game.diffgame

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.children
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.seno.game.R
import com.seno.game.base.BaseActivity
import com.seno.game.databinding.ActivityDiffPictureGameBinding
import com.seno.game.extensions.drawLottieAnswerCircle
import com.seno.game.extensions.screenWidth
import com.seno.game.ui.game.component.GamePrepareView
import com.seno.game.ui.game.diffgame.model.DiffGameInfo
import com.seno.game.util.DiffPictureOpencvUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

const val ANSWER_CORRECTION = 20

@AndroidEntryPoint
class DiffPictureGameActivity : BaseActivity<ActivityDiffPictureGameBinding>(
    layoutResId = R.layout.activity_diff_picture_game
) {
    private var rewardedAd: RewardedAd? = null
    private val viewModel by viewModels<DiffPictureGameViewModel>()
    private val opencvUtil: DiffPictureOpencvUtil = DiffPictureOpencvUtil()
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
    private lateinit var gameInfo: DiffGameInfo
    private var isShowHint = false
    private var isLoadingVideoAD = false

    private val fullScreenContentCallback = object: FullScreenContentCallback() {
        override fun onAdClicked() {
            super.onAdClicked()
        }

        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()
            if (isShowHint) {
                drawHintMark()
            }

            isShowHint = false
            isLoadingVideoAD = false
            rewardedAd = null
        }

        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
            super.onAdFailedToShowFullScreenContent(p0)
            rewardedAd = null
            isLoadingVideoAD = false
        }

        override fun onAdImpression() {
            super.onAdImpression()
        }

        override fun onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            activity = this@DiffPictureGameActivity
        }

        binding.cvPrepareView.setContent {
            var prepareVisible by remember { mutableStateOf(false) }

            AnimatedVisibility(
                visible = prepareVisible,
                exit = ExitTransition.None
            ) {
                GamePrepareView { prepareVisible = false }
            }

            initSetting()
            setImageTouchListener()
            observeFlow()
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

        super.onDestroy()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initSetting() {
        // 배너 광고 로드
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        // 틀린그림 리소스 로드
        val imageList = viewModel.imageListFlow.value
        setDiffPictureResource(resource1 = imageList[0].first, resource2 = imageList[0].second)
        gameInfo = DiffGameInfo(
            answer = opencvUtil.getDiffAnswer(
                srcBitmap = getDrawable(imageList[0].first)?.toBitmap(),
                copyBitmap = getDrawable(imageList[0].second)?.toBitmap()
            )
        ).apply {
            this.imageList = imageList
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setImageTouchListener() {
        binding.ivOrigin.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                drawAnswerCircle(
                    v = v as ImageView,
                    currentX = event.x,
                    currentY = event.y,
                )
            }
            false
        }

        binding.ivCopy.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                drawAnswerCircle(
                    v = v as ImageView,
                    currentX = event.x,
                    currentY = event.y,
                )
            }
            false
        }
    }

    // 이미지의 길이가 긴쪽을 채우고 짧은 쪽의 리사이즈 된 길이를 구함
    private val resizedLength: Float
        get() = ((copyIntrinsicHeight.toFloat() * fitXYHeightCorrection) * binding.ivOrigin.width.toFloat()) / copyIntrinsicWidth.toFloat()

    // 이미지 뷰의 width 혹은 height 와 리사이즈된 실제 이미지의 width 혹은 height 의 차이
    private val diff: Float
        get() = abs(binding.ivOrigin.height.toFloat() - resizedLength)

    private fun drawAnswerCircle(
        v: ImageView,
        currentX: Float,
        currentY: Float,
    ) {
        var isFindAnswer = false
        run {
            gameInfo.answer?.answerPointList?.forEachIndexed { index, point ->
                val centerX = (binding.ivOrigin.width.toFloat() * point.centerX / point.srcWidth)
                val centerY = (diff / 2f) + (resizedLength * point.centerY / point.srcHeight)

                // 두 점 사이의 거리를 구함
                val xLength = (currentX - centerX).toDouble().pow(2.0)
                val yLength = (currentY - centerY).toDouble().pow(2.0)
                val distance = sqrt(xLength + yLength)

                // Todo(point.answerRadius / 2 << 검증 필요)
                val isWrongAnswer = distance <= (point.answerRadius / 2) + ANSWER_CORRECTION

                if (isWrongAnswer) {
                    isFindAnswer = true
                    if (gameInfo.answerHashMap[centerX] == null || gameInfo.answerHashMap[centerX] != centerY) {
                        (this@DiffPictureGameActivity).drawLottieAnswerCircle(
                            x = binding.ivOrigin.x + centerX - (point.answerRadius / 2),
                            y = binding.ivOrigin.y + centerY - (point.answerRadius / 2),
                            rawRes = R.raw.right_answer_mark,
                            speed = 2f,
                            maxProgress = 1f,
                            radius = point.answerRadius.toInt()
                        ).also {
                            it.playAnimation()
                            binding.clAnswerMark.addView(it)
                        }

                        (this@DiffPictureGameActivity).drawLottieAnswerCircle(
                            x = binding.ivCopy.x + centerX - (point.answerRadius / 2),
                            y = binding.ivCopy.y + centerY - (point.answerRadius / 2),
                            rawRes = R.raw.right_answer_mark,
                            speed = 2f,
                            maxProgress = 1f,
                            radius = point.answerRadius.toInt()
                        ).also {
                            it.playAnimation()
                            binding.clAnswerMark.addView(it)
                        }

                        gameInfo.apply {
                            answerHashMap[centerX] = centerY
                            currentAnswerCount += 1
                            binding.tvTotalAnswerCount.text = String.format(getString(R.string.diff_total_answer_count), score.toString())
                        }
                    } else {
                        Timber.e("kkh 22")
                    }
                    return@run
                } else {
//                    if (index == setting.answer?.answerPointList?.size?.minus(1)) {
//                        setting.score -= 1
//                        binding.tvTotalAnswerCount.text = String.format(
//                            getString(R.string.diff_total_answer_count), setting.score.toString()
//                        )
//                    }
                }
            }
        }

        if (!isFindAnswer) {
            var lottieAnimationView: LottieAnimationView? = null
            lottieAnimationView = (this@DiffPictureGameActivity).drawLottieAnswerCircle(
                x = currentX - 80, // (radius / 2)
                y = v.y + currentY - 80, // (radius / 2)
                rawRes = R.raw.wrong_answer_mark,
                speed = 5f,
                maxProgress = 0.85f,
                radius = 160,
                onAnimationEnd = {
                    binding.clAnswerMark.removeView(lottieAnimationView)
                }
            ).also {
                it.playAnimation()
                binding.clAnswerMark.addView(it)
            }
        }
    }

    private fun drawHintMark() {
        run {
            gameInfo.answer?.answerPointList?.forEachIndexed { index, point ->
                val centerX = (binding.ivOrigin.width.toFloat() * point.centerX / point.srcWidth)
                val centerY = (diff / 2f) + (resizedLength * point.centerY / point.srcHeight)

                if (gameInfo.answerHashMap[centerX] == null || gameInfo.answerHashMap[centerX] != centerY) {
                    var lottieAnimationView1: LottieAnimationView? = null
                    lottieAnimationView1 = (this@DiffPictureGameActivity).drawLottieAnswerCircle(
                        x = binding.ivOrigin.x + centerX - (point.answerRadius / 2),
                        y = binding.ivOrigin.y + centerY - (point.answerRadius / 2),
                        rawRes = R.raw.right_answer_mark,
                        speed = 2f,
                        maxProgress = 1f,
                        radius = point.answerRadius.toInt(),
                        onAnimationEnd = {
                            binding.clAnswerMark.removeView(lottieAnimationView1)
                        }
                    ).also {
                        it.playAnimation()
                        binding.clAnswerMark.addView(it)
                    }

                    var lottieAnimationView2: LottieAnimationView? = null
                    lottieAnimationView2 = (this@DiffPictureGameActivity).drawLottieAnswerCircle(
                        x = binding.ivCopy.x + centerX - (point.answerRadius / 2),
                        y = binding.ivCopy.y + centerY - (point.answerRadius / 2),
                        rawRes = R.raw.right_answer_mark,
                        speed = 2f,
                        maxProgress = 1f,
                        radius = point.answerRadius.toInt(),
                        onAnimationEnd = {
                            binding.clAnswerMark.removeView(lottieAnimationView2)
                        }
                    ).also {
                        it.playAnimation()
                        binding.clAnswerMark.addView(it)
                    }
                    return@run
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun observeFlow() {
        lifecycleScope.launch {
            gameInfo.currentRound.distinctUntilChanged { _, new ->
                if (new > gameInfo.totalRound - 1) {
                    Timber.e("kkh 111")
                } else {
                    Timber.e("kkh 222")
                    Handler(Looper.getMainLooper()).postDelayed({
                        clearAnswerMark()

                        setDiffPictureResource(
                            resource1 = gameInfo.imageList[new].first,
                            resource2 = gameInfo.imageList[new].second
                        )

                        gameInfo.answer = opencvUtil.getDiffAnswer(
                            srcBitmap = getDrawable(gameInfo.imageList[new].first)?.toBitmap(),
                            copyBitmap = getDrawable(gameInfo.imageList[new].second)?.toBitmap()
                        )
                    }, 1000)
                }
                true
            }.collect {}
        }
    }

    private fun clearAnswerMark() {
        binding.clAnswerMark.removeAllViews()
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
        if (isLoadingVideoAD) {
            return
        }
        isLoadingVideoAD = true
//        if (binding.ivResult.visibility == View.VISIBLE) {
//            binding.clAnswerMark.visibility = View.VISIBLE
//            binding.ivCopy.visibility = View.VISIBLE
//            binding.ivResult.visibility = View.INVISIBLE
//            binding.ivResult.setImageBitmap(null)
//        } else {
//            binding.clAnswerMark.visibility = View.INVISIBLE
//            binding.ivCopy.visibility = View.INVISIBLE
//            binding.ivResult.visibility = View.VISIBLE
//            binding.ivResult.setImageBitmap(setting.answer?.answerMat.bitmapFrom())
//        }
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
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    super.onAdLoaded(rewardedAd)
                    this@DiffPictureGameActivity.rewardedAd = rewardedAd
                    this@DiffPictureGameActivity.rewardedAd?.fullScreenContentCallback = fullScreenContentCallback
                    this@DiffPictureGameActivity.rewardedAd?.show(
                        this@DiffPictureGameActivity
                    ) { rewardItem ->
                        isShowHint = true
                    }
                }
            }
        )
    }
}