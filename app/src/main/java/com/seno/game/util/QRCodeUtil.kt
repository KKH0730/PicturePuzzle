package com.seno.game.util

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.util.*

object QRCodeUtil {
    fun createQRCode(uid: String): Bitmap? {
        val hints = Hashtable<EncodeHintType, String>().apply {
            put(EncodeHintType.CHARACTER_SET, "UTF-8")
        }

        val multiFormatWriter = MultiFormatWriter()

        return try {
            val bitMatrix = multiFormatWriter.encode(uid, BarcodeFormat.QR_CODE, 1000, 1000, hints)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}