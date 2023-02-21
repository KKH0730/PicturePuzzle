package com.seno.game.domain.usecase.humminjeongeumgame

import com.seno.game.data.hunminjeongeum.HunMinJeongEumRepository
import com.seno.game.data.network.model.HumMinJeongEumResponse
import com.seno.game.di.coroutine.IoDispatcher
import com.seno.game.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class SearchWordUseCase @Inject constructor(
    private val hunMinJeongEumRepository: HunMinJeongEumRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) {
    suspend fun reqSearchWord(word: String): Result<HumMinJeongEumResponse>? {
        return try {
            Result.Success(hunMinJeongEumRepository.searchWord(input = word))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}