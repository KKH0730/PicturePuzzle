package com.seno.game.domain.usecase.humminjeongeumgame

import com.seno.game.data.hunminjeongeum.HunMinJeongEumRepository
import com.seno.game.data.network.model.HumMinJeongEumResponse
import com.seno.game.di.coroutine.IoDispatcher
import com.seno.game.domain.BaseUseCase2
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class SearchWordUseCase @Inject constructor(
    private val hunMinJeongEumRepository: HunMinJeongEumRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : BaseUseCase2<String, HumMinJeongEumResponse>(ioDispatcher) {

    override suspend fun execute(params: String): HumMinJeongEumResponse {
        return hunMinJeongEumRepository.searchWord(input = params)
    }
}