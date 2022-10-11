package com.seno.game.util

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

object OpencvUtil {

    fun getMask(bitmap: Bitmap, gameColor: GameColor): Mat {
        val src = Mat()
        Utils.bitmapToMat(bitmap, src)

        val srcHSV = Mat()
        val mask = Mat()

        Imgproc.cvtColor(src, srcHSV, Imgproc.COLOR_RGB2HSV)
        val lowerb = when (gameColor) {
            GameColor.GREEN -> {
                Scalar(50.0, 100.0, 100.0)
            }
            GameColor.RED -> {
                Scalar(-10.0, 100.0, 100.0)
            }
            else -> {
                Scalar(110.0, 100.0, 100.0)
            }
        }

        val upperb = when (gameColor) {
            GameColor.GREEN -> {
                Scalar(70.0, 255.0, 255.0)
            }
            GameColor.RED -> {
                Scalar(10.0, 255.0, 255.0)
            }
            else -> {
                Scalar(130.0, 255.0, 255.0)
            }
        }

        Core.inRange(srcHSV, lowerb, upperb, mask)
        return mask
    }
}