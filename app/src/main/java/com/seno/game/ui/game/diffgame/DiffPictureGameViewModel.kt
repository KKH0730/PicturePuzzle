package com.seno.game.ui.game.diffgame

import androidx.lifecycle.ViewModel
import com.seno.game.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class DiffPictureGameViewModel @Inject constructor(

) : ViewModel() {
    private val _imageListFlow =
        MutableStateFlow(
            arrayListOf(
                R.drawable.img_diff_ex1 to R.drawable.img_diff_ex2,
                R.drawable.img_diff_ex3 to R.drawable.img_diff_ex4
            )
        )
    val imageListFlow = _imageListFlow.asStateFlow()
}