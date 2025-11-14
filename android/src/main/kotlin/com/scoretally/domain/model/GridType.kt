package com.scoretally.domain.model

enum class GridType {
    STANDARD,
    YAHTZEE,
    TAROT,
    RUMMY;

    companion object {
        fun fromString(value: String): GridType {
            return entries.find { it.name == value } ?: STANDARD
        }
    }
}
