package com.scoretally.ui.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scoretally.domain.model.MatchListItem
import com.scoretally.domain.repository.MatchRepository
import com.scoretally.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchesViewModel @Inject constructor(
    private val matchRepository: MatchRepository
) : ViewModel() {

    val matches: StateFlow<List<MatchListItem>> = matchRepository.getAllMatchesForList()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_TIMEOUT_MILLIS),
            initialValue = emptyList()
        )

    fun deleteMatch(matchId: Long) {
        viewModelScope.launch {
            matchRepository.deleteMatchById(matchId)
        }
    }
}
