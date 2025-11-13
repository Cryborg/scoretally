package com.scoretally.ui.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scoretally.domain.model.Player
import com.scoretally.domain.usecase.GetAllPlayersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PlayersViewModel @Inject constructor(
    getAllPlayersUseCase: GetAllPlayersUseCase
) : ViewModel() {

    val players: StateFlow<List<Player>> = getAllPlayersUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
