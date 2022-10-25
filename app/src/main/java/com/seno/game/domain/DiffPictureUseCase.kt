package com.seno.game.domain

import android.net.Uri
import com.seno.game.data.diff_picture.DiffPictureRepository
import com.seno.game.model.Result
import javax.inject.Inject

class DiffPictureUseCase @Inject constructor(
    private val diffPictureRepository: DiffPictureRepository
) {

    suspend fun reqDiffPictures(): Result<List<Pair<Uri, Uri>>>  {
        return diffPictureRepository.getDiffPictures()
    }
}