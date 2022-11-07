package com.seno.game.ui.game.diffgame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seno.game.R
import com.seno.game.domain.DiffPictureUseCase
import com.seno.game.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DiffPictureGameViewModel @Inject constructor(
    private val diffPictureUseCase: DiffPictureUseCase
) : ViewModel() {
    private val _imageListFlow =
        MutableStateFlow(
            arrayListOf(
                R.drawable.diff_image1 to R.drawable.diff_image1_copy,
                R.drawable.diff_image2 to R.drawable.diff_image2_copy
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
            } else if(result is Result.Error) {
            }
        }
    }
}