package com.seno.game.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import com.seno.game.R
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

val GREEN = Scalar(0.0, 255.0, 0.0)
val RED = Scalar(0.0, 0.0, 200.0)

enum class GameColor() {
    GREEN,
    RED,
    BLUE
}

class AreaOpencvUtil {

    fun drawBorder(mat: Mat): Mat {
        val src = Mat()
        mat.copyTo(src)

        val bin = Mat()
        Imgproc.cvtColor(src, bin, Imgproc.COLOR_BGR2GRAY)
        Imgproc.threshold(bin, bin, 0.0, 255.0, Imgproc.THRESH_OTSU)

        val contours = ArrayList<MatOfPoint>()
        val hierarchy = Mat()

        Imgproc.findContours(
            bin,
            contours,
            hierarchy,
            Imgproc.RETR_TREE,
            Imgproc.CHAIN_APPROX_SIMPLE
        )

        for (i in 0 until contours.size) {
            val contour = contours[i] // 윤곽선 좌표
            val contourArea = Imgproc.contourArea(contour) // 면적 구하기

            if (contourArea < 400) {
                continue
            }

            // 도형위에 윤곽선 그리기
            Imgproc.drawContours(src, contours, i,
                RED, 5, Imgproc.LINE_4, hierarchy, 0)
        }

        return src
    }

    private fun getPixelCount(src: Mat, maxPixel: Int): Int {
        val mat = Mat()

        Imgproc.cvtColor(src, mat, Imgproc.COLOR_BGR2GRAY)
        Imgproc.threshold(mat, mat, 150.0, 255.0, Imgproc.THRESH_BINARY)
        val size = Core.countNonZero(mat)
        return maxPixel - size
    }


    // 특정 범위의 색상 추출
    @SuppressLint("UseCompatLoadingForDrawables")
    fun extractColor(bitmap: Bitmap, gameColor: GameColor, context: Context): Pair<Mat, Int> {
        val mask = OpencvUtil.getMask(bitmap = bitmap, gameColor = gameColor)

        val colorDrawable = when (gameColor) {
            GameColor.GREEN -> context.getDrawable(R.drawable.bg_color_green)
            GameColor.RED -> context.getDrawable(R.drawable.bg_color_red)
            else -> context.getDrawable(R.drawable.bg_color_blue)
        }
        val colorBitmap = colorDrawable?.toBitmap(
            width = mask.width(),
            height = mask.height()
        )
        val colorMat = Mat()
        Utils.bitmapToMat(colorBitmap, colorMat)

        val whiteDrawable = context.getDrawable(R.drawable.bg_color_white)
        val whiteBitmap = whiteDrawable?.toBitmap(
            width = mask.width(),
            height = mask.height()
        )
        val whiteMat = Mat()
        Utils.bitmapToMat(whiteBitmap, whiteMat)

        Core.copyTo(colorMat, whiteMat, mask) // 마스크에 합성할 이미지, 원본이미지(합성 되어질 원본 이미지), 마스크
        Imgproc.cvtColor(whiteMat, whiteMat, Imgproc.COLOR_RGB2BGR)

        return whiteMat to getPixelCount(src = whiteMat, maxPixel = mask.rows() * mask.cols())
    }
}