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
