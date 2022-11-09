package com.seno.game.ui.game.diffgame

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import com.seno.game.R
import com.seno.game.base.BaseActivity
import com.seno.game.databinding.ActivityDiffPictureGameBinding
import com.seno.game.extensions.bitmapFrom
import com.seno.game.extensions.drawAnswerCircle
import com.seno.game.extensions.screenWidth
import com.seno.game.ui.game.component.GamePrepareView
import com.seno.game.ui.game.diffgame.model.Setting
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
    private val viewModel by viewModels<DiffPictureGameViewModel>()
    private val opencvUtil: DiffPictureOpencvUtil = DiffPictureOpencvUtil()
    private val copyIntrinsicWidth: Int
        get() = binding.ivCopy.drawable.intrinsicWidth

    private val copyIntrinsicHeight: Int
        get() = binding.ivCopy.drawable.intrinsicHeight

    private val fitXYHeightCorrection: Float
        get() {
            val originWidth = 1280f
            val originHeight = 800f
            return if (originHeight == binding.ivOrigin.height.toFloat()) {
                1f
            } else  {
                val stretchedHeight = (originWidth * binding.ivOrigin.height.toFloat()) / screenWidth.toFloat()
                val correction = stretchedHeight / originHeight
                correction
            }
        }
    private lateinit var setting: Setting

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

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initSetting() {
        val imageList = viewModel.imageListFlow.value

        setImageResource(
            resource1 = imageList[0].first,
            resource2 = imageList[0].second
        )

        setting = Setting(
            answer = opencvUtil.drawCircle(
                srcBitmap = getDrawable(imageList[0].first)?.toBitmap(),
                copyBitmap = getDrawable(imageList[0].second)?.toBitmap()
            )
        ).apply {
            this.imageList = imageList
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setImageTouchListener() {
        binding.ivOrigin.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                drawAnswerCircle(
                    currentX = event.x,
                    currentY = event.y
                )
            }
            false
        }

        binding.ivCopy.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                drawAnswerCircle(
                    currentX = event.x,
                    currentY = event.y
                )
            }
            false
        }
    }

    private fun drawAnswerCircle(
        currentX: Float,
        currentY: Float,
    ) {
        // 이미지의 길이가 긴쪽을 채우고 짧은 쪽의 리사이즈 된 길이를 구함
        val resizedLength = ((copyIntrinsicHeight.toFloat() * fitXYHeightCorrection) * binding.ivOrigin.width.toFloat()) / copyIntrinsicWidth.toFloat()

        // 이미지 뷰의 width 혹은 height 와 리사이즈된 실제 이미지의 width 혹은 height 의 차이
        val diff = abs(binding.ivOrigin.height.toFloat() - resizedLength)

        setting.answer?.answerPointList?.forEachIndexed { index, point ->
            val centerX = (binding.ivOrigin.width.toFloat() * point.centerX / point.srcWidth)
            val centerY = (diff / 2f) + (resizedLength * point.centerY / point.srcHeight)

            // 두 점 사이의 거리를 구함
            val xLength = (currentX - centerX).toDouble().pow(2.0)
            val yLength = (currentY - centerY).toDouble().pow(2.0)
            val distance = sqrt(xLength + yLength)

            if (distance <= point.answerRadius + ANSWER_CORRECTION) {
                if (setting.answerHashMap[centerX] == null || setting.answerHashMap[centerX] != centerY) {
                    val ivCircle1 =
                        (this@DiffPictureGameActivity).drawAnswerCircle(point.answerRadius).apply {
                            this.x = binding.ivOrigin.x + centerX - (point.answerRadius / 2)
                            this.y = binding.ivOrigin.y + centerY - (point.answerRadius / 2)
                        }

                    val ivCircle2 =
                        (this@DiffPictureGameActivity).drawAnswerCircle(point.answerRadius).apply {
                            this.x = binding.ivCopy.x + centerX - (point.answerRadius / 2)
                            this.y = binding.ivCopy.y + centerY - (point.answerRadius / 2)
                        }
                    binding.clAnswerMark.addView(ivCircle1)
                    binding.clAnswerMark.addView(ivCircle2)

                    setting.apply {
                        answerHashMap[centerX] = centerY
                        currentAnswerCount += 1
                        binding.tvTotalAnswerCount.text = String.format(
                            getString(R.string.diff_total_answer_count), score.toString()
                        )
                    }
                    return
                }
            } else {
                if (index == setting.answer?.answerPointList?.size?.minus(1)) {
                    setting.score -= 1
                    binding.tvTotalAnswerCount.text = String.format(
                        getString(R.string.diff_total_answer_count), setting.score.toString()
                    )
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun observeFlow() {
        lifecycleScope.launch {
            setting.currentRound.distinctUntilChanged { _, new ->
                if (new > setting.totalRound - 1) {
                    Timber.e("kkh 111")
                } else {
                    Timber.e("kkh 222")
                    Handler(Looper.getMainLooper()).postDelayed({
                        clearAnswerMark()

                        setImageResource(
                            resource1 = setting.imageList[new].first,
                            resource2 = setting.imageList[new].second
                        )

                        setting.answer = opencvUtil.drawCircle(
                            srcBitmap = getDrawable(setting.imageList[new].first)?.toBitmap(),
                            copyBitmap = getDrawable(setting.imageList[new].second)?.toBitmap()
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
    private fun setImageResource(@DrawableRes resource1: Int, @DrawableRes resource2: Int) {
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
        if (binding.ivResult.visibility == View.VISIBLE) {
            binding.clAnswerMark.visibility = View.VISIBLE
            binding.ivCopy.visibility = View.VISIBLE
            binding.ivResult.visibility = View.INVISIBLE
            binding.ivResult.setImageBitmap(null)
        } else {
            binding.clAnswerMark.visibility = View.INVISIBLE
            binding.ivCopy.visibility = View.INVISIBLE
            binding.ivResult.visibility = View.VISIBLE
            binding.ivResult.setImageBitmap(setting.answer?.answerMat.bitmapFrom())
        }
    }
}