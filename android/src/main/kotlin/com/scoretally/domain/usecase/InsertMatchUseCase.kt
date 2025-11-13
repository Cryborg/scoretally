package com.scoretally.domain.usecase

import com.scoretally.domain.model.Match
import com.scoretally.domain.repository.MatchRepository
import javax.inject.Inject

class InsertMatchUseCase @Inject constructor(
    private val matchRepository: MatchRepository
) {
    suspend operator fun invoke(match: Match): Long {
        return matchRepository.insertMatch(match)
    }
}
