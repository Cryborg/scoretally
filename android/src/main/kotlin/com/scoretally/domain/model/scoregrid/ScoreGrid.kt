package com.scoretally.domain.model.scoregrid

import com.scoretally.domain.model.GridCell
import com.scoretally.domain.model.GridType

interface ScoreGrid {
    val type: GridType
    val cells: List<GridCell>

    fun updateCell(cellId: String, value: Int?): ScoreGrid
    fun calculateTotal(): Int
    fun isComplete(): Boolean
    fun reset(): ScoreGrid
}
