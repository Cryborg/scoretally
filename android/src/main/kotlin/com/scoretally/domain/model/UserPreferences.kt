package com.scoretally.domain.model

data class UserPreferences(
    val language: AppLanguage = AppLanguage.SYSTEM,
    val theme: AppTheme = AppTheme.SYSTEM,
    val autoSyncEnabled: Boolean = true,
    val lastSyncTimestamp: Long = 0
)

enum class AppLanguage(val code: String) {
    SYSTEM("system"),
    ENGLISH("en"),
    FRENCH("fr"),
    SPANISH("es"),
    GERMAN("de"),
    ITALIAN("it");

    companion object {
        fun fromCode(code: String): AppLanguage =
            entries.find { it.code == code } ?: SYSTEM
    }
}

enum class AppTheme {
    SYSTEM,
    LIGHT,
    DARK,
    CARTOON;

    companion object {
        fun fromName(name: String): AppTheme =
            entries.find { it.name == name } ?: SYSTEM
    }
}
