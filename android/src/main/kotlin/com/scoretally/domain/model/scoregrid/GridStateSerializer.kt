package com.scoretally.domain.model.scoregrid

import com.scoretally.domain.model.GridCell
import org.json.JSONObject

object GridStateSerializer {

    fun serializeToJson(cells: List<GridCell>): String {
        val jsonObject = JSONObject()
        cells.forEach { cell ->
            if (cell.value != null) {
                jsonObject.put(cell.id, cell.value)
            }
        }
        return jsonObject.toString()
    }

    fun deserializeFromJson(json: String?): Map<String, Int> {
        if (json.isNullOrBlank()) return emptyMap()

        val result = mutableMapOf<String, Int>()
        try {
            val jsonObject = JSONObject(json)
            jsonObject.keys().forEach { key ->
                result[key] = jsonObject.getInt(key)
            }
        } catch (e: Exception) {
            // Si le JSON est invalide, retourner une map vide
            return emptyMap()
        }
        return result
    }

    fun applyStateToGrid(grid: ScoreGrid, state: Map<String, Int>): ScoreGrid {
        var updatedGrid = grid
        state.forEach { (cellId, value) ->
            updatedGrid = updatedGrid.updateCell(cellId, value)
        }
        return updatedGrid
    }
}
