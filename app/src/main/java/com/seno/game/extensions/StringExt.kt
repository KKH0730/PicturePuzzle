package com.seno.game.extensions

import android.graphics.Bitmap
import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.annotation.StringRes
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.seno.game.App
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

fun getString(@StringRes res: Int) = App.getInstance().getString(res)

@OptIn(ExperimentalContracts::class)
fun CharSequence?.isNotNullAndNotEmpty(): Boolean {
    contract {
        returns(true) implies (this@isNotNullAndNotEmpty != null)
    }

    return this != null && isNotEmpty()
}

fun String.fromHtml(): Spanned {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        Html.fromHtml(this)
    } else {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    }
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

