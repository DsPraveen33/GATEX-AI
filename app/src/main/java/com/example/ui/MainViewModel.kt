package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.GatexApplication
import com.example.data.ai.AIOrchestrator
import com.example.data.ai.ActionRecommendation
import com.example.data.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

sealed class ScreenDestination {
    object Login : ScreenDestination()
    object Dashboard : ScreenDestination()
    object Syllabus : ScreenDestination()
    data class TopicDetail(val topicId: String) : ScreenDestination()
    object Practice : ScreenDestination()
    object PYQs : ScreenDestination()
    object MockTests : ScreenDestination()
    data class ActiveMockTest(val mockTestId: Long) : ScreenDestination()
    data class MockResult(val mockAttemptId: Long) : ScreenDestination()
    object AITutor : ScreenDestination()
    object MistakeBook : ScreenDestination()
    object RevisionDeck : ScreenDestination()
    object FormulaBank : ScreenDestination()
    object ConceptLab : ScreenDestination()
    object CodingLab : ScreenDestination()
    object NotesKnowledgeBase : ScreenDestination()
    object Analytics : ScreenDestination()
    object Settings : ScreenDestination()
    object Reminders : ScreenDestination()
    object QuestionStudio : ScreenDestination()
    data class FocusMode(val topicName: String = "Operating Systems — Deadlocks", val durationMinutes: Int = 25) : ScreenDestination()
}

