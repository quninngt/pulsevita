package com.healthapp.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room database migrations.
 *
 * When adding a new migration:
 * 1. Increment the database version in @Database annotation
 * 2. Add a Migration(oldVersion, newVersion) object here
 * 3. Register it in AppDatabase.getDatabase() via .addMigrations(...)
 *
 * Schema JSON files are exported to app/schemas/ for validation.
 */
object Migrations {

    // Example: Migration from version 1 to 2
    // Uncomment and adapt when schema changes are needed.
    //
    // val MIGRATION_1_2 = object : Migration(1, 2) {
    //     override fun migrate(db: SupportSQLiteDatabase) {
    //         db.execSQL("ALTER TABLE water_record ADD COLUMN note TEXT DEFAULT NULL")
    //     }
    // }
}
