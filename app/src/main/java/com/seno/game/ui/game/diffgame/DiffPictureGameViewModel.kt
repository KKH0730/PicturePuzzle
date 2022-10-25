package com.seno.game.ui.game.diffgame

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seno.game.R
import com.seno.game.domain.DiffPictureUseCase
import com.seno.game.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DiffPictureGameViewModel @Inject constructor(
    private val diffPictureUseCase: DiffPictureUseCase
) : ViewModel() {
    private val _imageListFlow =
        MutableStateFlow(
            arrayListOf(
                R.drawable.halloween1 to R.drawable.halloween1_copy,
                R.drawable.img_diff_ex1 to R.drawable.img_diff_ex2
            )
        )
    val imageListFlow = _imageListFlow.asStateFlow()


    init {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                diffPictureUseCase.reqDiffPictures()
            }

            if (result is Result.Success) {
                val uriPairList = result.data
                Timber.e("kkh uriList size : ${uriPairList.size}")
            } else if(result is Result.Error) {
                Timber.e("kkh error : ${result.exception.message}")
            }
        }
    }
}