data class AgentAction(
    val type: String, // "START_TEST", "OPEN_PYQ", "OPEN_REVISION", "OPEN_MISTAKES", "OPEN_PROGRESS", "OPEN_FORMULAS", "OPEN_PRACTICE", "OPEN_TOPIC"
    val label: String,
    val targetId: String = "",
    val subjectId: String = ""
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: String, // "USER", "AI_TUTOR", "SYSTEM"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val actions: List<AgentAction> = emptyList(),
    val modelName: String? = null,
    val roleName: String? = null,
    val imageUri: String? = null,
    val base64Image: String? = null,
    val attachmentName: String? = null,
    val attachmentType: String? = null // "IMAGE", "DOCUMENT", "CODE"
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as GatexApplication).repository
    private val orchestrator = (application as GatexApplication).orchestrator
    val authManager = (application as GatexApplication).authManager
    val firestoreSyncManager = (application as GatexApplication).firestoreSyncManager
    private val ttsHelper = com.example.ui.audio.TextToSpeechHelper(application)

    // --- AUDIO & VOICE TTS STATE ---
    val isTtsSpeaking: StateFlow<Boolean> = ttsHelper.isSpeaking
    val ttsSpeed: StateFlow<Float> = ttsHelper.speechRate

    fun speakText(text: String, rate: Float = ttsSpeed.value) {
        ttsHelper.speak(text, rate)
    }

    fun stopSpeech() {
        ttsHelper.stop()
    }

    fun setTtsSpeed(speed: Float) {
        ttsHelper.setSpeed(speed)
    }

    // --- THEME MODE STATE (SYSTEM, LIGHT, DARK) ---
    private val _themeMode = MutableStateFlow<String>("DARK")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
    }

    // --- AUTHENTICATION & CLOUD SYNC STATE ---
    val authState: StateFlow<com.example.data.firebase.AuthState> = authManager.authState
    val currentUserId: String
        get() = authManager.currentUserId

    private val _syncStatus = MutableStateFlow<com.example.data.firebase.SyncStatus>(com.example.data.firebase.SyncStatus.Idle)
    val syncStatus: StateFlow<com.example.data.firebase.SyncStatus> = _syncStatus.asStateFlow()

    init {
        // Observe auth state changes to auto-pull cloud data for authenticated user
        viewModelScope.launch {
            authManager.authState.collect { state ->
                if (state is com.example.data.firebase.AuthState.Authenticated) {
                    syncWithCloud(pullFromCloudFirst = true)
                }
            }
        }

        // Auto-ensure Daily Free Complete Quiz notification is scheduled and active every day
        viewModelScope.launch {
            try {
                val dailyReminder = repository.getReminderById(99999L)
                if (dailyReminder == null) {
                    val defaultDailyQuizReminder = ReminderScheduleEntity(
                        id = 99999L,
                        title = "⚡ Daily Free Complete GATE CSE Quiz is Live!",
                        message = "Today's fresh 65Q/100M paper & solutions are ready. Solve now and boost your AIR rank!",
                        type = ReminderType.DAILY_PRACTICE,
                        targetName = "Daily Full Attempt Quiz",
                        hour = 8,
                        minute = 0,
                        daysOfWeek = "EVERYDAY",
                        isEnabled = true
                    )
                    repository.insertReminder(defaultDailyQuizReminder)
                    com.example.notification.NotificationHelper.scheduleAlarm(getApplication(), defaultDailyQuizReminder)
                } else if (dailyReminder.isEnabled) {
                    com.example.notification.NotificationHelper.scheduleAlarm(getApplication(), dailyReminder)
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    fun signInWithGoogle(webClientId: String = "") {
        viewModelScope.launch {
            _syncStatus.value = com.example.data.firebase.SyncStatus.Syncing
            val result = authManager.signInWithGoogle(webClientId)
            if (result.isSuccess) {
                val user = result.getOrNull()
                if (user?.displayName != null && user.displayName.isNotBlank()) {
                    val current = _userProfileState()
                    repository.updateUserProfile(current.copy(name = user.displayName))
                }
                syncWithCloud(pullFromCloudFirst = true)
                _currentScreen.value = ScreenDestination.Dashboard
            } else {
                _syncStatus.value = com.example.data.firebase.SyncStatus.Error(result.exceptionOrNull()?.message ?: "Sign in failed")
            }
        }
    }

    fun signInWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            _syncStatus.value = com.example.data.firebase.SyncStatus.Syncing
            val result = authManager.signInWithEmail(email, pass)
            if (result.isSuccess) {
                val user = result.getOrNull()
                if (user?.displayName != null && user.displayName.isNotBlank()) {
                    val current = _userProfileState()
                    repository.updateUserProfile(current.copy(name = user.displayName))
                }
                syncWithCloud(pullFromCloudFirst = true)
                _currentScreen.value = ScreenDestination.Dashboard
            } else {
                _syncStatus.value = com.example.data.firebase.SyncStatus.Error(result.exceptionOrNull()?.message ?: "Sign in failed")
            }
        }
    }

    fun signUpWithEmail(name: String, email: String, pass: String) {
        viewModelScope.launch {
            _syncStatus.value = com.example.data.firebase.SyncStatus.Syncing
            val result = authManager.signUpWithEmail(name, email, pass)
            if (result.isSuccess) {
                val current = _userProfileState()
                repository.updateUserProfile(current.copy(name = name.ifBlank { "GATE Aspirant" }))
                syncWithCloud(pullFromCloudFirst = false)
                _currentScreen.value = ScreenDestination.Dashboard
            } else {
                _syncStatus.value = com.example.data.firebase.SyncStatus.Error(result.exceptionOrNull()?.message ?: "Sign up failed")
            }
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch {
            _syncStatus.value = com.example.data.firebase.SyncStatus.Syncing
            val result = authManager.signInAnonymously()
            if (result.isSuccess) {
                _syncStatus.value = com.example.data.firebase.SyncStatus.Synced()
                _currentScreen.value = ScreenDestination.Dashboard
            }
        }
    }

    fun loginDirectly(name: String, email: String) {
        viewModelScope.launch {
            _syncStatus.value = com.example.data.firebase.SyncStatus.Syncing
            val user = authManager.loginDirect(name, email)
            val current = _userProfileState()
            repository.updateUserProfile(current.copy(name = name))
            _syncStatus.value = com.example.data.firebase.SyncStatus.Synced()
            _currentScreen.value = ScreenDestination.Dashboard
        }
    }

    fun signOut() {
        authManager.signOut()
        _syncStatus.value = com.example.data.firebase.SyncStatus.Idle
        _currentScreen.value = ScreenDestination.Login
    }

    fun syncWithCloud(pullFromCloudFirst: Boolean = false) {
        val uid = authManager.currentUserId
        if (uid.isBlank() || uid == "local_default_user") {
            _syncStatus.value = com.example.data.firebase.SyncStatus.Idle
            return
        }

        viewModelScope.launch {
            _syncStatus.value = com.example.data.firebase.SyncStatus.Syncing
            if (pullFromCloudFirst) {
                firestoreSyncManager.pullUserDataFromCloud(uid, repository)
            }
            val uploadResult = firestoreSyncManager.syncAllDataToCloud(uid, repository)
            if (uploadResult.isSuccess) {
                _syncStatus.value = com.example.data.firebase.SyncStatus.Synced()
            } else {
                _syncStatus.value = com.example.data.firebase.SyncStatus.Error(
                    uploadResult.exceptionOrNull()?.message ?: "Sync error"
                )
            }
        }
    }

    // --- NAVIGATION STATE ---
    private val _currentScreen = MutableStateFlow<ScreenDestination>(
        if (authManager.isUserLoggedIn) ScreenDestination.Dashboard else ScreenDestination.Login
    )
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    fun navigateTo(destination: ScreenDestination) {
        _currentScreen.value = destination
    }

    // --- REPOSITORY FLOWS ---
    val subjects: StateFlow<List<SubjectEntity>> = repository.allSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val topics: StateFlow<List<TopicEntity>> = repository.allTopics
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allQuestions: StateFlow<List<QuestionEntity>> = repository.allQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val officialPYQs: StateFlow<List<QuestionEntity>> = repository.officialPYQs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedQuestions: StateFlow<List<QuestionEntity>> = repository.bookmarkedQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mistakes: StateFlow<List<MistakeEntity>> = repository.allMistakes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unresolvedMistakes: StateFlow<List<MistakeEntity>> = repository.unresolvedMistakes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dueRevisions: StateFlow<List<RevisionEntity>> = repository.getDueRevisions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val flashcards: StateFlow<List<FlashcardEntity>> = repository.allFlashcards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val formulas: StateFlow<List<FormulaEntity>> = repository.allFormulas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayTasks: StateFlow<List<StudyTaskEntity>> = repository.getTasksForDate(
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mockTests: StateFlow<List<MockTestEntity>> = repository.allMockTests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mockAttempts: StateFlow<List<MockAttemptEntity>> = repository.allMockAttempts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<DocumentNoteEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyStudyLogs: StateFlow<List<DailyStudyLogEntity>> = repository.allDailyStudyLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val reminders: StateFlow<List<ReminderScheduleEntity>> = repository.allReminders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- "WHAT SHOULD I DO NOW?" RECOMMENDATION & PROGRESS DIAGNOSIS ---
    private val _nextAction = MutableStateFlow<ActionRecommendation?>(null)
    val nextAction: StateFlow<ActionRecommendation?> = _nextAction.asStateFlow()

    private val _aiDiagnosisResult = MutableStateFlow<String?>(null)
    val aiDiagnosisResult: StateFlow<String?> = _aiDiagnosisResult.asStateFlow()

    private val _isGeneratingDiagnosis = MutableStateFlow(false)
    val isGeneratingDiagnosis: StateFlow<Boolean> = _isGeneratingDiagnosis.asStateFlow()

    fun refreshNextAction() {
        viewModelScope.launch {
            _nextAction.value = orchestrator.getNextStudyActionRecommendation()
        }
    }

    fun generateProgressDiagnosis() {
        viewModelScope.launch {
            _isGeneratingDiagnosis.value = true
            try {
                val report = orchestrator.generateDeepProgressDiagnosis(
                    subjects = subjects.value,
                    topics = topics.value,
                    mistakes = mistakes.value,
                    mockAttempts = mockAttempts.value,
                    dailyLogs = dailyStudyLogs.value
                )
                _aiDiagnosisResult.value = report
            } catch (e: Exception) {
                _aiDiagnosisResult.value = "⚠️ Failed to generate diagnostic report: ${e.localizedMessage}"
            } finally {
                _isGeneratingDiagnosis.value = false
            }
        }
    }

    fun clearDiagnosis() {
        _aiDiagnosisResult.value = null
    }

    init {
        refreshNextAction()
    }

    // --- AI TUTOR CHAT & GEMINI MULTI-TURN CONVERSATION ---
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAILoading = MutableStateFlow(false)
    val isAILoading: StateFlow<Boolean> = _isAILoading.asStateFlow()

    private val _tutorRole = MutableStateFlow("AIR 1 Exam Coach") // "AIR 1 Exam Coach", "IIT CSE Professor", "Socratic Tutor", "Code & Algo Lab", "Rapid Revision", "Telugu Mentor"
    val tutorRole: StateFlow<String> = _tutorRole.asStateFlow()

    private val _selectedModel = MutableStateFlow("AUTO") // "AUTO", "gemini-3.1-pro-preview", "gemini-3.5-flash", "gemini-3.1-flash-lite-preview"
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    var tutorLanguage = "English" // "English", "Telugu", "Telugu + English"
    var tutorMode = "Exam Coach"

    fun setTutorRole(role: String) {
        _tutorRole.value = role
        tutorMode = role
    }

    fun setSelectedModel(model: String) {
        _selectedModel.value = model
    }

    fun clearChat() {
        _chatMessages.value = emptyList()
    }

    fun sendTutorMessage(
        userText: String,
        imageUri: String? = null,
        base64Image: String? = null,
        attachmentName: String? = null,
        attachmentType: String? = null
    ) {
        if (userText.isBlank() && imageUri == null && base64Image == null && attachmentName == null) return
        val displayText = if (userText.isBlank() && imageUri != null) "Uploaded image / diagram for analysis." else userText
        val userMsg = ChatMessage(
            sender = "USER",
            text = displayText,
            imageUri = imageUri,
            base64Image = base64Image,
            attachmentName = attachmentName,
            attachmentType = attachmentType
        )
        val updatedHistory = _chatMessages.value + userMsg
        _chatMessages.value = updatedHistory
        _isAILoading.value = true

        viewModelScope.launch {
            val lower = userText.lowercase()
            val actionsList = mutableListOf<AgentAction>()

            if (lower.contains("quiz") || lower.contains("test") || lower.contains("drill")) {
                actionsList.add(AgentAction(type = "START_TEST", label = "⚡ Generate & Start Quiz Drill"))
            }
            if (lower.contains("mistake") || lower.contains("wrong") || lower.contains("error")) {
                actionsList.add(AgentAction(type = "OPEN_MISTAKES", label = "🎯 Open Mistake Book"))
            }
            if (lower.contains("revis") || lower.contains("spaced repetition")) {
                actionsList.add(AgentAction(type = "OPEN_REVISION", label = "🔄 Review Due Revisions"))
            }
            if (lower.contains("pyq") || lower.contains("previous year") || lower.contains("gate 202")) {
                actionsList.add(AgentAction(type = "OPEN_PYQ", label = "🏛️ Browse Official PYQ Bank"))
            }
            if (lower.contains("formula") || lower.contains("equation")) {
                actionsList.add(AgentAction(type = "OPEN_FORMULAS", label = "📐 Open Formula Bank"))
            }
            if (lower.contains("what should i do") || lower.contains("plan") || lower.contains("progress")) {
                val recommendation = orchestrator.getNextStudyActionRecommendation()
                actionsList.add(
                    AgentAction(
                        type = recommendation.actionCommand,
                        label = "🚀 ${recommendation.title}",
                        targetId = recommendation.targetId
                    )
                )
            }

            // Route multi-turn chat with full conversation history and role/model specifications
            val (response, actualModel) = orchestrator.routeMultiTurnChat(
                history = updatedHistory,
                language = tutorLanguage,
                tutorMode = _tutorRole.value,
                preferredModel = _selectedModel.value
            )

            _chatMessages.value = _chatMessages.value + ChatMessage(
                sender = "AI_TUTOR",
                text = response,
                actions = actionsList,
                modelName = actualModel,
                roleName = _tutorRole.value
            )
            _isAILoading.value = false
        }
    }

    fun executeAgentAction(action: AgentAction) {
        when (action.type) {
            "START_TEST" -> {
                generateCustomTest(
                    testType = "QUICK_QUIZ",
                    questionCount = 10,
                    durationMinutes = 15,
                    avoidAttempted = true
                )
            }
            "OPEN_PYQ" -> navigateTo(ScreenDestination.PYQs)
            "OPEN_REVISION" -> navigateTo(ScreenDestination.RevisionDeck)
            "OPEN_MISTAKES" -> navigateTo(ScreenDestination.MistakeBook)
            "OPEN_PROGRESS" -> navigateTo(ScreenDestination.Analytics)
            "OPEN_FORMULAS" -> navigateTo(ScreenDestination.FormulaBank)
            "OPEN_PRACTICE" -> navigateTo(ScreenDestination.Practice)
            "OPEN_TOPIC" -> {
                if (action.targetId.isNotBlank()) {
                    navigateTo(ScreenDestination.TopicDetail(action.targetId))
                } else {
                    navigateTo(ScreenDestination.Syllabus)
                }
            }
            else -> navigateTo(ScreenDestination.Dashboard)
        }
    }

    // --- QUESTION PRACTICE & PYQ SOLVER ---
    private val _selectedQuestionId = MutableStateFlow<Long?>(null)
    val selectedQuestionId: StateFlow<Long?> = _selectedQuestionId.asStateFlow()

    private val _userPracticeAnswer = MutableStateFlow("")
    val userPracticeAnswer: StateFlow<String> = _userPracticeAnswer.asStateFlow()

    private val _isAnswerSubmitted = MutableStateFlow(false)
    val isAnswerSubmitted: StateFlow<Boolean> = _isAnswerSubmitted.asStateFlow()

    private val _practiceConfidence = MutableStateFlow(3) // 1 to 5
    val practiceConfidence: StateFlow<Int> = _practiceConfidence.asStateFlow()

    fun selectQuestion(questionId: Long) {
        _selectedQuestionId.value = questionId
        _userPracticeAnswer.value = ""
        _isAnswerSubmitted.value = false
        _practiceConfidence.value = 3
    }

    fun setPracticeAnswer(answer: String) {
        _userPracticeAnswer.value = answer
    }

    fun setPracticeConfidence(level: Int) {
        _practiceConfidence.value = level
    }

    fun submitPracticeAnswer(
        question: QuestionEntity,
        timeSpent: Int,
        mistakeCategory: MistakeCategory = MistakeCategory.CONCEPTUAL,
        mistakeNote: String = ""
    ) {
        _isAnswerSubmitted.value = true
        val isCorrect = when (question.questionType) {
            QuestionType.NAT -> {
                val correct = question.correctAnswer.trim()
                val user = _userPracticeAnswer.value.trim()
                if (correct.contains(":")) {
                    val parts = correct.split(":")
                    val min = parts[0].toDoubleOrNull() ?: 0.0
                    val max = parts[1].toDoubleOrNull() ?: 0.0
                    val userNum = user.toDoubleOrNull() ?: Double.MIN_VALUE
                    userNum in min..max
                } else {
                    user.equals(correct, ignoreCase = true) || (user.toDoubleOrNull() != null && user.toDouble() == correct.toDoubleOrNull())
                }
            }
            QuestionType.MSQ -> {
                val userSet = _userPracticeAnswer.value.split(",").map { it.trim().uppercase() }.filter { it.isNotBlank() }.toSet()
                val correctSet = question.correctAnswer.split(",").map { it.trim().uppercase() }.filter { it.isNotBlank() }.toSet()
                userSet == correctSet
            }
            QuestionType.MCQ -> {
                _userPracticeAnswer.value.trim().take(1).equals(question.correctAnswer.trim().take(1), ignoreCase = true)
            }
        }

        viewModelScope.launch {
            repository.recordAttempt(
                question = question,
                userResponse = _userPracticeAnswer.value,
                isCorrect = isCorrect,
                timeSpentSeconds = timeSpent,
                confidenceLevel = _practiceConfidence.value,
                mistakeCategoryIfWrong = mistakeCategory,
                userMistakeNote = mistakeNote
            )
            refreshNextAction()
        }
    }

    fun toggleBookmark(questionId: Long, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleBookmark(questionId, !currentStatus)
        }
    }

    // --- MISTAKE BOOK ---
    fun resolveMistake(mistakeId: Long, isResolved: Boolean) {
        viewModelScope.launch {
            repository.resolveMistake(mistakeId, isResolved)
            refreshNextAction()
        }
    }

    fun deleteMistake(mistakeId: Long) {
        viewModelScope.launch {
            repository.deleteMistake(mistakeId)
        }
    }

    // --- REVISION & FLASHCARDS ---
    private val _currentFlashcardIndex = MutableStateFlow(0)
    val currentFlashcardIndex: StateFlow<Int> = _currentFlashcardIndex.asStateFlow()

    private val _isCardFlipped = MutableStateFlow(false)
    val isCardFlipped: StateFlow<Boolean> = _isCardFlipped.asStateFlow()

    fun flipCard() {
        _isCardFlipped.value = !_isCardFlipped.value
    }

    fun nextCard(total: Int) {
        if (total > 0) {
            _currentFlashcardIndex.value = (_currentFlashcardIndex.value + 1) % total
            _isCardFlipped.value = false
        }
    }

    fun rateRevision(revision: RevisionEntity, rating: String) {
        viewModelScope.launch {
            repository.reviewRevision(revision, rating)
            refreshNextAction()
        }
    }

    fun toggleFormulaFavorite(formulaId: Long, current: Boolean) {
        viewModelScope.launch {
            repository.toggleFormulaFavorite(formulaId, !current)
        }
    }

    // --- STUDY TASKS & RESCUE MODE ---
    fun toggleTask(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.setTaskCompleted(taskId, !isCompleted)
            refreshNextAction()
        }
    }

    fun activateRescueMode() {
        viewModelScope.launch {
            repository.activateRescueMode()
            refreshNextAction()
        }
    }

    // --- MOCK TEST & ADVANCED QUIZ GENERATOR ENGINE ---
    data class MockSessionState(
        val mockTest: MockTestEntity,
        val questions: List<QuestionEntity>,
        val currentQuestionIndex: Int = 0,
        val userAnswers: Map<Long, String> = emptyMap(),
        val markedForReview: Set<Long> = emptySet(),
        val timeRemainingSeconds: Int = 180 * 60,
        val isSubmitted: Boolean = false,
        val resultAttemptId: Long? = null
    )

    private val _activeMockSession = MutableStateFlow<MockSessionState?>(null)
    val activeMockSession: StateFlow<MockSessionState?> = _activeMockSession.asStateFlow()
    private var timerJob: Job? = null

    fun startOfficial100MarkFullGatePaper(
        title: String = "GATE 2027 CSE Official 100-Mark Full Mock Exam"
    ) {
        viewModelScope.launch {
            val officialQuestions = com.example.data.local.GateFullPaperData.generateOfficial100MarkGatePaper()
            val totalMarks = officialQuestions.sumOf { it.marks }.toDouble() // Exactly 100.0 Marks
            val questionIdsJson = org.json.JSONArray(officialQuestions.map { it.id }).toString()

            val mockTestEntity = MockTestEntity(
                title = title,
                durationMinutes = 180, // Exactly 3 Hours (180 Minutes)
                totalQuestions = 65, // Exactly 65 Questions
                totalMarks = totalMarks, // 100.0 Marks
                testType = "FULL_GATE",
                questionIdsJson = questionIdsJson
            )

            val createdId = repository.insertMockTest(mockTestEntity)
            val fullEntity = mockTestEntity.copy(id = createdId)

            _activeMockSession.value = MockSessionState(
                mockTest = fullEntity,
                questions = officialQuestions,
                currentQuestionIndex = 0,
                userAnswers = emptyMap(),
                markedForReview = emptySet(),
                timeRemainingSeconds = 180 * 60, // 10,800 seconds (3 hours)
                isSubmitted = false
            )
            startTimer()
            navigateTo(ScreenDestination.ActiveMockTest(createdId))
        }
    }

    fun startDailyFullAttemptQuiz(
        questionCount: Int = 15,
        durationMinutes: Int = 25
    ) {
        val todayStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        viewModelScope.launch {
            if (questionCount >= 65 || durationMinutes >= 180) {
                // Official Full 100-Mark Paper requested
                startOfficial100MarkFullGatePaper("GATE 2027 CSE Daily Official 100-Mark Paper ($todayStr)")
                return@launch
            }

            val allQs = allQuestions.value.toMutableList()
            if (allQs.size < questionCount) {
                // Complement with official paper questions pool
                allQs.addAll(com.example.data.local.GateFullPaperData.generateOfficial100MarkGatePaper())
            }

            val pastAttempts = repository.allAttempts.first()
            val attemptedToday = pastAttempts.filter {
                val attemptDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it.timestamp))
                attemptDate == todayStr
            }.map { it.questionId }.toSet()

            var candidateQs = allQs.filter { !attemptedToday.contains(it.id) }
            if (candidateQs.size < questionCount) {
                candidateQs = allQs
            }

            // High-yield balanced distribution across core subjects
            val bySubject = candidateQs.groupBy { it.subjectId }
            val selected = mutableListOf<QuestionEntity>()
            val subjectsList = bySubject.keys.shuffled()
            var idx = 0
            while (selected.size < questionCount && bySubject.values.any { it.isNotEmpty() }) {
                if (subjectsList.isNotEmpty()) {
                    val sub = subjectsList[idx % subjectsList.size]
                    val pool = bySubject[sub]?.filter { !selected.contains(it) } ?: emptyList()
                    if (pool.isNotEmpty()) {
                        selected.add(pool.random())
                    }
                } else break
                idx++
                if (idx > questionCount * 4) break
            }
            if (selected.size < questionCount) {
                val remaining = candidateQs.filter { !selected.contains(it) }
                selected.addAll(remaining.shuffled().take(questionCount - selected.size))
            }

            val finalQs = if (selected.isNotEmpty()) selected.shuffled() else allQs.take(questionCount)
            val questionIdsJson = org.json.JSONArray(finalQs.map { it.id }).toString()
            val totalMarks = finalQs.sumOf { it.marks }.toDouble()

            val mockTestEntity = MockTestEntity(
                title = "GATE 2027 Daily Attempt Quiz — $todayStr",
                durationMinutes = durationMinutes,
                totalQuestions = finalQs.size,
                totalMarks = totalMarks,
                testType = "DAILY_QUIZ",
                questionIdsJson = questionIdsJson
            )

            val createdId = repository.insertMockTest(mockTestEntity)
            val fullEntity = mockTestEntity.copy(id = createdId)

            _activeMockSession.value = MockSessionState(
                mockTest = fullEntity,
                questions = finalQs,
                currentQuestionIndex = 0,
                userAnswers = emptyMap(),
                markedForReview = emptySet(),
                timeRemainingSeconds = durationMinutes * 60,
                isSubmitted = false
            )
            startTimer()
            navigateTo(ScreenDestination.ActiveMockTest(createdId))
        }
    }

    fun generateCustomTest(
        testType: String, // "QUICK_QUIZ", "SUBJECT_TEST", "TOPIC_TEST", "MIXED_PRACTICE", "PYQ_TEST", "MISTAKE_TEST", "REVISION_TEST", "FULL_MOCK"
        subjectId: String = "",
        topicId: String = "",
        questionCount: Int = 10,
        durationMinutes: Int = 15,
        avoidAttempted: Boolean = true,
        yearMin: Int = 2015,
        yearMax: Int = 2026,
        distributionMode: String = "SUBJECT_WEIGHTED" // "SUBJECT_WEIGHTED", "EQUAL", "RANDOM"
    ) {
        viewModelScope.launch {
            val allQs = allQuestions.value
            val pastAttempts = repository.allAttempts.first()
            val attemptedIds = if (avoidAttempted) pastAttempts.map { it.questionId }.toSet() else emptySet()

            var candidateQs = allQs.filter { !attemptedIds.contains(it.id) }
            if (candidateQs.isEmpty() && avoidAttempted) {
                // If all attempted, fallback to full pool
                candidateQs = allQs
            }

            // Filter according to test type
            val filteredQs = when (testType) {
                "SUBJECT_TEST" -> candidateQs.filter { it.subjectId.equals(subjectId, ignoreCase = true) }
                "TOPIC_TEST" -> candidateQs.filter { it.topicId.equals(topicId, ignoreCase = true) }
                "PYQ_TEST" -> candidateQs.filter { it.source == QuestionSource.OFFICIAL_PYQ && (it.year in yearMin..yearMax) }
                "MISTAKE_TEST" -> {
                    val mistakeQIds = repository.allMistakes.first().filter { !it.isResolved }.map { it.questionId }.toSet()
                    val mQs = allQs.filter { mistakeQIds.contains(it.id) }
                    if (mQs.isNotEmpty()) mQs else candidateQs
                }
                "REVISION_TEST" -> {
                    val dueTopicIds = repository.getDueRevisions().first().map { it.topicId }.toSet()
                    val rQs = candidateQs.filter { dueTopicIds.contains(it.topicId) }
                    if (rQs.isNotEmpty()) rQs else candidateQs
                }
                else -> candidateQs
            }

            // Apply section distribution
            val finalQuestions = if (distributionMode == "SUBJECT_WEIGHTED" && testType in listOf("MIXED_PRACTICE", "FULL_MOCK", "QUICK_QUIZ")) {
                val bySubject = filteredQs.groupBy { it.subjectId }
                val selected = mutableListOf<QuestionEntity>()
                val subjectsList = bySubject.keys.toList()
                var currentIdx = 0
                while (selected.size < questionCount && bySubject.values.any { it.isNotEmpty() }) {
                    if (subjectsList.isNotEmpty()) {
                        val sub = subjectsList[currentIdx % subjectsList.size]
                        val pool = bySubject[sub]?.filter { !selected.contains(it) } ?: emptyList()
                        if (pool.isNotEmpty()) {
                            selected.add(pool.random())
                        }
                    } else break
                    currentIdx++
                    if (currentIdx > questionCount * 3) break
                }
                // Fill if remaining
                val remaining = filteredQs.filter { !selected.contains(it) }
                selected.addAll(remaining.shuffled().take(questionCount - selected.size))
                selected.shuffled()
            } else {
                filteredQs.shuffled().take(questionCount)
            }

            if (finalQuestions.isEmpty()) {
                // Fallback to sample questions to prevent crash
                startMockTest(
                    MockTestEntity(
                        title = "${testType.replace("_", " ")} Drill",
                        durationMinutes = durationMinutes,
                        totalQuestions = questionCount,
                        totalMarks = questionCount * 1.5,
                        testType = testType,
                        questionIdsJson = "[]"
                    ),
                    allQs.take(questionCount)
                )
                return@launch
            }

            val questionIdsJson = org.json.JSONArray(finalQuestions.map { it.id }).toString()
            val totalMarks = finalQuestions.sumOf { it.marks }.toDouble()

            val mockTestEntity = MockTestEntity(
                title = when (testType) {
                    "DAILY_QUIZ" -> "GATE 2027 Daily Full Attempt Quiz (${SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date())})"
                    "SUBJECT_TEST" -> "${subjectId.uppercase()} Subject Drill"
                    "TOPIC_TEST" -> "Topic Mastery Drill"
                    "PYQ_TEST" -> "Official GATE PYQs ($yearMin-$yearMax)"
                    "MISTAKE_TEST" -> "Mistake Buster Test"
                    "REVISION_TEST" -> "Spaced Repetition Drill"
                    "QUICK_QUIZ" -> "Quick $questionCount-Question Quiz"
                    else -> "Adaptive Multi-Section GATE Exam"
                },
                durationMinutes = durationMinutes,
                totalQuestions = finalQuestions.size,
                totalMarks = totalMarks,
                testType = testType,
                questionIdsJson = questionIdsJson
            )

            val createdId = repository.insertMockTest(mockTestEntity)
            val fullEntity = mockTestEntity.copy(id = createdId)

            _activeMockSession.value = MockSessionState(
                mockTest = fullEntity,
                questions = finalQuestions,
                currentQuestionIndex = 0,
                userAnswers = emptyMap(),
                markedForReview = emptySet(),
                timeRemainingSeconds = durationMinutes * 60,
                isSubmitted = false
            )
            startTimer()
            navigateTo(ScreenDestination.ActiveMockTest(createdId))
        }
    }

    fun startMockTest(mockTest: MockTestEntity, questionsList: List<QuestionEntity>) {
        val selectedQuestions = if (mockTest.testType == "FULL_GATE" || mockTest.totalQuestions >= 65) {
            com.example.data.local.GateFullPaperData.generateOfficial100MarkGatePaper()
        } else {
            try {
                val arr = org.json.JSONArray(mockTest.questionIdsJson)
                val ids = (0 until arr.length()).map { arr.getLong(it) }.toSet()
                val filtered = questionsList.filter { it.id in ids }
                if (filtered.isNotEmpty()) filtered else questionsList
            } catch (e: Exception) {
                questionsList
            }
        }

        _activeMockSession.value = MockSessionState(
            mockTest = mockTest,
            questions = selectedQuestions,
            currentQuestionIndex = 0,
            userAnswers = emptyMap(),
            markedForReview = emptySet(),
            timeRemainingSeconds = mockTest.durationMinutes * 60,
            isSubmitted = false
        )
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val current = _activeMockSession.value ?: break
                if (current.isSubmitted) break
                if (current.timeRemainingSeconds <= 1) {
                    submitMockTest()
                    break
                }
                _activeMockSession.value = current.copy(
                    timeRemainingSeconds = current.timeRemainingSeconds - 1
                )
            }
        }
    }

    fun selectMockQuestion(index: Int) {
        _activeMockSession.value = _activeMockSession.value?.copy(currentQuestionIndex = index)
    }

    fun setMockAnswer(questionId: Long, answer: String) {
        val current = _activeMockSession.value ?: return
        val updated = current.userAnswers.toMutableMap()
        updated[questionId] = answer
        _activeMockSession.value = current.copy(userAnswers = updated)
    }

    fun toggleMockMarkForReview(questionId: Long) {
        val current = _activeMockSession.value ?: return
        val updated = current.markedForReview.toMutableSet()
        if (updated.contains(questionId)) updated.remove(questionId) else updated.add(questionId)
        _activeMockSession.value = current.copy(markedForReview = updated)
    }

    fun clearMockResponse(questionId: Long) {
        val current = _activeMockSession.value ?: return
        val updated = current.userAnswers.toMutableMap()
        updated.remove(questionId)
        _activeMockSession.value = current.copy(userAnswers = updated)
    }

    fun submitMockTest() {
        timerJob?.cancel()
        val current = _activeMockSession.value ?: return
        if (current.isSubmitted) return

        var score = 0.0
        var correctCount = 0
        var incorrectCount = 0
        var attemptedCount = 0

        for (q in current.questions) {
            val userAns = current.userAnswers[q.id]?.trim()
            if (!userAns.isNullOrBlank()) {
                attemptedCount++
                val isCorrect = when (q.questionType) {
                    QuestionType.NAT -> {
                        val correct = q.correctAnswer.trim()
                        if (correct.contains(":")) {
                            val parts = correct.split(":")
                            val min = parts[0].toDoubleOrNull() ?: 0.0
                            val max = parts[1].toDoubleOrNull() ?: 0.0
                            val userNum = userAns.toDoubleOrNull() ?: Double.MIN_VALUE
                            userNum in min..max
                        } else {
                            userAns.equals(correct, ignoreCase = true) || (userAns.toDoubleOrNull() != null && userAns.toDouble() == correct.toDoubleOrNull())
                        }
                    }
                    QuestionType.MSQ -> {
                        val userSet = userAns.split(",").map { it.trim().uppercase() }.toSet()
                        val correctSet = q.correctAnswer.split(",").map { it.trim().uppercase() }.toSet()
                        userSet == correctSet
                    }
                    QuestionType.MCQ -> {
                        userAns.take(1).equals(q.correctAnswer.trim().take(1), ignoreCase = true)
                    }
                }

                if (isCorrect) {
                    correctCount++
                    score += q.marks
                } else {
                    incorrectCount++
                    // Negative marking applies only to MCQs: 1 mark -> -0.33, 2 marks -> -0.66
                    if (q.questionType == QuestionType.MCQ) {
                        score -= if (q.marks == 1) 0.33 else 0.66
                    }
                }
            }
        }

        val totalTime = (current.mockTest.durationMinutes * 60) - current.timeRemainingSeconds

        viewModelScope.launch {
            val attemptId = repository.recordMockAttempt(
                MockAttemptEntity(
                    mockTestId = current.mockTest.id,
                    score = score.coerceAtLeast(0.0),
                    totalMarks = current.mockTest.totalMarks,
                    attemptedCount = attemptedCount,
                    correctCount = correctCount,
                    incorrectCount = incorrectCount,
                    totalTimeSeconds = totalTime
                )
            )

            // Record each individual question attempt into database & Mistake Book
            for (q in current.questions) {
                val userAns = current.userAnswers[q.id]?.trim()
                if (!userAns.isNullOrBlank()) {
                    val isCorrect = when (q.questionType) {
                        QuestionType.NAT -> {
                            val correct = q.correctAnswer.trim()
                            if (correct.contains(":")) {
                                val parts = correct.split(":")
                                val min = parts[0].toDoubleOrNull() ?: 0.0
                                val max = parts[1].toDoubleOrNull() ?: 0.0
                                val userNum = userAns.toDoubleOrNull() ?: Double.MIN_VALUE
                                userNum in min..max
                            } else {
                                userAns.equals(correct, ignoreCase = true) || (userAns.toDoubleOrNull() != null && userAns.toDouble() == correct.toDoubleOrNull())
                            }
                        }
                        QuestionType.MSQ -> {
                            val userSet = userAns.split(",").map { it.trim().uppercase() }.toSet()
                            val correctSet = q.correctAnswer.split(",").map { it.trim().uppercase() }.toSet()
                            userSet == correctSet
                        }
                        QuestionType.MCQ -> {
                            userAns.take(1).equals(q.correctAnswer.trim().take(1), ignoreCase = true)
                        }
                    }

                    val timePerQ = if (attemptedCount > 0) totalTime / attemptedCount else 60
                    repository.recordAttempt(
                        question = q,
                        userResponse = userAns,
                        isCorrect = isCorrect,
                        timeSpentSeconds = timePerQ,
                        confidenceLevel = 3,
                        mistakeCategoryIfWrong = if (!isCorrect) MistakeCategory.CONCEPTUAL else MistakeCategory.CARELESS,
                        userMistakeNote = if (!isCorrect) "Mistake during ${current.mockTest.title}" else ""
                    )
                }
            }

            _activeMockSession.value = current.copy(
                isSubmitted = true,
                resultAttemptId = attemptId
            )
            refreshNextAction()
            navigateTo(ScreenDestination.MockResult(attemptId))
        }
    }

    // --- GATE VIRTUAL CALCULATOR DIALOG ---
    private val _isCalculatorOpen = MutableStateFlow(false)
    val isCalculatorOpen: StateFlow<Boolean> = _isCalculatorOpen.asStateFlow()

    private val _calcDisplay = MutableStateFlow("0")
    val calcDisplay: StateFlow<String> = _calcDisplay.asStateFlow()

    fun openCalculator() { _isCalculatorOpen.value = true }
    fun closeCalculator() { _isCalculatorOpen.value = false }

    fun onCalcKeyPress(key: String) {
        val cur = _calcDisplay.value
        when (key) {
            "C" -> _calcDisplay.value = "0"
            "DEL" -> _calcDisplay.value = if (cur.length > 1) cur.dropLast(1) else "0"
            "=" -> {
                try {
                    // Simple arithmetic evaluator
                    val res = evaluateSimpleExpression(cur)
                    _calcDisplay.value = res
                } catch (e: Exception) {
                    _calcDisplay.value = "Error"
                }
            }
            "sqrt" -> {
                val num = cur.toDoubleOrNull() ?: 0.0
                _calcDisplay.value = String.format(Locale.US, "%.4f", kotlin.math.sqrt(num))
            }
            "log2" -> {
                val num = cur.toDoubleOrNull() ?: 1.0
                _calcDisplay.value = String.format(Locale.US, "%.4f", kotlin.math.ln(num) / kotlin.math.ln(2.0))
            }
            "ln" -> {
                val num = cur.toDoubleOrNull() ?: 1.0
                _calcDisplay.value = String.format(Locale.US, "%.4f", kotlin.math.ln(num))
            }
            "1/x" -> {
                val num = cur.toDoubleOrNull() ?: 1.0
                _calcDisplay.value = String.format(Locale.US, "%.4f", 1.0 / num)
            }
            else -> {
                _calcDisplay.value = if (cur == "0" && key != ".") key else cur + key
            }
        }
    }

    private fun evaluateSimpleExpression(expr: String): String {
        return try {
            val clean = expr.replace("x", "*").replace("÷", "/")
            val tokens = clean.split(Regex("(?<=[-+*/])|(?=[-+*/])")).map { it.trim() }
            if (tokens.isEmpty()) return "0"
            var result = tokens[0].toDoubleOrNull() ?: 0.0
            var i = 1
            while (i < tokens.size - 1) {
                val op = tokens[i]
                val nextVal = tokens[i + 1].toDoubleOrNull() ?: 0.0
                when (op) {
                    "+" -> result += nextVal
                    "-" -> result -= nextVal
                    "*" -> result *= nextVal
                    "/" -> result = if (nextVal != 0.0) result / nextVal else 0.0
                }
                i += 2
            }
            if (result % 1.0 == 0.0) result.toInt().toString() else String.format(Locale.US, "%.4f", result)
        } catch (e: Exception) {
            "Error"
        }
    }

    // --- NOTES KNOWLEDGE BASE ---
    fun addNote(title: String, subjectId: String, topicId: String, content: String) {
        viewModelScope.launch {
            repository.insertNote(
                DocumentNoteEntity(
                    title = title,
                    subjectId = subjectId,
                    topicId = topicId,
                    content = content
                )
            )
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            repository.deleteNote(id)
        }
    }

    // --- DAILY STUDY TRACKER & PROGRESS ---
    fun logStudyHours(
        hours: Double,
        questionsSolved: Int = 0,
        subject: String = "",
        notes: String = "",
        date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    ) {
        viewModelScope.launch {
            repository.logDailyStudyHours(
                date = date,
                additionalHours = hours,
                additionalQuestions = questionsSolved,
                subject = subject,
                notes = notes
            )
            // also record a study session
            repository.recordStudySession(
                StudySessionEntity(
                    subjectId = subject.ifBlank { "gate_cse" },
                    topicId = subject.ifBlank { "study_session" },
                    activity = "STUDY",
                    focusedMinutes = (hours * 60).toInt(),
                    questionsSolved = questionsSolved
                )
            )
        }
    }

    fun setDailyTarget(targetHours: Double, date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) {
        viewModelScope.launch {
            repository.setDailyTargetHours(date, targetHours)
        }
    }

    // --- USER PROFILE & SETTINGS ---
    fun updateProfile(hoursGoal: Int, language: String, tutorMode: String) {
        val current = _userProfileState()
        this.tutorLanguage = language
        this.tutorMode = tutorMode
        viewModelScope.launch {
            repository.updateUserProfile(
                current.copy(
                    dailyStudyHoursGoal = hoursGoal,
                    preferredLanguage = language,
                    tutorMode = tutorMode
                )
            )
        }
    }

    fun resetAllUserProgress() {
        viewModelScope.launch {
            repository.resetAllUserData()
            refreshNextAction()
        }
    }

    // --- PUSH NOTIFICATIONS & REMINDER SCHEDULING ---
    fun saveReminder(reminder: ReminderScheduleEntity) {
        viewModelScope.launch {
            val insertedId = repository.insertReminder(reminder)
            val updatedReminder = if (reminder.id == 0L) reminder.copy(id = insertedId) else reminder
            com.example.notification.NotificationHelper.scheduleAlarm(getApplication(), updatedReminder)
        }
    }

    fun toggleReminder(reminder: ReminderScheduleEntity, isEnabled: Boolean) {
        viewModelScope.launch {
            val updated = reminder.copy(isEnabled = isEnabled)
            repository.setReminderEnabled(reminder.id, isEnabled)
            if (isEnabled) {
                com.example.notification.NotificationHelper.scheduleAlarm(getApplication(), updated)
            } else {
                com.example.notification.NotificationHelper.cancelAlarm(getApplication(), reminder.id)
            }
        }
    }

    fun deleteReminder(reminder: ReminderScheduleEntity) {
        viewModelScope.launch {
            repository.deleteReminder(reminder.id)
            com.example.notification.NotificationHelper.cancelAlarm(getApplication(), reminder.id)
        }
    }

    fun scheduleDailyPracticeReminder(
        hour: Int,
        minute: Int,
        daysOfWeek: String = "EVERYDAY",
        subjectName: String = "GATE CSE Practice Drill"
    ) {
        val reminder = ReminderScheduleEntity(
            title = "Daily Practice: $subjectName",
            message = "Stay consistent! Complete today's syllabus targets & practice questions.",
            type = ReminderType.DAILY_PRACTICE,
            targetName = subjectName,
            hour = hour,
            minute = minute,
            daysOfWeek = daysOfWeek,
            isEnabled = true
        )
        saveReminder(reminder)
    }

    fun scheduleMockTestReminder(
        mockTest: MockTestEntity,
        scheduledDate: String,
        hour: Int,
        minute: Int
    ) {
        val reminder = ReminderScheduleEntity(
            title = "Upcoming Mock Test: ${mockTest.title}",
            message = "Scheduled ${mockTest.durationMinutes}-minute exam starts now! Target score: 60+ Marks.",
            type = ReminderType.MOCK_TEST,
            targetId = mockTest.id.toString(),
            targetName = mockTest.title,
            hour = hour,
            minute = minute,
            scheduledDate = scheduledDate,
            daysOfWeek = "CUSTOM",
            isEnabled = true
        )
        saveReminder(reminder)
    }

    fun triggerTestNotification(
        title: String = "GATE 2027 CSE Practice Reminder",
        message: String = "This is a live test notification from GATEX AI. Time to solve 5 rapid questions!",
        type: ReminderType = ReminderType.DAILY_PRACTICE
    ) {
        com.example.notification.NotificationHelper.scheduleInstantTestReminder(
            context = getApplication(),
            title = title,
            message = message,
            type = type,
            delaySeconds = 2
        )
    }

    // --- QUESTION STUDIO & DAILY SYNC ---
    fun saveOrUpdateQuestion(question: QuestionEntity) {
        viewModelScope.launch {
            if (question.id == 0L) {
                repository.insertQuestion(question)
            } else {
                repository.updateQuestion(question)
            }
        }
    }

    fun deleteQuestion(questionId: Long) {
        viewModelScope.launch {
            repository.deleteQuestion(questionId)
        }
    }

    fun downloadAndSyncDailyQuestions(onComplete: ((Int) -> Unit)? = null) {
        viewModelScope.launch {
            val freshPack = com.example.data.local.DailyQuestionSyncData.generateFreshDailyPack()
            repository.insertQuestions(freshPack)
            onComplete?.invoke(freshPack.size)
        }
    }

    fun scheduleDailyFreeQuizNotification(
        hour: Int = 8,
        minute: Int = 0,
        isEnabled: Boolean = true
    ) {
        val reminder = ReminderScheduleEntity(
            id = 99999L,
            title = "⚡ Daily Free Complete GATE CSE Quiz is Live!",
            message = "Today's fresh 65Q/100M paper & solutions are ready. Solve now and boost your AIR rank!",
            type = ReminderType.DAILY_PRACTICE,
            targetName = "Daily Full Attempt Quiz",
            hour = hour,
            minute = minute,
            daysOfWeek = "EVERYDAY",
            isEnabled = isEnabled
        )
        saveReminder(reminder)
    }

    fun triggerDailyQuizTestNotification() {
        triggerTestNotification(
            title = "⚡ Daily Free Complete GATE CSE Quiz is Live!",
            message = "Today's fresh 65Q / 100 Marks exam paper with latest questions & answers is ready. Tap to start!",
            type = ReminderType.DAILY_PRACTICE
        )
    }

    // --- AI WEAK DRILL STATE ---
    private val _aiDrillContent = MutableStateFlow<String?>(null)
    val aiDrillContent: StateFlow<String?> = _aiDrillContent.asStateFlow()

    private val _isGeneratingDrill = MutableStateFlow(false)
    val isGeneratingDrill: StateFlow<Boolean> = _isGeneratingDrill.asStateFlow()

    fun generateWeakAreaDrill(subjectId: String = "GATE CSE", topicName: String = "High Error Topics") {
        viewModelScope.launch {
            _isGeneratingDrill.value = true
            val unresolved = repository.unresolvedMistakes.firstOrNull() ?: emptyList()
            val mistakeSummary = unresolved.take(3).joinToString("; ") { "Category: ${it.category}, Notes: ${it.userNotes}" }
            val drill = orchestrator.generateWeakAreaDrill(
                subjectId = subjectId,
                topicName = topicName,
                mistakeContext = mistakeSummary
            )
            _aiDrillContent.value = drill
            _isGeneratingDrill.value = false
        }
    }

    fun clearAiDrill() {
        _aiDrillContent.value = null
    }

    fun exportMistakeBook(context: android.content.Context) {
        viewModelScope.launch {
            val mistakesList = repository.allMistakes.firstOrNull() ?: emptyList()
            val questions = repository.allQuestions.firstOrNull() ?: emptyList()

            val sb = StringBuilder()
            sb.appendLine("📘 GATEX AI — GATE 2027 CSE MISTAKE BOOK & ERROR LEDGER")
            sb.appendLine("Generated on: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())}")
            sb.appendLine("=".repeat(50))
            sb.appendLine()

            if (mistakesList.isEmpty()) {
                sb.appendLine("No mistakes recorded yet! Perfect record.")
            } else {
                mistakesList.forEachIndexed { index, m ->
                    val q = questions.find { it.id == m.questionId }
                    sb.appendLine("Mistake #${index + 1} [${m.category.name}] — ${if (m.isResolved) "✅ RESOLVED" else "⚠️ UNRESOLVED"}")
                    if (q != null) {
                        sb.appendLine("Subject: ${q.subjectId.uppercase()} | Topic: ${q.topicId} | Retries: ${m.retryCount}")
                        sb.appendLine("Question: ${q.questionText}")
                        sb.appendLine("Correct Answer: ${q.correctAnswer}")
                        sb.appendLine("Explanation: ${q.explanation}")
                        if (q.commonTrap.isNotBlank()) sb.appendLine("⚠️ GATE Trap: ${q.commonTrap}")
                    } else {
                        sb.appendLine("Question ID: ${m.questionId} | Retries: ${m.retryCount}")
                    }
                    if (m.userNotes.isNotBlank()) {
                        sb.appendLine("My Notes: ${m.userNotes}")
                    }
                    sb.appendLine("-".repeat(40))
                }
            }

            val sendIntent = android.content.Intent().apply {
                action = android.content.Intent.ACTION_SEND
                putExtra(android.content.Intent.EXTRA_TEXT, sb.toString())
                putExtra(android.content.Intent.EXTRA_SUBJECT, "GATEX AI - My GATE CSE Mistake Book")
                type = "text/plain"
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(android.content.Intent.createChooser(sendIntent, "Export Mistake Book").apply {
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }

    fun exportFormulaCompendium(context: android.content.Context) {
        viewModelScope.launch {
            val formulas = repository.allFormulas.firstOrNull() ?: emptyList()
            val sb = StringBuilder()
            sb.appendLine("📐 GATEX AI — HIGH-YIELD GATE 2027 CSE FORMULA COMPENDIUM")
            sb.appendLine("Generated on: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())}")
            sb.appendLine("=".repeat(50))
            sb.appendLine()

            formulas.groupBy { it.subjectId }.forEach { (subj, list) ->
                sb.appendLine("📌 SUBJECT: ${subj.uppercase()}")
                list.forEach { f ->
                    sb.appendLine("• ${f.title} (${f.topicId})")
                    sb.appendLine("  Formula: ${f.formulaLatex}")
                    sb.appendLine("  Explanation: ${f.variablesExplanation}")
                    if (f.practicalExample.isNotBlank()) {
                        sb.appendLine("  Example: ${f.practicalExample}")
                    }
                    sb.appendLine()
                }
                sb.appendLine("-".repeat(40))
            }

            val sendIntent = android.content.Intent().apply {
                action = android.content.Intent.ACTION_SEND
                putExtra(android.content.Intent.EXTRA_TEXT, sb.toString())
                putExtra(android.content.Intent.EXTRA_SUBJECT, "GATEX AI - GATE CSE Formula Compendium")
                type = "text/plain"
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(android.content.Intent.createChooser(sendIntent, "Export Formulas").apply {
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsHelper.shutdown()
    }

    private fun _userProfileState(): UserProfileEntity {
        return userProfile.value ?: UserProfileEntity()
    }
}
