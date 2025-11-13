package com.scoretally.domain.model

enum class GridType {
    STANDARD,
    YAHTZEE;

    companion object {
        fun fromString(value: String): GridType {
            return values().find { it.name == value } ?: STANDARD
        }
    }
}
