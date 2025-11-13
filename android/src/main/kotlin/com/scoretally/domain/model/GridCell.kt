package com.scoretally.domain.model

data class GridCell(
    val id: String,
    val label: String,
    val value: Int?,
    val isLocked: Boolean = false,
    val category: String? = null,
    val description: String? = null,
    val fixedValue: Int? = null
)
