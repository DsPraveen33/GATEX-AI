package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        SubjectEntity::class,
        TopicEntity::class,
        QuestionEntity::class,
        AttemptEntity::class,
        MistakeEntity::class,
        RevisionEntity::class,
        FlashcardEntity::class,
        FormulaEntity::class,
        StudyTaskEntity::class,
        StudySessionEntity::class,
        DailyStudyLogEntity::class,
        MockTestEntity::class,
        MockAttemptEntity::class,
        DocumentNoteEntity::class,
        AgentLogEntity::class,
        UserProfileEntity::class,
        ReminderScheduleEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class GatexDatabase : RoomDatabase() {
    abstract fun gatexDao(): GatexDao

    companion object {
        @Volatile
        private var INSTANCE: GatexDatabase? = null

        fun getDatabase(context: Context): GatexDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GatexDatabase::class.java,
                    "gatex_ai_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
