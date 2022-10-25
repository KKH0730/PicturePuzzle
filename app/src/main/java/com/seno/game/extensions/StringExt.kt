package com.seno.game.extensions

import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.annotation.StringRes
import com.seno.game.App
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

