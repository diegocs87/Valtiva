package com.example.valtiva.data.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Adds the `conversion_history` table introduced for the history feature.
 *
 * Everything else in the database — rates, favorites, currencies — is untouched, so upgrading must
 * not use `fallbackToDestructiveMigration`: that would silently wipe a user's saved favorites just
 * to make room for an unrelated new table.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
  override fun migrate(db: SupportSQLiteDatabase) {
    db.execSQL(
      """
      CREATE TABLE IF NOT EXISTS `conversion_history` (
        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
        `source_amount` TEXT NOT NULL,
        `base_currency` TEXT NOT NULL,
        `target_amount` TEXT NOT NULL,
        `quote_currency` TEXT NOT NULL,
        `rate` TEXT NOT NULL,
        `rate_date` TEXT NOT NULL,
        `timestamp` INTEGER NOT NULL
      )
      """.trimIndent(),
    )
  }
}
