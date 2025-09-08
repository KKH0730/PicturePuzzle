package com.seno.game.ui.main.home.game.diff_picture.multi.entry.multi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.seno.game.R
import com.seno.game.domain.usecase.diff_game.DiffPictureUseCase
import com.seno.game.extensions.getString
import com.seno.game.manager.AccountManager
import com.seno.game.model.Player
import com.seno.game.model.successData
import com.seno.game.prefs.PrefsManager
import com.seno.game.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MultiGameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val diffPictureUseCase: DiffPictureUseCase,
) : BaseViewModel() {
    var path: String = savedStateHandle[LobbyActivity.PATH] ?: ""
    val isHost: Boolean
        get() {
        val hostUid = try {
            path.split("_")[0]
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
        return AccountManager.firebaseUid.isNotEmpty() && hostUid.isNotEmpty() && AccountManager.firebaseUid == hostUid
    }

    val players = diffPictureUseCase.observeMultiGameSnapshot(path = path)
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
    )

    private val _gameTimeLimit = MutableStateFlow(GameTimeLimit.FIFTEEN)
    val gameTimeLimit get() = _gameTimeLimit.asStateFlow()

    private val _gameRounds = MutableStateFlow(GameRounds.THREE)
    val gameRounds get() = _gameRounds.asStateFlow()

    private val _gameDifficulty = MutableStateFlow(Difficulty.NORMAL)
    val gameDifficulty get() = _gameDifficulty.asStateFlow()

    private val _startMultiGame = MutableSharedFlow<String>()
    val startMultiGame get() = _startMultiGame.asSharedFlow()

    init {
        if (isHost) {
            createMultiGame()
        } else {
            updateMultiGamePlayer(isAdd = true)
        }
    }

    fun updateTimeLimit(timeLimit: GameTimeLimit) = _gameTimeLimit.update { timeLimit }

    fun updateRounds(rounds: GameRounds) = _gameRounds.update { rounds }

    fun updateDifficulty(difficulty: Difficulty) = _gameDifficulty.update { difficulty }

    fun createMultiGame() {
        vmScopeJob {
            val hostPlayer = Player(
                uid = AccountManager.firebaseUid,
                nickname = PrefsManager.nickname,
                profileUri = PrefsManager.profileUri
            )
            val result = diffPictureUseCase.createMultiGame(
                path = path,
                hostUid = hostPlayer.uid,
                hostNickname = hostPlayer.nickname,
                hostProfileUri = hostPlayer.profileUri
            )

            val data = result.successData()
            if (data == null) {
                showNetworkErrorDialog(true)
            }
        }
    }

    fun updateMultiGamePlayer(isAdd: Boolean) {
        vmScopeJob {
            val result = diffPictureUseCase.updateMultiGamePlayer(
                path = path,
                uid = AccountManager.firebaseUid,
                nickname = PrefsManager.nickname,
                profileUri = PrefsManager.profileUri,
                isAdd = isAdd,
            )

            val data = result.successData()
            if (data == null) {
                showNetworkErrorDialog(true)
            }
        }
    }
}


enum class GameTimeLimit(val seconds: Int) {
    TEN(seconds = 10),
    FIFTEEN(seconds = 15),
    TWENTY(seconds = 20);

    fun add(): GameTimeLimit {
        return when (this) {
            TEN -> FIFTEEN
            FIFTEEN -> TWENTY
            TWENTY -> TWENTY
        }
    }

    fun minus(): GameTimeLimit {
        return when (this) {
            TEN -> TEN
            FIFTEEN -> TEN
            TWENTY -> FIFTEEN
        }
    }
}

enum class GameRounds(val count: Int) {
    THREE(count = 3),
    FOUR(count = 4),
    FIVE(count = 5);

    fun add(): GameRounds {
        return when (this) {
            THREE -> FOUR
            FOUR -> FIVE
            FIVE -> FIVE
        }
    }

    fun minus(): GameRounds {
        return when (this) {
            THREE -> THREE
            FOUR -> THREE
            FIVE -> FOUR
        }
    }
}

enum class Difficulty(val text: String) {
    EASY(text = getString(R.string.multi_lobby_game_setting_difficulty_1)),
    NORMAL(text = getString(R.string.multi_lobby_game_setting_difficulty_2)),
    HARD(text = getString(R.string.multi_lobby_game_setting_difficulty_3));
}