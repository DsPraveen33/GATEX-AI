package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// --- 1. SYLLABUS & TOPICS ---
@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey val id: String, // e.g. "os", "algo", "dbms"
    val name: String,
    val code: String,
    val iconName: String,
    val totalWeightage: Double, // Approx % in GATE CSE (e.g. 9.0)
    val colorHex: String,
    val orderIndex: Int
)

@Entity(tableName = "topics")
data class TopicEntity(
    @PrimaryKey val id: String, // e.g. "os_deadlocks"
    val subjectId: String,
    val name: String,
    val subtopicsList: String, // Pipe or comma separated subtopics
    val importance: String, // HIGH, MEDIUM, LOW
    val estimatedHours: Int,
    val masteryLevel: Double = 0.0 // 0.0 to 100.0 calculated evidence-based
)

// --- 2. QUESTIONS (PYQs & PRACTICE) ---
enum class QuestionType { MCQ, MSQ, NAT }
enum class QuestionSource { OFFICIAL_PYQ, GATEOVERFLOW_RESOURCE, AI_PRACTICE, USER_CREATED }
enum class Difficulty { EASY, MEDIUM, HARD }

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: String,
    val topicId: String,
    val questionText: String,
    val questionType: QuestionType,
    val optionsJson: String, // JSON array of options e.g. ["A","B","C","D"]
    val correctAnswer: String, // "A" or "A,C" or numerical range "14.5" / "14.5:15.5"
    val explanation: String,
    val commonTrap: String = "",
    val formulaUsed: String = "",
    val source: QuestionSource,
    val year: Int = 0, // 0 for AI Practice, e.g. 2024 for GATE 2024
    val marks: Int = 1, // 1 or 2
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val isBookmarked: Boolean = false,
    val verifiedStatus: String = "PUBLISHED" // PENDING, VALIDATED, PUBLISHED
)

// --- 3. ATTEMPTS & PERFORMANCE ---
@Entity(tableName = "attempts")
data class AttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val questionId: Long,
    val userResponse: String,
    val isCorrect: Boolean,
    val timeSpentSeconds: Int,
    val confidenceLevel: Int, // 1: Guess, 2: Low, 3: Medium, 4: High, 5: Certain
    val timestamp: Long = System.currentTimeMillis(),
    val isMockAttempt: Boolean = false,
    val mockAttemptId: Long = 0
)

// --- 4. MISTAKE BOOK ---
enum class MistakeCategory {
    CONCEPTUAL,
    CALCULATION,
    MEMORY,
    READING,
    CARELESS,
    GUESS,
    TIME_PRESSURE,
    FORMULA,
    LOGIC,
    IMPLEMENTATION
}

@Entity(tableName = "mistakes")
data class MistakeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val questionId: Long,
    val category: MistakeCategory,
    val userNotes: String,
    val aiInsight: String = "",
    val retryCount: Int = 0,
    val isResolved: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis(),
    val lastRetriedAt: Long = 0
)

// --- 5. SPACED REPETITION & FLASHCARDS ---
enum class RevisionState { NEW, LEARNING, REVIEW, MASTERED, RELEARN }

@Entity(tableName = "revisions")
data class RevisionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topicId: String,
    val conceptTitle: String,
    val keySummary: String,
    val state: RevisionState = RevisionState.NEW,
    val intervalDays: Int = 1,
    val easeFactor: Double = 2.5,
    val nextReviewDate: Long = System.currentTimeMillis(),
    val lastReviewedDate: Long = 0,
    val successStreak: Int = 0,
    val failureCount: Int = 0
)

@Entity(tableName = "flashcards")
data class FlashcardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: String,
    val topicId: String,
    val front: String,
    val back: String,
    val cardType: String = "CONCEPT", // FORMULA, SHORTCUT, DEFINITION, MISTAKE
    val masteryCount: Int = 0,
    val isFavorite: Boolean = false
)

