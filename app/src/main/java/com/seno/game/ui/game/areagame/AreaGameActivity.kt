package com.seno.game.ui.game.areagame

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.seno.game.R
import com.seno.game.base.BaseActivity
import com.seno.game.databinding.ActivityAreaGameBinding
import com.seno.game.util.BitmapUtil
import com.seno.game.util.GameColor
import com.seno.game.util.OpencvUtil
import org.opencv.core.Mat
import timber.log.Timber

class AreaGameActivity : BaseActivity<ActivityAreaGameBinding>(
    layoutResId = R.layout.activity_area_game
) {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btn.setOnClickListener {
            val bitmap = binding.canvasView.currentCanvas
            val maxScorePair = getMaxScorePair(bitmap = bitmap)
            val strokeMat = OpencvUtil.drawBorder(mat = maxScorePair.first)
            binding.imgView.setImageBitmap(BitmapUtil.bitmapFrom(bgrMat = strokeMat))
        }

        binding.reset.setOnClickListener {
            binding.canvasView.reset()
            binding.imgView.setImageBitmap(null)
        }

        binding.btnChangeBlue.setOnClickListener {
            binding.canvasView.changeColor(getColor(R.color.area_blue))
        }

        binding.btnChangeRed.setOnClickListener {
            binding.canvasView.changeColor(getColor(R.color.area_red))
        }

        binding.btnChangeGreen.setOnClickListener {
            binding.canvasView.changeColor(getColor(R.color.area_green))
        }
    }

    private fun getMaxScorePair(bitmap: Bitmap): Pair<Mat, Int> {
        val greenExtractedPair = OpencvUtil.extractColor(
            bitmap = bitmap,
            gameColor = GameColor.GREEN,
            context = this@AreaGameActivity
        )
        val redExtractedPair = OpencvUtil.extractColor(
            bitmap = bitmap,
            gameColor = GameColor.RED,
            context = this@AreaGameActivity
        )
        val blueExtractedPair = OpencvUtil.extractColor(
            bitmap = bitmap,
            gameColor = GameColor.BLUE,
            context = this@AreaGameActivity
        )

        val firstCompareScore = greenExtractedPair.second.coerceAtLeast(redExtractedPair.second)

        return when (firstCompareScore.coerceAtLeast(blueExtractedPair.second)) {
            greenExtractedPair.second -> greenExtractedPair
            redExtractedPair.second -> redExtractedPair
            else -> blueExtractedPair
        }
    }
}