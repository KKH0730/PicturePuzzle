package com.seno.game.extensions

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.text.Html
import android.text.Spanned
import com.seno.game.App
import com.seno.game.R
import com.seno.game.prefs.PrefsManager
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


fun Resources.createRandomNickname(): String {
    val random = Random()
    val adjectiveList = getStringArray(R.array.adjective)
    val nounList = getStringArray(R.array.noun)
    val nickname = "${adjectiveList[random.nextInt(adjectiveList.size - 1)]} ${nounList[random.nextInt(nounList.size - 1)]}"
    PrefsManager.nickname = nickname

    return nickname
}

fun Int.saveCompleteDPGameRound() {
    if (!PrefsManager.diffPictureCompleteGameRound.split(",").contains(this.toString())) {
        PrefsManager.diffPictureCompleteGameRound = this.toString()
    }
}

fun String.getDrawableResourceId(): Int {
    val context = App.getInstance()
    val resName = "@drawable/$this"
    val packName = context.packageName
    return context.resources.getIdentifier(resName, "drawable", packName)
}
