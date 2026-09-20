package com.example.data.repository

import com.example.data.local.GatexDao
import com.example.data.local.SeedData
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class GatexRepository(private val dao: GatexDao) {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedDatabaseIfEmpty()
        }
    }

    private suspend fun seedDatabaseIfEmpty() {
        val existingSubjects = dao.getAllSubjects().firstOrNull()
        if (existingSubjects.isNullOrEmpty()) {
            dao.insertSubjects(SeedData.getInitialSubjects())
            dao.insertTopics(SeedData.getInitialTopics())
            dao.insertQuestions(SeedData.getInitialQuestions())
            dao.insertFormulas(SeedData.getInitialFormulas())
            dao.insertFlashcards(SeedData.getInitialFlashcards())
            dao.insertTasks(SeedData.getInitialStudyTasks())
            dao.insertMockTests(SeedData.getInitialMockTests())
            dao.insertMockAttempts(SeedData.getInitialMockAttempts())
            dao.insertDailyStudyLogs(SeedData.getInitialDailyStudyLogs())
            dao.insertReminders(SeedData.getInitialReminders())

            // Initialize default clean profile (Day 1 / Zero start)
            dao.insertUserProfile(
                UserProfileEntity(
                    id = 1,
                    name = "GATE Aspirant",
                    targetExam = "GATE 2027 CSE",
                    targetExamDate = "2027-02-06",
                    dailyStudyHoursGoal = 4,
                    preferredLanguage = "English",
                    tutorMode = "Exam Coach",
                    isDiagnosticCompleted = false,
                    streakDays = 0,
                    lastActiveDate = ""
                )
            )
        }
    }

    suspend fun resetAllUserData() = withContext(Dispatchers.IO) {
        // Reset all topics to 0% mastery
        val topics = dao.getAllTopics().firstOrNull() ?: emptyList()
        topics.forEach { dao.updateTopicMastery(it.id, 0.0) }

        // Clear all mock attempts and daily study logs
        // Reset user profile streak
        val profile = dao.getUserProfile().firstOrNull()
        if (profile != null) {
            dao.updateUserProfile(profile.copy(streakDays = 0, lastActiveDate = ""))
        }
    }

    // --- SUBJECTS & TOPICS ---
    val allSubjects: Flow<List<SubjectEntity>> = dao.getAllSubjects()
    val allTopics: Flow<List<TopicEntity>> = dao.getAllTopics()

    suspend fun updateTopicMastery(topicId: String, mastery: Double) = dao.updateTopicMastery(topicId, mastery)

    fun getTopicsForSubject(subjectId: String): Flow<List<TopicEntity>> = dao.getTopicsForSubject(subjectId)
    suspend fun getTopicById(topicId: String): TopicEntity? = dao.getTopicById(topicId)
    suspend fun getSubjectById(subjectId: String): SubjectEntity? = dao.getSubjectById(subjectId)

    // --- QUESTIONS ---
    val allQuestions: Flow<List<QuestionEntity>> = dao.getAllQuestions()
    val officialPYQs: Flow<List<QuestionEntity>> = dao.getOfficialPYQs()
    val bookmarkedQuestions: Flow<List<QuestionEntity>> = dao.getBookmarkedQuestions()

    fun getQuestionsBySubject(subjectId: String): Flow<List<QuestionEntity>> = dao.getQuestionsBySubject(subjectId)
    fun getQuestionsByTopic(topicId: String): Flow<List<QuestionEntity>> = dao.getQuestionsByTopic(topicId)
    fun searchQuestions(query: String): Flow<List<QuestionEntity>> = dao.searchQuestions(query)
    suspend fun getQuestionById(id: Long): QuestionEntity? = dao.getQuestionById(id)
    suspend fun toggleBookmark(id: Long, isBookmarked: Boolean) = dao.updateQuestionBookmark(id, isBookmarked)
    suspend fun insertQuestion(question: QuestionEntity): Long = dao.insertQuestion(question)
    suspend fun insertQuestions(questions: List<QuestionEntity>) = dao.insertQuestions(questions)
    suspend fun updateQuestion(question: QuestionEntity) = dao.updateQuestion(question)
    suspend fun deleteQuestion(id: Long) = dao.deleteQuestion(id)

    // --- ATTEMPTS & MISTAKES ---
    val allAttempts: Flow<List<AttemptEntity>> = dao.getAllAttempts()
    val allMistakes: Flow<List<MistakeEntity>> = dao.getAllMistakes()
    val unresolvedMistakes: Flow<List<MistakeEntity>> = dao.getUnresolvedMistakes()

    suspend fun recordAttempt(
        question: QuestionEntity,
        userResponse: String,
        isCorrect: Boolean,
        timeSpentSeconds: Int,
        confidenceLevel: Int,
        mistakeCategoryIfWrong: MistakeCategory = MistakeCategory.CONCEPTUAL,
        userMistakeNote: String = ""
    ): Long = withContext(Dispatchers.IO) {
        val attemptId = dao.insertAttempt(
            AttemptEntity(
                questionId = question.id,
                userResponse = userResponse,
                isCorrect = isCorrect,
                timeSpentSeconds = timeSpentSeconds,
                confidenceLevel = confidenceLevel
            )
        )

        // If incorrect, automatically register in Mistake Book
        if (!isCorrect) {
            val existing = dao.getMistakeByQuestionId(question.id)
            if (existing != null) {
                dao.updateMistake(
                    existing.copy(
                        retryCount = existing.retryCount + 1,
                        isResolved = false,
                        category = mistakeCategoryIfWrong,
                        userNotes = if (userMistakeNote.isNotBlank()) userMistakeNote else existing.userNotes,
                        lastRetriedAt = System.currentTimeMillis()
                    )
                )
            } else {
                dao.insertMistake(
                    MistakeEntity(
                        questionId = question.id,
                        category = mistakeCategoryIfWrong,
                        userNotes = userMistakeNote.ifBlank { "Incorrect attempt on ${question.topicId}" },
                        retryCount = 0,
                        isResolved = false
                    )
                )
            }
        } else {
            // If correct and in mistake book, mark resolved
            val existing = dao.getMistakeByQuestionId(question.id)
            if (existing != null && !existing.isResolved) {
                dao.setMistakeResolved(existing.id, true)
            }
        }

        // Re-calculate topic mastery
        updateMasteryForTopic(question.topicId)

        attemptId
    }

    private suspend fun updateMasteryForTopic(topicId: String) {
        // Evidence-based mastery calculation based on accuracy and attempts
        val questions = dao.getQuestionsByTopic(topicId).firstOrNull() ?: emptyList()
        if (questions.isEmpty()) return

        var totalCorrect = 0
        var totalAttempts = 0

        for (q in questions) {
            val attempts = dao.getAttemptsForQuestion(q.id).firstOrNull() ?: emptyList()
            if (attempts.isNotEmpty()) {
                totalAttempts += attempts.size
                if (attempts.first().isCorrect) {
                    totalCorrect += 1
                }
            }
        }

        val accuracy = if (totalAttempts > 0) (totalCorrect.toDouble() / questions.size) * 100.0 else 0.0
        val clampedMastery = accuracy.coerceIn(0.0, 100.0)
        dao.updateTopicMastery(topicId, clampedMastery)
    }

    suspend fun resolveMistake(mistakeId: Long, isResolved: Boolean) = dao.setMistakeResolved(mistakeId, isResolved)
    suspend fun deleteMistake(mistakeId: Long) = dao.deleteMistake(mistakeId)

    // --- REVISIONS ---
    val allRevisions: Flow<List<RevisionEntity>> = dao.getAllRevisions()
    fun getDueRevisions(currentTime: Long = System.currentTimeMillis()): Flow<List<RevisionEntity>> =
        dao.getDueRevisions(currentTime)

    suspend fun reviewRevision(revision: RevisionEntity, rating: String) = withContext(Dispatchers.IO) {
        // Spaced repetition SM-2 adjustment: "Again", "Hard", "Good", "Easy"
        val (newInterval, newEase, newState) = when (rating) {
            "Again" -> Triple(1, (revision.easeFactor - 0.2).coerceAtLeast(1.3), RevisionState.RELEARN)
            "Hard" -> Triple((revision.intervalDays * 1.2).toInt().coerceAtLeast(2), (revision.easeFactor - 0.15).coerceAtLeast(1.3), RevisionState.REVIEW)
            "Good" -> Triple((revision.intervalDays * revision.easeFactor).toInt().coerceAtLeast(3), revision.easeFactor, RevisionState.REVIEW)
            "Easy" -> Triple((revision.intervalDays * revision.easeFactor * 1.3).toInt().coerceAtLeast(5), (revision.easeFactor + 0.15), RevisionState.MASTERED)
            else -> Triple(1, revision.easeFactor, RevisionState.REVIEW)
        }

        val nextDate = System.currentTimeMillis() + (newInterval * 24L * 60L * 60L * 1000L)
        dao.updateRevision(
            revision.copy(
                intervalDays = newInterval,
                easeFactor = newEase,
                state = newState,
                nextReviewDate = nextDate,
                lastReviewedDate = System.currentTimeMillis(),
                successStreak = if (rating == "Again") 0 else revision.successStreak + 1,
                failureCount = if (rating == "Again") revision.failureCount + 1 else revision.failureCount
            )
        )
    }

    suspend fun insertRevision(revision: RevisionEntity) = dao.insertRevision(revision)

    // --- FLASHCARDS & FORMULAS ---
    val allFlashcards: Flow<List<FlashcardEntity>> = dao.getAllFlashcards()
    val allFormulas: Flow<List<FormulaEntity>> = dao.getAllFormulas()

    fun getFormulasBySubject(subjectId: String): Flow<List<FormulaEntity>> = dao.getFormulasBySubject(subjectId)
    fun searchFormulas(query: String): Flow<List<FormulaEntity>> = dao.searchFormulas(query)
    suspend fun toggleFormulaFavorite(id: Long, isFavorite: Boolean) = dao.setFormulaFavorite(id, isFavorite)
    suspend fun insertFlashcard(flashcard: FlashcardEntity) = dao.insertFlashcard(flashcard)
    suspend fun insertFormula(formula: FormulaEntity) = dao.insertFormulas(listOf(formula))

    // --- STUDY TASKS & RESCUE MODE ---
    fun getTasksForDate(date: String): Flow<List<StudyTaskEntity>> = dao.getTasksForDate(date)
    val allTasks: Flow<List<StudyTaskEntity>> = dao.getAllTasks()

    suspend fun setTaskCompleted(taskId: Long, isCompleted: Boolean) = dao.updateTaskStatus(taskId, isCompleted)
    suspend fun insertTask(task: StudyTaskEntity) = dao.insertTask(task)

    suspend fun activateRescueMode(): List<StudyTaskEntity> = withContext(Dispatchers.IO) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        // Generate high-yield recovery tasks focused on weak topics & due revisions
        val rescueTasks = listOf(
            StudyTaskEntity(
                title = "🚨 RESCUE: High-Yield OS Deadlocks Formula & Trap Drill",
                subjectId = "os",
                topicId = "os_deadlocks",
                durationMinutes = 25,
                taskType = "REVISION",
                isCompleted = false,
                targetDate = today,
                isRescueTask = true
            ),
            StudyTaskEntity(
                title = "🚨 RESCUE: DBMS Normalization 10-Minute Rapid Check",
                subjectId = "dbms",
                topicId = "dbms_normalization",
                durationMinutes = 20,
                taskType = "PRACTICE",
                isCompleted = false,
                targetDate = today,
                isRescueTask = true
            ),
            StudyTaskEntity(
                title = "🚨 RESCUE: Clear Top 3 Overdue Mistake Book Items",
                subjectId = "cn",
                topicId = "cn_tcp",
                durationMinutes = 20,
                taskType = "REVISION",
                isCompleted = false,
                targetDate = today,
                isRescueTask = true
            )
        )
        dao.insertTasks(rescueTasks)
        rescueTasks
    }

    // --- STUDY SESSIONS & DAILY STUDY LOGS ---
    val allSessions: Flow<List<StudySessionEntity>> = dao.getAllSessions()
    suspend fun recordStudySession(session: StudySessionEntity) = dao.insertSession(session)

    val allDailyStudyLogs: Flow<List<DailyStudyLogEntity>> = dao.getAllDailyStudyLogs()
    fun getRecentDailyStudyLogs(days: Int = 7): Flow<List<DailyStudyLogEntity>> = dao.getRecentDailyStudyLogs(days)
    suspend fun getDailyStudyLogForDate(date: String): DailyStudyLogEntity? = dao.getDailyStudyLogForDate(date)

    suspend fun logDailyStudyHours(
        date: String,
        additionalHours: Double,
        additionalQuestions: Int = 0,
        subject: String = "",
        notes: String = ""
    ) = withContext(Dispatchers.IO) {
        val existing = dao.getDailyStudyLogForDate(date)
        val profile = dao.getUserProfileDirect()
        val defaultTarget = profile?.dailyStudyHoursGoal?.toDouble() ?: 4.0

        if (existing != null) {
            val updatedSubjects = if (subject.isNotBlank()) {
                if (existing.subjectsStudied.isBlank()) subject else "${existing.subjectsStudied}, $subject"
            } else existing.subjectsStudied

            val updatedNotes = if (notes.isNotBlank()) {
                if (existing.notes.isBlank()) notes else "${existing.notes} | $notes"
            } else existing.notes

            val newHours = existing.hoursStudied + additionalHours
            val newScore = if (newHours >= existing.targetHours) 95 else 80

            dao.insertDailyStudyLog(
                existing.copy(
                    hoursStudied = newHours,
                    questionsSolved = existing.questionsSolved + additionalQuestions,
                    subjectsStudied = updatedSubjects,
                    focusScore = newScore,
                    notes = updatedNotes,
                    timestamp = System.currentTimeMillis()
                )
            )
        } else {
            dao.insertDailyStudyLog(
                DailyStudyLogEntity(
                    date = date,
                    hoursStudied = additionalHours,
                    targetHours = defaultTarget,
                    subjectsStudied = subject,
                    questionsSolved = additionalQuestions,
                    focusScore = if (additionalHours >= defaultTarget) 95 else 80,
                    notes = notes,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun setDailyTargetHours(date: String, targetHours: Double) = withContext(Dispatchers.IO) {
        val existing = dao.getDailyStudyLogForDate(date)
        if (existing != null) {
            dao.insertDailyStudyLog(existing.copy(targetHours = targetHours))
        } else {
            dao.insertDailyStudyLog(
                DailyStudyLogEntity(
                    date = date,
                    hoursStudied = 0.0,
                    targetHours = targetHours
                )
            )
        }
    }

    // --- MOCKS ---
    val allMockTests: Flow<List<MockTestEntity>> = dao.getAllMockTests()
    val allMockAttempts: Flow<List<MockAttemptEntity>> = dao.getAllMockAttempts()
    suspend fun getMockTestById(id: Long): MockTestEntity? = dao.getMockTestById(id)
    suspend fun insertMockTest(mock: MockTestEntity): Long = dao.insertMockTest(mock)
    suspend fun recordMockAttempt(attempt: MockAttemptEntity) = dao.insertMockAttempt(attempt)

    // --- DOCUMENT NOTES ---
    val allNotes: Flow<List<DocumentNoteEntity>> = dao.getAllNotes()
    suspend fun insertNote(note: DocumentNoteEntity) = dao.insertNote(note)
    suspend fun deleteNote(id: Long) = dao.deleteNote(id)

    // --- AGENT LOGS ---
    val recentAgentLogs: Flow<List<AgentLogEntity>> = dao.getRecentAgentLogs()
    suspend fun logAgentRun(agentName: String, prompt: String, summary: String, durationMs: Long, status: String) {
        dao.insertAgentLog(
            AgentLogEntity(
                agentName = agentName,
                prompt = prompt,
                responseSummary = summary,
                durationMs = durationMs,
                status = status
            )
        )
    }

    // --- USER PROFILE ---
    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
    suspend fun getUserProfile(): UserProfileEntity? = dao.getUserProfileDirect()
    suspend fun updateUserProfile(profile: UserProfileEntity) = dao.updateUserProfile(profile)

    // --- REMINDER SCHEDULES & NOTIFICATIONS ---
    val allReminders: Flow<List<ReminderScheduleEntity>> = dao.getAllReminders()
    val enabledReminders: Flow<List<ReminderScheduleEntity>> = dao.getEnabledReminders()
    suspend fun getEnabledRemindersDirect(): List<ReminderScheduleEntity> = dao.getEnabledRemindersDirect()
    suspend fun getReminderById(id: Long): ReminderScheduleEntity? = dao.getReminderById(id)
    suspend fun insertReminder(reminder: ReminderScheduleEntity): Long = dao.insertReminder(reminder)
    suspend fun updateReminder(reminder: ReminderScheduleEntity) = dao.updateReminder(reminder)
    suspend fun setReminderEnabled(id: Long, isEnabled: Boolean) = dao.setReminderEnabled(id, isEnabled)
    suspend fun deleteReminder(id: Long) = dao.deleteReminder(id)
}
