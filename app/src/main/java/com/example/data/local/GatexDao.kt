package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GatexDao {

    // --- SUBJECTS & TOPICS ---
    @Query("SELECT * FROM subjects ORDER BY orderIndex ASC")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE id = :id")
    suspend fun getSubjectById(id: String): SubjectEntity?

    @Query("SELECT * FROM topics ORDER BY id ASC")
    fun getAllTopics(): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topics WHERE subjectId = :subjectId ORDER BY id ASC")
    fun getTopicsForSubject(subjectId: String): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topics WHERE id = :topicId")
    suspend fun getTopicById(topicId: String): TopicEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<SubjectEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopics(topics: List<TopicEntity>)

    @Query("UPDATE topics SET masteryLevel = :mastery WHERE id = :topicId")
    suspend fun updateTopicMastery(topicId: String, mastery: Double)

    // --- QUESTIONS ---
    @Query("SELECT * FROM questions ORDER BY id ASC")
    fun getAllQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun getQuestionById(id: Long): QuestionEntity?

    @Query("SELECT * FROM questions WHERE subjectId = :subjectId")
    fun getQuestionsBySubject(subjectId: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE topicId = :topicId")
    fun getQuestionsByTopic(topicId: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE source = 'OFFICIAL_PYQ' ORDER BY year DESC")
    fun getOfficialPYQs(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE isBookmarked = 1")
    fun getBookmarkedQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE questionText LIKE '%' || :query || '%' OR explanation LIKE '%' || :query || '%'")
    fun searchQuestions(query: String): Flow<List<QuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity): Long

    @Update
    suspend fun updateQuestion(question: QuestionEntity)

    @Query("DELETE FROM questions WHERE id = :id")
    suspend fun deleteQuestion(id: Long)

    @Query("UPDATE questions SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateQuestionBookmark(id: Long, isBookmarked: Boolean)

    // --- ATTEMPTS ---
    @Query("SELECT * FROM attempts ORDER BY timestamp DESC")
    fun getAllAttempts(): Flow<List<AttemptEntity>>

    @Query("SELECT * FROM attempts WHERE questionId = :questionId ORDER BY timestamp DESC")
    fun getAttemptsForQuestion(questionId: Long): Flow<List<AttemptEntity>>

    @Query("SELECT * FROM attempts ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentAttempts(limit: Int): Flow<List<AttemptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: AttemptEntity): Long

    // --- MISTAKES ---
    @Query("SELECT * FROM mistakes ORDER BY dateAdded DESC")
    fun getAllMistakes(): Flow<List<MistakeEntity>>

    @Query("SELECT * FROM mistakes WHERE isResolved = 0 ORDER BY dateAdded DESC")
    fun getUnresolvedMistakes(): Flow<List<MistakeEntity>>

    @Query("SELECT * FROM mistakes WHERE questionId = :questionId LIMIT 1")
    suspend fun getMistakeByQuestionId(questionId: Long): MistakeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMistake(mistake: MistakeEntity): Long

    @Update
    suspend fun updateMistake(mistake: MistakeEntity)

    @Query("UPDATE mistakes SET isResolved = :isResolved WHERE id = :id")
    suspend fun setMistakeResolved(id: Long, isResolved: Boolean)

    @Query("DELETE FROM mistakes WHERE id = :id")
    suspend fun deleteMistake(id: Long)

    // --- REVISIONS (SPACED REPETITION) ---
    @Query("SELECT * FROM revisions ORDER BY nextReviewDate ASC")
    fun getAllRevisions(): Flow<List<RevisionEntity>>

    @Query("SELECT * FROM revisions WHERE nextReviewDate <= :currentTime ORDER BY nextReviewDate ASC")
    fun getDueRevisions(currentTime: Long): Flow<List<RevisionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRevision(revision: RevisionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRevisions(revisions: List<RevisionEntity>)

    @Update
    suspend fun updateRevision(revision: RevisionEntity)

    // --- FLASHCARDS ---
    @Query("SELECT * FROM flashcards ORDER BY id ASC")
    fun getAllFlashcards(): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards WHERE topicId = :topicId")
    fun getFlashcardsByTopic(topicId: String): Flow<List<FlashcardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcards(flashcards: List<FlashcardEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: FlashcardEntity): Long

    @Update
    suspend fun updateFlashcard(flashcard: FlashcardEntity)

    // --- FORMULAS ---
    @Query("SELECT * FROM formulas ORDER BY subjectId, title ASC")
    fun getAllFormulas(): Flow<List<FormulaEntity>>

    @Query("SELECT * FROM formulas WHERE subjectId = :subjectId")
    fun getFormulasBySubject(subjectId: String): Flow<List<FormulaEntity>>

    @Query("SELECT * FROM formulas WHERE title LIKE '%' || :query || '%' OR formulaLatex LIKE '%' || :query || '%'")
    fun searchFormulas(query: String): Flow<List<FormulaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFormulas(formulas: List<FormulaEntity>)

    @Query("UPDATE formulas SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFormulaFavorite(id: Long, isFavorite: Boolean)

    // --- STUDY TASKS & SESSIONS & DAILY LOGS ---
    @Query("SELECT * FROM daily_study_logs ORDER BY date ASC")
    fun getAllDailyStudyLogs(): Flow<List<DailyStudyLogEntity>>

    @Query("SELECT * FROM daily_study_logs WHERE date = :date LIMIT 1")
    suspend fun getDailyStudyLogForDate(date: String): DailyStudyLogEntity?

    @Query("SELECT * FROM daily_study_logs ORDER BY date DESC LIMIT :days")
    fun getRecentDailyStudyLogs(days: Int): Flow<List<DailyStudyLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyStudyLog(log: DailyStudyLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyStudyLogs(logs: List<DailyStudyLogEntity>)

    @Query("SELECT * FROM study_tasks WHERE targetDate = :date ORDER BY id ASC")
    fun getTasksForDate(date: String): Flow<List<StudyTaskEntity>>

    @Query("SELECT * FROM study_tasks ORDER BY id DESC")
    fun getAllTasks(): Flow<List<StudyTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: StudyTaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<StudyTaskEntity>)

    @Query("UPDATE study_tasks SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateTaskStatus(id: Long, isCompleted: Boolean)

    @Query("SELECT * FROM study_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<StudySessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySessionEntity): Long

    // --- MOCK TESTS & ATTEMPTS ---
    @Query("SELECT * FROM mock_tests ORDER BY id ASC")
    fun getAllMockTests(): Flow<List<MockTestEntity>>

    @Query("SELECT * FROM mock_tests WHERE id = :id")
    suspend fun getMockTestById(id: Long): MockTestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMockTest(test: MockTestEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMockTests(tests: List<MockTestEntity>)

    @Query("SELECT * FROM mock_attempts ORDER BY timestamp DESC")
    fun getAllMockAttempts(): Flow<List<MockAttemptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMockAttempt(attempt: MockAttemptEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMockAttempts(attempts: List<MockAttemptEntity>)

    // --- DOCUMENT NOTES ---
    @Query("SELECT * FROM document_notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<DocumentNoteEntity>>

    @Query("SELECT * FROM document_notes WHERE topicId = :topicId")
    fun getNotesByTopic(topicId: String): Flow<List<DocumentNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: DocumentNoteEntity): Long

    @Query("DELETE FROM document_notes WHERE id = :id")
    suspend fun deleteNote(id: Long)

    // --- AGENT LOGS ---
    @Query("SELECT * FROM agent_logs ORDER BY timestamp DESC LIMIT 50")
    fun getRecentAgentLogs(): Flow<List<AgentLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgentLog(log: AgentLogEntity): Long

    // --- USER PROFILE ---
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileDirect(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfileEntity)

    @Update
    suspend fun updateUserProfile(profile: UserProfileEntity)

    // --- REMINDER SCHEDULES & NOTIFICATIONS ---
    @Query("SELECT * FROM reminder_schedules ORDER BY hour ASC, minute ASC")
    fun getAllReminders(): Flow<List<ReminderScheduleEntity>>

    @Query("SELECT * FROM reminder_schedules WHERE isEnabled = 1 ORDER BY hour ASC, minute ASC")
    fun getEnabledReminders(): Flow<List<ReminderScheduleEntity>>

    @Query("SELECT * FROM reminder_schedules WHERE isEnabled = 1")
    suspend fun getEnabledRemindersDirect(): List<ReminderScheduleEntity>

    @Query("SELECT * FROM reminder_schedules WHERE id = :id")
    suspend fun getReminderById(id: Long): ReminderScheduleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderScheduleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminders(reminders: List<ReminderScheduleEntity>)

    @Update
    suspend fun updateReminder(reminder: ReminderScheduleEntity)

    @Query("UPDATE reminder_schedules SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun setReminderEnabled(id: Long, isEnabled: Boolean)

    @Query("DELETE FROM reminder_schedules WHERE id = :id")
    suspend fun deleteReminder(id: Long)
}
