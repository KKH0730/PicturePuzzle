package com.seno.game.data.diff_picture

import android.net.Uri
import com.seno.game.model.Result

interface DiffPictureRepository {
    suspend fun getDiffPictures(): Result<List<Pair<Uri, Uri>>>
}