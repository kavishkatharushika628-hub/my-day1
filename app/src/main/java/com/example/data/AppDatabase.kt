package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [TaskEntity::class, FocusSessionEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tasks ADD COLUMN originalDateKey TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE tasks ADD COLUMN completedDateKey TEXT")
                db.execSQL("UPDATE tasks SET originalDateKey = strftime('%Y-%m-%d', createdAt / 1000, 'unixepoch', 'localtime') WHERE originalDateKey = ''")
                db.execSQL("UPDATE tasks SET completedDateKey = strftime('%Y-%m-%d', completedAt / 1000, 'unixepoch', 'localtime') WHERE isCompleted = 1 AND completedAt IS NOT NULL")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE INDEX IF NOT EXISTS index_tasks_originalDateKey ON tasks(originalDateKey)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_tasks_isCompleted ON tasks(isCompleted)")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "my_day_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
