package com.seno.game.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seno.game.R
import com.seno.game.domain.usecase.humminjeongeumgame.SearchWordUseCase
import com.seno.game.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.EOFException
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val searchWordUseCase: SearchWordUseCase,
) : ViewModel() {

    private val _message = MutableSharedFlow<Int>()
    val message = _message.asSharedFlow()

    private val _searchResultMessage = MutableSharedFlow<Int>()
    val searchResultMessage = _searchResultMessage.asSharedFlow()

    fun searchWord(input: String) {
        viewModelScope.launch {
            val result = searchWordUseCase(input)

            withContext(Dispatchers.Main) {
                if (result is Result.Success) {
                    val response = result.data
                    _searchResultMessage.emit(
                        if (response.channel.item.isNotEmpty()) {
                            R.string.hummin_correct
                        } else {
                            R.string.hummin_incorrect
                        }
                    )

                } else if (result is Result.Error) {
                    when (result.exception) {
                        is EOFException -> {
                            // 사전 API 검색 결과가 없음
                            _searchResultMessage.emit(R.string.hummin_incorrect)
                        }
                        else -> {
                            _message.emit(R.string.network_error)
                        }
                    }
                }
            }
        }
    }
}