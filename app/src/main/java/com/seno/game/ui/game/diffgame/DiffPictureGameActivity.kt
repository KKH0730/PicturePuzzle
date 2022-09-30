package com.seno.game.ui.game.diffgame

import android.os.Bundle
import androidx.core.graphics.drawable.toBitmap
import com.seno.game.R
import com.seno.game.base.BaseActivity
import com.seno.game.databinding.ActivityDiffPictureGameBinding
import com.seno.game.util.BitmapUtil
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

class DiffPictureGameActivity : BaseActivity<ActivityDiffPictureGameBinding>(
    layoutResId = R.layout.activity_diff_picture_game
) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val drawable1 = binding.img1.drawable
        val bitmap1 = drawable1.toBitmap()
        val mat1 = Mat()
        Utils.bitmapToMat(bitmap1, mat1)

        val drawable2 = binding.img1.drawable
        val bitmap2 = drawable2.toBitmap()
        val mat2 = Mat()
        Utils.bitmapToMat(bitmap2, mat2)

        val result = Mat()
        Core.absdiff(mat1, mat2, result)

        binding.imgResult.setImageBitmap(BitmapUtil.bitmapFrom(bgrMat = result))
    }
}