package com.seno.game.ui.game.diff_picture.list.model

import com.seno.game.R
import com.seno.game.extensions.getString

enum class DPSingleGameLevel(val value: String) {
    LOW("low"),
    MIDDLE("middle"),
    HIGH("high"),
    HELL("hell");

    companion object {
        fun getLevelValue(level: DPSingleGameLevel): String {
            return when(level) {
                LOW -> getString(R.string.diff_level_low)
                MIDDLE -> getString(R.string.diff_level_middle)
                HIGH -> getString(R.string.diff_level_high)
                else -> getString(R.string.diff_level_hell)
            }
        }
    }

}