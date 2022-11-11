package com.seno.game.extensions

import android.content.res.Resources
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
import com.seno.game.R
import com.seno.game.manager.AccountManager
import com.seno.game.prefs.PrefsManager
import com.seno.game.prefs.PrefsManager.nickname
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

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

fun Resources.createRandomNickname(): String {
    val random = Random()
    val adjectiveList = getStringArray(R.array.adjective)
    val nounList = getStringArray(R.array.noun)
    val nickname = "${adjectiveList[random.nextInt(adjectiveList.size - 1)]} ${nounList[random.nextInt(nounList.size - 1)]}"
    PrefsManager.nickname = nickname

    return nickname
}

