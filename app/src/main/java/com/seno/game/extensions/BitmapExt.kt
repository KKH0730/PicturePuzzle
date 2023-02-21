package com.seno.game.extensions

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import org.opencv.android.Utils
import org.opencv.core.CvException
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import timber.log.Timber
import java.util.*

fun Mat?.bitmapFrom(): Bitmap? {
    if (this == null) {
        return null
    }
    var bmp: Bitmap? = null
    val rgbMat = Mat()
    Imgproc.cvtColor(this, rgbMat, Imgproc.COLOR_BGR2RGB)
    try {
        bmp = Bitmap.createBitmap(rgbMat.cols(), rgbMat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(rgbMat, bmp)
    } catch (e: CvException) {
        Timber.e(e)
    }
    return bmp
}

fun String.createQRCode(): Bitmap? {
    val hints = Hashtable<EncodeHintType, String>().apply {
        put(EncodeHintType.CHARACTER_SET, "UTF-8")
    }

    val multiFormatWriter = MultiFormatWriter()

    return try {
        val bitMatrix = multiFormatWriter.encode(this@createQRCode, BarcodeFormat.QR_CODE, 1000, 1000, hints)
        val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.createBitmap(bitMatrix)
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}