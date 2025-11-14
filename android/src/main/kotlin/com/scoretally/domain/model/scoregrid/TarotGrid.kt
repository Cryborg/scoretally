package com.scoretally.domain.model.scoregrid

import android.content.Context
import com.scoretally.R
import com.scoretally.domain.model.GridCell
import com.scoretally.domain.model.GridType

data class TarotGrid(
    override val cells: List<GridCell> = emptyList(),
    val roundCount: Int = 0
) : ScoreGrid {

    override val type: GridType = GridType.TAROT

    override fun updateCell(cellId: String, value: Int?): ScoreGrid {
        val updatedCells = cells.map { cell ->
            if (cell.id == cellId) {
                cell.copy(value = value, isLocked = value != null)
            } else {
                cell
            }
        }
        return copy(cells = updatedCells)
    }

    override fun calculateTotal(): Int {
        return cells.sumOf { it.value ?: 0 }
    }

    override fun isComplete(): Boolean {
        return cells.isNotEmpty() && cells.all { it.value != null }
    }

    override fun reset(): ScoreGrid {
        return TarotGrid()
    }

    fun addRound(context: Context): TarotGrid {
        val newRoundNumber = roundCount + 1
        val newCell = GridCell(
            id = "round_$newRoundNumber",
            label = context.getString(R.string.tarot_round, newRoundNumber),
            value = null,
            category = "round"
        )
        return copy(
            cells = cells + newCell,
            roundCount = newRoundNumber
        )
    }

    companion object {
        fun createInitialCells(context: Context): List<GridCell> {
            return listOf(
                GridCell("round_1", context.getString(R.string.tarot_round, 1), null, category = "round")
            )
        }

        fun createInitialGrid(context: Context): TarotGrid {
            return TarotGrid(
                cells = createInitialCells(context),
                roundCount = 1
            )
        }
    }
}
