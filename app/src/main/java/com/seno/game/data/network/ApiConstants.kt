package com.seno.game.data.network

object ApiConstants {
    object Header {
        const val X_API_KEY = "tZXZmgGslf3U868aknFji2LT6p0DhgC49OG7yG0g"
    }
    object ApiKey {
        const val DICTIONARY_API_KEY = "A58B619ED95097AD7BC70688D3C81014"
    }

    object Collection {
        const val GAME = "game"
        const val PROFILE = "profile"
        const val SAVE_GAME_INFO = "save_game_info"
    }

    object Document {
        const val DIFF_PICTURE = "diff_picture"
    }

    object FirestoreKey {
        const val DIFF_PICTURE_GAME_CURRENT_STATE = "diffPictureGameCurrentStage"
        const val COMPLETE_GAME_ROUND = "completeGameRound"
    }
}