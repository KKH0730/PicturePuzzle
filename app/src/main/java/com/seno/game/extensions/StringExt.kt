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

fun Int.saveCompleteDPGameRound(currentStagePosition: Int) {
    if (!PrefsManager.diffPictureCompleteGameRound.split(",").contains("$currentStagePosition-$this")) {
        PrefsManager.diffPictureCompleteGameRound = "$currentStagePosition-$this"
    }
}


fun String.getDrawableResourceId(): Int {
    val context = App.getInstance()
    val resName = "@drawable/$this"
    val packName = context.packageName
    return context.resources.getIdentifier(resName, "drawable", packName)
}


fun saveStage1CompleteDPGameRound() {
    PrefsManager.diffPictureCompleteGameRound = "0-0"
    PrefsManager.diffPictureCompleteGameRound = "0-1"
    PrefsManager.diffPictureCompleteGameRound = "0-2"
    PrefsManager.diffPictureCompleteGameRound = "0-3"
    PrefsManager.diffPictureCompleteGameRound = "0-4"
    PrefsManager.diffPictureCompleteGameRound = "0-5"
    PrefsManager.diffPictureCompleteGameRound = "0-6"
    PrefsManager.diffPictureCompleteGameRound = "0-7"
    PrefsManager.diffPictureCompleteGameRound = "0-8"
    PrefsManager.diffPictureCompleteGameRound = "0-9"
    PrefsManager.diffPictureCompleteGameRound = "0-10"
    PrefsManager.diffPictureCompleteGameRound = "0-11"
    PrefsManager.diffPictureCompleteGameRound = "0-12"
    PrefsManager.diffPictureCompleteGameRound = "0-13"
    PrefsManager.diffPictureCompleteGameRound = "0-14"
    PrefsManager.diffPictureStage = 1
}

fun saveStage2CompleteDPGameRound() {
    PrefsManager.diffPictureCompleteGameRound = "1-0"
    PrefsManager.diffPictureCompleteGameRound = "1-1"
    PrefsManager.diffPictureCompleteGameRound = "1-2"
    PrefsManager.diffPictureCompleteGameRound = "1-3"
    PrefsManager.diffPictureCompleteGameRound = "1-4"
    PrefsManager.diffPictureCompleteGameRound = "1-5"
    PrefsManager.diffPictureCompleteGameRound = "1-6"
    PrefsManager.diffPictureCompleteGameRound = "1-7"
    PrefsManager.diffPictureCompleteGameRound = "1-8"
    PrefsManager.diffPictureCompleteGameRound = "1-9"
    PrefsManager.diffPictureCompleteGameRound = "1-10"
    PrefsManager.diffPictureCompleteGameRound = "1-11"
    PrefsManager.diffPictureCompleteGameRound = "1-12"
    PrefsManager.diffPictureCompleteGameRound = "1-13"
    PrefsManager.diffPictureCompleteGameRound = "1-14"
    PrefsManager.diffPictureStage = 2
}

fun saveStage3CompleteDPGameRound() {
    PrefsManager.diffPictureCompleteGameRound = "2-0"
    PrefsManager.diffPictureCompleteGameRound = "2-1"
    PrefsManager.diffPictureCompleteGameRound = "2-2"
    PrefsManager.diffPictureCompleteGameRound = "2-3"
    PrefsManager.diffPictureCompleteGameRound = "2-4"
    PrefsManager.diffPictureCompleteGameRound = "2-5"
    PrefsManager.diffPictureCompleteGameRound = "2-6"
    PrefsManager.diffPictureCompleteGameRound = "2-7"
    PrefsManager.diffPictureCompleteGameRound = "2-8"
    PrefsManager.diffPictureCompleteGameRound = "2-9"
    PrefsManager.diffPictureCompleteGameRound = "2-10"
    PrefsManager.diffPictureCompleteGameRound = "2-11"
    PrefsManager.diffPictureCompleteGameRound = "2-12"
    PrefsManager.diffPictureCompleteGameRound = "2-13"
    PrefsManager.diffPictureCompleteGameRound = "2-14"
    PrefsManager.diffPictureStage = 3
}

fun saveStage4CompleteDPGameRound() {
    PrefsManager.diffPictureCompleteGameRound = "3-0"
    PrefsManager.diffPictureCompleteGameRound = "3-1"
    PrefsManager.diffPictureCompleteGameRound = "3-2"
    PrefsManager.diffPictureCompleteGameRound = "3-3"
    PrefsManager.diffPictureCompleteGameRound = "3-4"
    PrefsManager.diffPictureCompleteGameRound = "3-5"
    PrefsManager.diffPictureCompleteGameRound = "3-6"
    PrefsManager.diffPictureCompleteGameRound = "3-7"
    PrefsManager.diffPictureCompleteGameRound = "3-8"
    PrefsManager.diffPictureCompleteGameRound = "3-9"
    PrefsManager.diffPictureCompleteGameRound = "3-10"
    PrefsManager.diffPictureCompleteGameRound = "3-11"
    PrefsManager.diffPictureCompleteGameRound = "3-12"
    PrefsManager.diffPictureCompleteGameRound = "3-13"
    PrefsManager.diffPictureCompleteGameRound = "3-14"
    PrefsManager.diffPictureStage = 4
}

fun saveStage5CompleteDPGameRound() {
    PrefsManager.diffPictureCompleteGameRound = "4-0"
    PrefsManager.diffPictureCompleteGameRound = "4-1"
    PrefsManager.diffPictureCompleteGameRound = "4-2"
    PrefsManager.diffPictureCompleteGameRound = "4-3"
    PrefsManager.diffPictureCompleteGameRound = "4-4"
    PrefsManager.diffPictureCompleteGameRound = "4-5"
    PrefsManager.diffPictureCompleteGameRound = "4-6"
    PrefsManager.diffPictureCompleteGameRound = "4-7"
    PrefsManager.diffPictureCompleteGameRound = "4-8"
    PrefsManager.diffPictureCompleteGameRound = "4-9"
    PrefsManager.diffPictureCompleteGameRound = "4-10"
    PrefsManager.diffPictureCompleteGameRound = "4-11"
    PrefsManager.diffPictureCompleteGameRound = "4-12"
    PrefsManager.diffPictureCompleteGameRound = "4-13"
}