package com.scoretally.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.UUID

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Ajouter les colonnes de synchronisation aux joueurs
        db.execSQL("ALTER TABLE players ADD COLUMN syncId TEXT NOT NULL DEFAULT '${UUID.randomUUID()}'")
        db.execSQL("ALTER TABLE players ADD COLUMN lastModifiedAt INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}")
        db.execSQL("ALTER TABLE players ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")

        // Ajouter les colonnes de synchronisation aux jeux
        db.execSQL("ALTER TABLE games ADD COLUMN syncId TEXT NOT NULL DEFAULT '${UUID.randomUUID()}'")
        db.execSQL("ALTER TABLE games ADD COLUMN lastModifiedAt INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}")
        db.execSQL("ALTER TABLE games ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")

        // Ajouter les colonnes de synchronisation aux matchs
        db.execSQL("ALTER TABLE matches ADD COLUMN syncId TEXT NOT NULL DEFAULT '${UUID.randomUUID()}'")
        db.execSQL("ALTER TABLE matches ADD COLUMN lastModifiedAt INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}")
        db.execSQL("ALTER TABLE matches ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE games ADD COLUMN diceCount INTEGER NOT NULL DEFAULT 1")
        db.execSQL("ALTER TABLE games ADD COLUMN diceFaces INTEGER NOT NULL DEFAULT 6")
    }
}

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE games ADD COLUMN diceCount INTEGER NOT NULL DEFAULT 1")
        db.execSQL("ALTER TABLE games ADD COLUMN diceFaces INTEGER NOT NULL DEFAULT 6")
    }
}

val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE games ADD COLUMN hasDice INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_10_11 = object : Migration(10, 11) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Ajouter la colonne bggId à la table games
        db.execSQL("ALTER TABLE games ADD COLUMN bggId TEXT DEFAULT NULL")
    }
}

val MIGRATION_11_12 = object : Migration(11, 12) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Ajouter la colonne isFavorite à la table games
        db.execSQL("ALTER TABLE games ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_12_13 = object : Migration(12, 13) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Mettre à jour les syncId des jeux prédéfinis existants pour utiliser des IDs fixes
        // Yahtzee / Yam's
        db.execSQL("""
            UPDATE games
            SET syncId = 'predefined-yahtzee'
            WHERE isPredefined = 1
            AND gridType = 'YAHTZEE'
        """)

        // Tarot
        db.execSQL("""
            UPDATE games
            SET syncId = 'predefined-tarot'
            WHERE isPredefined = 1
            AND gridType = 'TAROT'
        """)

        // Rummy / Rami
        db.execSQL("""
            UPDATE games
            SET syncId = 'predefined-rummy'
            WHERE isPredefined = 1
            AND gridType = 'RUMMY'
        """)
    }
}
