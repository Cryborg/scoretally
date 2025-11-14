package com.scoretally.domain.model.scoregrid

import android.content.Context
import com.scoretally.R
import com.scoretally.domain.model.GridCell
import com.scoretally.domain.model.GridType

data class YahtzeeGrid(
    override val cells: List<GridCell> = emptyList()
) : ScoreGrid {

    override val type: GridType = GridType.YAHTZEE

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
        return cells.all { it.value != null }
    }

    override fun reset(): ScoreGrid {
        return YahtzeeGrid()
    }

    fun calculateUpperSectionTotal(): Int {
        return cells.filter { it.category == "upper" }.sumOf { it.value ?: 0 }
    }

    fun calculateUpperSectionBonus(): Int {
        return if (calculateUpperSectionTotal() >= 63) 35 else 0
    }

    fun calculateLowerSectionTotal(): Int {
        return cells.filter { it.category == "lower" }.sumOf { it.value ?: 0 }
    }

    companion object {
        fun createInitialCells(context: Context): List<GridCell> {
            val upperSection = listOf(
                GridCell("ones", context.getString(R.string.yahtzee_ones), null, category = "upper"),
                GridCell("twos", context.getString(R.string.yahtzee_twos), null, category = "upper"),
                GridCell("threes", context.getString(R.string.yahtzee_threes), null, category = "upper"),
                GridCell("fours", context.getString(R.string.yahtzee_fours), null, category = "upper"),
                GridCell("fives", context.getString(R.string.yahtzee_fives), null, category = "upper"),
                GridCell("sixes", context.getString(R.string.yahtzee_sixes), null, category = "upper")
            )

            val lowerSection = listOf(
                GridCell("three_of_kind", context.getString(R.string.yahtzee_three_of_kind), null, category = "lower"),
                GridCell("four_of_kind", context.getString(R.string.yahtzee_four_of_kind), null, category = "lower"),
                GridCell("full_house", context.getString(R.string.yahtzee_full_house), null, category = "lower", fixedValue = 25),
                GridCell("small_straight", context.getString(R.string.yahtzee_small_straight), null, category = "lower", fixedValue = 30),
                GridCell("large_straight", context.getString(R.string.yahtzee_large_straight), null, category = "lower", fixedValue = 40),
                GridCell("yahtzee", context.getString(R.string.yahtzee_yahtzee), null, category = "lower", fixedValue = 50),
                GridCell("chance", context.getString(R.string.yahtzee_chance), null, category = "lower")
            )

            return upperSection + lowerSection
        }
    }
}
