package com.seno.game.ui.account

import androidx.lifecycle.viewModelScope
import com.seno.game.R
import com.seno.game.domain.usecase.user.GameConfigUseCase
import com.seno.game.extensions.getString
import com.seno.game.model.Result
import com.seno.game.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val configUseCase: GameConfigUseCase,
) : BaseViewModel() {

    private val _message = MutableSharedFlow<String>()
    val message = _message.asSharedFlow()


    fun reqUpdateNickname(
        uid: String?,
        nickname: String,
        onComplete: (String) -> Unit
    ) {
        uid?.let {
            viewModelScope.launch {
                val result = configUseCase.reqUpdateNickname(uid = uid, nickname = nickname).stateIn(viewModelScope).value
                when (result) {
                    is Result.Success -> { onComplete.invoke(result.data) }
                    is Result.Error -> { _message.emit(getString(R.string.network_request_error)) }
                    else -> {}
                }
            }
        }
    }
}