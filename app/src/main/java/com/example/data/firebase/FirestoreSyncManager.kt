package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.example.data.model.*
import com.example.data.repository.GatexRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

sealed class SyncStatus {
    object Idle : SyncStatus()
    object Syncing : SyncStatus()
    data class Synced(val lastSyncTimeMillis: Long = System.currentTimeMillis()) : SyncStatus()
    data class Error(val message: String) : SyncStatus()
}

class FirestoreSyncManager(private val context: Context) {

    private val tag = "FirestoreSyncManager"

    private val firestore: FirebaseFirestore? by lazy {
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                FirebaseFirestore.getInstance()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(tag, "Firestore not initialized: ${e.message}")
            null
        }
    }

    /**
     * Uploads all local Room data strictly scoped under users/{userId} in Firestore.
     */
    suspend fun syncAllDataToCloud(userId: String, repository: GatexRepository): Result<Unit> = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext Result.failure(Exception("Firestore is not configured"))
        if (userId.isBlank() || userId == "local_default_user") {
            return@withContext Result.failure(Exception("No authenticated user"))
        }

        try {
            val userDoc = db.collection("users").document(userId)

            // 1. Sync User Profile
            val profile = repository.userProfile.firstOrNull()
            if (profile != null) {
                val profileMap = hashMapOf(
                    "name" to profile.name,
                    "targetExam" to profile.targetExam,
                    "targetExamDate" to profile.targetExamDate,
                    "dailyStudyHoursGoal" to profile.dailyStudyHoursGoal,
                    "preferredLanguage" to profile.preferredLanguage,
                    "tutorMode" to profile.tutorMode,
                    "streakDays" to profile.streakDays,
                    "lastActiveDate" to profile.lastActiveDate,
                    "updatedAt" to System.currentTimeMillis()
                )
                userDoc.set(profileMap, SetOptions.merge()).await()
            }

            // 2. Sync Topic Progress
            val topics = repository.allTopics.firstOrNull() ?: emptyList()
            for (t in topics) {
                if (t.masteryLevel > 0) {
                    userDoc.collection("topic_progress").document(t.id).set(
                        hashMapOf(
                            "topicId" to t.id,
                            "subjectId" to t.subjectId,
                            "masteryLevel" to t.masteryLevel,
                            "updatedAt" to System.currentTimeMillis()
                        ),
                        SetOptions.merge()
                    ).await()
                }
            }

            // 3. Sync Daily Study Logs
            val logs = repository.allDailyStudyLogs.firstOrNull() ?: emptyList()
            for (log in logs) {
                userDoc.collection("study_logs").document(log.date).set(
                    hashMapOf(
                        "date" to log.date,
                        "hoursStudied" to log.hoursStudied,
                        "targetHours" to log.targetHours,
                        "subjectsStudied" to log.subjectsStudied,
                        "questionsSolved" to log.questionsSolved,
                        "focusScore" to log.focusScore,
                        "notes" to log.notes,
                        "updatedAt" to System.currentTimeMillis()
                    ),
                    SetOptions.merge()
                ).await()
            }

            // 4. Sync Mistakes
            val mistakes = repository.allMistakes.firstOrNull() ?: emptyList()
            for (m in mistakes) {
                userDoc.collection("mistakes").document(m.id.toString()).set(
                    hashMapOf(
                        "id" to m.id,
                        "questionId" to m.questionId,
                        "category" to m.category.name,
                        "userNotes" to m.userNotes,
                        "retryCount" to m.retryCount,
                        "isResolved" to m.isResolved,
                        "dateAdded" to m.dateAdded,
                        "lastRetriedAt" to m.lastRetriedAt
                    ),
                    SetOptions.merge()
                ).await()
            }

            // 5. Sync Mock Attempts
            val attempts = repository.allMockAttempts.firstOrNull() ?: emptyList()
            for (att in attempts) {
                userDoc.collection("mock_attempts").document(att.id.toString()).set(
                    hashMapOf(
                        "id" to att.id,
                        "mockTestId" to att.mockTestId,
                        "score" to att.score,
                        "totalMarks" to att.totalMarks,
                        "attemptedCount" to att.attemptedCount,
                        "correctCount" to att.correctCount,
                        "incorrectCount" to att.incorrectCount,
                        "totalTimeSeconds" to att.totalTimeSeconds,
                        "timestamp" to att.timestamp
                    ),
                    SetOptions.merge()
                ).await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Failed to sync data to Firestore", e)
            Result.failure(e)
        }
    }

    /**
     * Pulls data from users/{userId} in Firestore down into local Room database.
     */
    suspend fun pullUserDataFromCloud(userId: String, repository: GatexRepository): Result<Unit> = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext Result.failure(Exception("Firestore is not configured"))
        if (userId.isBlank() || userId == "local_default_user") {
            return@withContext Result.failure(Exception("No authenticated user"))
        }

        try {
            val userDoc = db.collection("users").document(userId)

            // 1. Pull User Profile
            val profileSnapshot = userDoc.get().await()
            if (profileSnapshot.exists()) {
                val name = profileSnapshot.getString("name") ?: "Aspirant"
                val targetExam = profileSnapshot.getString("targetExam") ?: "GATE 2027 CSE"
                val targetExamDate = profileSnapshot.getString("targetExamDate") ?: "2027-02-06"
                val hoursGoal = profileSnapshot.getLong("dailyStudyHoursGoal")?.toInt() ?: 4
                val language = profileSnapshot.getString("preferredLanguage") ?: "English"
                val tutorMode = profileSnapshot.getString("tutorMode") ?: "Exam Coach"
                val streak = profileSnapshot.getLong("streakDays")?.toInt() ?: 0
                val lastActive = profileSnapshot.getString("lastActiveDate") ?: ""

                repository.updateUserProfile(
                    UserProfileEntity(
                        name = name,
                        targetExam = targetExam,
                        targetExamDate = targetExamDate,
                        dailyStudyHoursGoal = hoursGoal,
                        preferredLanguage = language,
                        tutorMode = tutorMode,
                        streakDays = streak,
                        lastActiveDate = lastActive
                    )
                )
            }

            // 2. Pull Topic Progress
            val topicsSnapshot = userDoc.collection("topic_progress").get().await()
            for (doc in topicsSnapshot.documents) {
                val topicId = doc.getString("topicId") ?: doc.id
                val mastery = doc.getDouble("masteryLevel") ?: 0.0
                if (mastery > 0.0) {
                    repository.updateTopicMastery(topicId, mastery)
                }
            }

            // 3. Pull Daily Study Logs
            val logsSnapshot = userDoc.collection("study_logs").get().await()
            for (doc in logsSnapshot.documents) {
                val date = doc.getString("date") ?: doc.id
                val hours = doc.getDouble("hoursStudied") ?: 0.0
                val qSolved = doc.getLong("questionsSolved")?.toInt() ?: 0
                val subject = doc.getString("subjectsStudied") ?: ""
                val notes = doc.getString("notes") ?: ""
                repository.logDailyStudyHours(
                    date = date,
                    additionalHours = hours,
                    additionalQuestions = qSolved,
                    subject = subject,
                    notes = notes
                )
            }

            // 4. Pull Mock Attempts
            val mockSnapshot = userDoc.collection("mock_attempts").get().await()
            for (doc in mockSnapshot.documents) {
                val testId = doc.getLong("mockTestId") ?: 1L
                val score = doc.getDouble("score") ?: 0.0
                val totalMarks = doc.getDouble("totalMarks") ?: 100.0
                val attempted = doc.getLong("attemptedCount")?.toInt() ?: 0
                val correct = doc.getLong("correctCount")?.toInt() ?: 0
                val incorrect = doc.getLong("incorrectCount")?.toInt() ?: 0
                val timeSeconds = doc.getLong("totalTimeSeconds")?.toInt() ?: 0

                val existing = repository.allMockAttempts.firstOrNull()?.find { it.mockTestId == testId }
                if (existing == null) {
                    repository.recordMockAttempt(
                        MockAttemptEntity(
                            mockTestId = testId,
                            score = score,
                            totalMarks = totalMarks,
                            attemptedCount = attempted,
                            correctCount = correct,
                            incorrectCount = incorrect,
                            totalTimeSeconds = timeSeconds
                        )
                    )
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Failed to pull user data from Firestore", e)
            Result.failure(e)
        }
    }

    /**
     * Incremental single-item updates to keep cloud in sync in real time.
     */
    suspend fun uploadStudyLog(userId: String, log: DailyStudyLogEntity) = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext
        if (userId.isBlank() || userId == "local_default_user") return@withContext
        try {
            db.collection("users").document(userId)
                .collection("study_logs").document(log.date)
                .set(
                    hashMapOf(
                        "date" to log.date,
                        "hoursStudied" to log.hoursStudied,
                        "targetHours" to log.targetHours,
                        "subjectsStudied" to log.subjectsStudied,
                        "questionsSolved" to log.questionsSolved,
                        "focusScore" to log.focusScore,
                        "notes" to log.notes,
                        "updatedAt" to System.currentTimeMillis()
                    ),
                    SetOptions.merge()
                ).await()
        } catch (e: Exception) {
            Log.e(tag, "Error syncing study log", e)
        }
    }

    suspend fun uploadMockAttempt(userId: String, attempt: MockAttemptEntity) = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext
        if (userId.isBlank() || userId == "local_default_user") return@withContext
        try {
            db.collection("users").document(userId)
                .collection("mock_attempts").document(attempt.id.toString())
                .set(
                    hashMapOf(
                        "id" to attempt.id,
                        "mockTestId" to attempt.mockTestId,
                        "score" to attempt.score,
                        "totalMarks" to attempt.totalMarks,
                        "attemptedCount" to attempt.attemptedCount,
                        "correctCount" to attempt.correctCount,
                        "incorrectCount" to attempt.incorrectCount,
                        "totalTimeSeconds" to attempt.totalTimeSeconds,
                        "timestamp" to attempt.timestamp
                    ),
                    SetOptions.merge()
                ).await()
        } catch (e: Exception) {
            Log.e(tag, "Error syncing mock attempt", e)
        }
    }
}