@Entity(tableName = "formulas")
data class FormulaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: String,
    val topicId: String,
    val title: String,
    val formulaLatex: String,
    val variablesExplanation: String,
    val practicalExample: String = "",
    val isFavorite: Boolean = false
)

// --- 6. STUDY PLANS & SESSIONS ---
@Entity(tableName = "daily_study_logs")
data class DailyStudyLogEntity(
    @PrimaryKey val date: String, // "YYYY-MM-DD" e.g. "2026-09-18"
    val hoursStudied: Double, // e.g. 4.5 hours
    val targetHours: Double, // e.g. 4.0 or 6.0 hours
    val subjectsStudied: String = "", // e.g. "OS, DBMS, ALGO"
    val questionsSolved: Int = 0,
    val focusScore: Int = 100, // 0 to 100
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "study_tasks")
data class StudyTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subjectId: String,
    val topicId: String,
    val durationMinutes: Int,
    val taskType: String, // "STUDY", "PYQ", "PRACTICE", "REVISION", "MOCK"
    val isCompleted: Boolean = false,
    val targetDate: String, // "YYYY-MM-DD"
    val isRescueTask: Boolean = false
)

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: String,
    val topicId: String,
    val activity: String, // STUDY, PRACTICE, PYQ, REVISION, MOCK, CODING
    val focusedMinutes: Int,
    val questionsSolved: Int = 0,
    val correctCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

// --- 7. MOCK TESTS ---
@Entity(tableName = "mock_tests")
data class MockTestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val testType: String, // "FULL_GATE", "SUBJECT_TEST", "TOPIC_TEST", "CUSTOM_MOCK"
    val durationMinutes: Int = 180,
    val totalQuestions: Int = 65,
    val totalMarks: Double = 100.0,
    val questionIdsJson: String // JSON array of question IDs
)

@Entity(tableName = "mock_attempts")
data class MockAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mockTestId: Long,
    val score: Double,
    val totalMarks: Double,
    val attemptedCount: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val totalTimeSeconds: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val sectionBreakdownJson: String = ""
)

// --- 8. NOTES & DOCUMENT KNOWLEDGE BASE ---
@Entity(tableName = "document_notes")
data class DocumentNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subjectId: String,
    val topicId: String,
    val content: String,
    val tags: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

// --- 9. AGENT LOGS & USER PROFILE ---
@Entity(tableName = "agent_logs")
data class AgentLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val agentName: String,
    val prompt: String,
    val responseSummary: String,
    val durationMs: Long,
    val status: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "GATE Aspirant",
    val targetExam: String = "GATE 2027 CSE",
    val targetExamDate: String = "2027-02-06",
    val dailyStudyHoursGoal: Int = 4,
    val preferredLanguage: String = "English", // "English", "Telugu", "Telugu + English"
    val tutorMode: String = "Exam Coach",
    val isDiagnosticCompleted: Boolean = false,
    val streakDays: Int = 1,
    val lastActiveDate: String = ""
)

// --- 10. NOTIFICATION SCHEDULES & REMINDERS ---
enum class ReminderType {
    DAILY_PRACTICE,
    MOCK_TEST,
    SPACED_REVISION,
    CUSTOM_SESSION
}

@Entity(tableName = "reminder_schedules")
data class ReminderScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val type: ReminderType = ReminderType.DAILY_PRACTICE,
    val targetId: String = "", // e.g. mock test id "1" or subject id "os"
    val targetName: String = "", // e.g. "Full GATE CSE Mock #1" or "Operating Systems"
    val hour: Int = 19, // 0-23
    val minute: Int = 30, // 0-59
    val scheduledDate: String = "", // "YYYY-MM-DD" for one-off mock dates, empty if repeating
    val daysOfWeek: String = "EVERYDAY", // "EVERYDAY", "WEEKDAYS", "WEEKENDS", "CUSTOM"
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

