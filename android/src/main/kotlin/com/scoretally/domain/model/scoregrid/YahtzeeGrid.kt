package com.scoretally.domain.model.scoregrid

import com.scoretally.domain.model.GridCell
import com.scoretally.domain.model.GridType

data class YahtzeeGrid(
    override val cells: List<GridCell> = createInitialCells()
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
        fun createInitialCells(): List<GridCell> {
            val upperSection = listOf(
                GridCell("ones", "As (1)", null, category = "upper", description = "Somme des 1"),
                GridCell("twos", "Deux (2)", null, category = "upper", description = "Somme des 2"),
                GridCell("threes", "Trois (3)", null, category = "upper", description = "Somme des 3"),
                GridCell("fours", "Quatre (4)", null, category = "upper", description = "Somme des 4"),
                GridCell("fives", "Cinq (5)", null, category = "upper", description = "Somme des 5"),
                GridCell("sixes", "Six (6)", null, category = "upper", description = "Somme des 6")
            )

            val lowerSection = listOf(
                GridCell("three_of_kind", "Brelan", null, category = "lower", description = "3 dés identiques"),
                GridCell("four_of_kind", "Carré", null, category = "lower", description = "4 dés identiques"),
                GridCell("full_house", "Full", null, category = "lower", description = "3 + 2 identiques = 25 pts", fixedValue = 25),
                GridCell("small_straight", "Petite suite", null, category = "lower", description = "4 dés consécutifs = 30 pts", fixedValue = 30),
                GridCell("large_straight", "Grande suite", null, category = "lower", description = "5 dés consécutifs = 40 pts", fixedValue = 40),
                GridCell("yahtzee", "Yam's", null, category = "lower", description = "5 dés identiques = 50 pts", fixedValue = 50),
                GridCell("chance", "Chance", null, category = "lower", description = "Somme de tous les dés")
            )

            return upperSection + lowerSection
        }
    }
}
