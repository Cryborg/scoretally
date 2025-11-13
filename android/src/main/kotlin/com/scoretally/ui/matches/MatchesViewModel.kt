package com.scoretally.ui.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scoretally.domain.model.MatchListItem
import com.scoretally.domain.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MatchesViewModel @Inject constructor(
    matchRepository: MatchRepository
) : ViewModel() {

    val matches: StateFlow<List<MatchListItem>> = matchRepository.getAllMatchesForList()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
