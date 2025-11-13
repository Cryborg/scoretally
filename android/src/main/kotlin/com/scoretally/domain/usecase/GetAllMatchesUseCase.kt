package com.scoretally.domain.usecase

import com.scoretally.domain.model.Match
import com.scoretally.domain.repository.MatchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllMatchesUseCase @Inject constructor(
    private val matchRepository: MatchRepository
) {
    operator fun invoke(): Flow<List<Match>> = matchRepository.getAllMatches()
}
