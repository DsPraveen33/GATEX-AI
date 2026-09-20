package com.example.data.ai

import com.example.data.model.*
import com.example.data.repository.GatexRepository
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*

class AIOrchestrator(private val repository: GatexRepository) {

    enum class AgentType {
        PLANNER,
        TUTOR,
        PYQ,
        QUIZ,
        REVISION,
        MISTAKE,
        ANALYTICS,
        MOCK,
        CODING,
        VOICE
    }

    /**
     * Central multi-turn chat routing engine directing conversational history to Gemini.
     */
    suspend fun routeMultiTurnChat(
        history: List<com.example.ui.ChatMessage>,
        language: String = "English",
        tutorMode: String = "Exam Coach",
        preferredModel: String = "AUTO"
    ): Pair<String, String> {
        val startTime = System.currentTimeMillis()
        val lastUserMessage = history.lastOrNull { it.sender == "USER" }?.text ?: "Hello"
        val detectedAgent = detectAgent(lastUserMessage)

        // Select model according to task complexity rules:
        // Complex (STEM/proofs/professor/deep code) -> gemini-3.1-pro-preview
        // Fast (Rapid revision / quick checks) -> gemini-3.1-flash-lite-preview
        // General (Exam coach / concept QA / Telugu) -> gemini-3.5-flash
        val selectedModel = when {
            preferredModel != "AUTO" -> preferredModel
            tutorMode == "Professor Mode" || detectedAgent == AgentType.CODING || lastUserMessage.contains("derive", ignoreCase = true) || lastUserMessage.contains("proof", ignoreCase = true) || lastUserMessage.contains("complexity", ignoreCase = true) -> "gemini-3.1-pro-preview"
            tutorMode == "Quick Revision" || lastUserMessage.contains("fast", ignoreCase = true) || lastUserMessage.contains("summary", ignoreCase = true) || lastUserMessage.contains("formula only", ignoreCase = true) -> "gemini-3.1-flash-lite-preview"
            else -> "gemini-3.5-flash"
        }

        val systemInstruction = buildSystemPrompt(detectedAgent, language, tutorMode)

        // Convert ChatMessage history into GeminiContent items (take last 16 turns to keep context optimal)
        val geminiContents = history.takeLast(16).mapNotNull { msg ->
            when (msg.sender) {
                "USER" -> {
                    val parts = mutableListOf<GeminiPart>()
                    if (!msg.base64Image.isNullOrBlank()) {
                        parts.add(GeminiPart(inlineData = GeminiBlob(mimeType = "image/jpeg", data = msg.base64Image)))
                    }
                    if (msg.text.isNotBlank()) {
                        parts.add(GeminiPart(text = msg.text))
                    } else if (parts.isEmpty()) {
                        parts.add(GeminiPart(text = "Please analyze this uploaded GATE question/document."))
                    }
                    GeminiContent(role = "user", parts = parts)
                }
                "AI_TUTOR" -> GeminiContent(
                    role = "model",
                    parts = listOf(GeminiPart(text = msg.text))
                )
                else -> null
            }
        }.ifEmpty {
            listOf(GeminiContent(role = "user", parts = listOf(GeminiPart(text = lastUserMessage))))
        }

        val (response, actualModel) = GeminiClient.generateMultiTurnChat(
            history = geminiContents,
            systemInstruction = systemInstruction,
            preferredModel = selectedModel
        )

        val duration = System.currentTimeMillis() - startTime
        repository.logAgentRun(
            agentName = "${detectedAgent.name}_$actualModel",
            prompt = lastUserMessage.take(120),
            summary = response.take(200),
            durationMs = duration,
            status = "SUCCESS"
        )

        return Pair(response, actualModel)
    }

    /**
     * Single-shot fallback routing engine.
     */
    suspend fun routeRequest(
        prompt: String,
        explicitAgent: AgentType? = null,
        language: String = "English",
        tutorMode: String = "Exam Coach"
    ): String {
        val startTime = System.currentTimeMillis()
        val detectedAgent = explicitAgent ?: detectAgent(prompt)

        val selectedModel = when {
            tutorMode == "Professor Mode" || detectedAgent == AgentType.CODING -> "gemini-3.1-pro-preview"
            tutorMode == "Quick Revision" -> "gemini-3.1-flash-lite-preview"
            else -> "gemini-3.5-flash"
        }

        val systemInstruction = buildSystemPrompt(detectedAgent, language, tutorMode)
        val (response, actualModel) = GeminiClient.generateMultiTurnChat(
            history = listOf(GeminiContent(role = "user", parts = listOf(GeminiPart(text = prompt)))),
            systemInstruction = systemInstruction,
            preferredModel = selectedModel
        )

        val duration = System.currentTimeMillis() - startTime
        repository.logAgentRun(
            agentName = "${detectedAgent.name}_$actualModel",
            prompt = prompt.take(120),
            summary = response.take(200),
            durationMs = duration,
            status = "SUCCESS"
        )

        return response
    }

    private fun detectAgent(prompt: String): AgentType {
        val lower = prompt.lowercase()
        return when {
            lower.contains("plan") || lower.contains("schedule") || lower.contains("mission") || lower.contains("what should i do") -> AgentType.PLANNER
            lower.contains("pyq") || lower.contains("previous year") || lower.contains("gate 202") -> AgentType.PYQ
            lower.contains("mistake") || lower.contains("wrong") || lower.contains("error") -> AgentType.MISTAKE
            lower.contains("revis") || lower.contains("flashcard") || lower.contains("formula") || lower.contains("spaced repetition") -> AgentType.REVISION
            lower.contains("mock") || lower.contains("test") || lower.contains("score") || lower.contains("rank") -> AgentType.MOCK
            lower.contains("code") || lower.contains("pointer") || lower.contains("c program") || lower.contains("recursion") || lower.contains("complexity") -> AgentType.CODING
            lower.contains("weak") || lower.contains("analytics") || lower.contains("progress") || lower.contains("mastery") -> AgentType.ANALYTICS
            else -> AgentType.TUTOR
        }
    }

    private suspend fun buildSystemPrompt(agent: AgentType, language: String, tutorMode: String): String {
        val langInstruction = when (language) {
            "Telugu" -> "Explain primarily in Telugu with standard English technical terms for GATE CSE concepts."
            "Telugu + English" -> "Explain using a clear blend of Telugu and English (Bilingual Tutor Mode) with Telugu analogies and English mathematical rigor."
            else -> "Explain in crisp, professional, pedagogical English with high academic rigor for GATE CSE."
        }

        val modeInstruction = when (tutorMode) {
            "Professor Mode" -> """
                Role: Distinguished IIT CSE Professor & Research Faculty.
                Pedagogy: Concept-first instruction, rigorous mathematical derivation, formal definitions, time/space complexity, and architecture-level reasoning.
            """.trimIndent()
            "Socratic Tutor" -> """
                Role: Socratic GATE CSE Mentor.
                Pedagogy: Do not immediately give away the final numerical key or answer. Guide the student with leading questions, conceptual checks, and progressive hints.
            """.trimIndent()
            "Telugu AI", "Telugu Mentor" -> """
                Role: Dedicated Telugu GATE CSE Mentor.
                Pedagogy: Explain core ideas naturally in Telugu (e.g. 'Operating Systems lo Paging endukante...'), retain official English CS keywords and formulas, and keep student motivated.
            """.trimIndent()
            "Quick Revision" -> """
                Role: Rapid GATE 2027 Revision Coach.
                Pedagogy: Under 200 words, bullet-point summary, key formulas, and high-frequency exam traps.
            """.trimIndent()
            else -> """
                Role: Top AIR 1 GATE CSE Exam Strategist & Coach.
                Pedagogy: High-yield focus, standard PYQ patterns, shortcut formulas, negative mark trap avoidance (⚠️ GATE Trap), and optimal solving order.
            """.trimIndent()
        }

        val revisionsDue = repository.getDueRevisions().firstOrNull()?.size ?: 0
        val unresolvedMistakes = repository.unresolvedMistakes.firstOrNull()?.size ?: 0

        val studentContext = """
            Student Context:
            - Unresolved Mistakes in Mistake Book: $unresolvedMistakes
            - Spaced Repetition Reviews Due Today: $revisionsDue
            - Target Exam: GATE 2027 CSE / IT
        """.trimIndent()

        return """
            You are GATEX AI Tutor, the dedicated intelligent study agent for GATE 2027 CSE.
            Specialization: ${agent.name}
            Mode: $modeInstruction
            Language Strategy: $langInstruction
            
            $studentContext
            
            Formatting & Visual Structure Mandate (Make it as clean and structured as Gemini / ChatGPT):
            1. Use clean Markdown headings:
               ### 📌 Core Concept & Intuition
               ### 📐 Mathematical Formulation & Proof
               ### 🔍 Step-by-Step Derivation / Solution
               ### 💻 Code / Algorithm (if applicable, using ```c or ```python)
               ### ⚠️ GATE Trap & Common Pitfalls
            2. Formatting:
               - Use bold `**term**` for keywords and key variables.
               - Use bullet points (`- `) with clear sub-points.
               - Use numbered lists (`1. `, `2. `) for sequential calculation steps.
               - Format formulas clearly with LaTeX math notation or clear equations.
            3. For GATE Questions (MCQ/MSQ/NAT): Clearly highlight Option/Key, Explanation, and ⚠️ GATE Trap.
            4. When student asks about study recommendations, reference their real unresolved mistakes ($unresolvedMistakes) and reviews due ($revisionsDue).
            5. Always be direct, clean, and pedagogical.
        """.trimIndent()
    }

    /**
     * Dynamic "What Should I Do Now?" recommendation evaluating mastery, weak topics, and overdue revisions.
     */
    suspend fun getNextStudyActionRecommendation(): ActionRecommendation {
        val revisionsDue = repository.getDueRevisions().firstOrNull() ?: emptyList()
        val unresolvedMistakes = repository.unresolvedMistakes.firstOrNull() ?: emptyList()
        val allTopics = repository.allTopics.firstOrNull() ?: emptyList()

        val weakestTopic = allTopics.minByOrNull { it.masteryLevel }

        return when {
            revisionsDue.isNotEmpty() -> {
                val dueItem = revisionsDue.first()
                ActionRecommendation(
                    actionType = "SPACED_REVISION",
                    title = "Revise: ${dueItem.conceptTitle}",
                    subject = dueItem.topicId.split("_").firstOrNull()?.uppercase() ?: "REVISION",
                    reason = "Spaced repetition review is due now for maximum long-term memory retention.",
                    actionCommand = "OPEN_REVISION",
                    targetId = dueItem.topicId
                )
            }
            unresolvedMistakes.isNotEmpty() -> {
                val mistake = unresolvedMistakes.first()
                ActionRecommendation(
                    actionType = "MISTAKE_RETRY",
                    title = "Rectify Unresolved Mistake in ${mistake.category.name}",
                    subject = "MISTAKE BOOK",
                    reason = "You have ${unresolvedMistakes.size} mistakes requiring retry to eliminate concept flaws.",
                    actionCommand = "OPEN_MISTAKES",
                    targetId = mistake.questionId.toString()
                )
            }
            weakestTopic != null && weakestTopic.masteryLevel < 50.0 -> {
                ActionRecommendation(
                    actionType = "TOPIC_MASTERY",
                    title = "Strengthen ${weakestTopic.name}",
                    subject = weakestTopic.subjectId.uppercase(),
                    reason = "Current mastery is only ${weakestTopic.masteryLevel.toInt()}%. Practice 5 questions to advance.",
                    actionCommand = "OPEN_PRACTICE",
                    targetId = weakestTopic.id
                )
            }
            else -> {
                ActionRecommendation(
                    actionType = "PYQ_SESSION",
                    title = "Solve High-Yield GATE 2024 PYQs",
                    subject = "OS & ALGO",
                    reason = "Keep exam speed sharp by solving recent authentic GATE questions.",
                    actionCommand = "OPEN_PYQ",
                    targetId = "os"
                )
            }
        }
    }

    suspend fun generateWeakAreaDrill(
        subjectId: String,
        topicName: String,
        mistakeContext: String = ""
    ): String {
        val prompt = """
            You are creating an authentic GATE 2027 CSE Weak-Area Remedial Practice Drill.
            Target Subject: $subjectId
            Target Topic: $topicName
            User's Weakness Context: $mistakeContext

            Generate 2 high-yield, authentic exam-standard questions:
            - Question 1: NAT (Numerical Answer Type) requiring step-by-step calculation (e.g. Cache size, Paging memory overhead, Pipeline speedup, or Relational Algebra tuple count).
            - Question 2: MSQ (Multiple Select Question) testing tricky edge cases and common traps.

            Format each question with:
            - Question Text
            - Options / Numeric Range
            - Correct Key
            - Detailed First-Principles Derivation
            - ⚠️ Common GATE Pitfall / Trap to Avoid
        """.trimIndent()

        return routeRequest(prompt, AgentType.TUTOR)
    }

    suspend fun analyzeMistakeReason(
        question: QuestionEntity,
        userResponse: String,
        correctAnswer: String
    ): String {
        val prompt = """
            Analyze this GATE CSE mistake:
            Subject: ${question.subjectId}
            Topic: ${question.topicId}
            Question: ${question.questionText}
            Correct Answer: $correctAnswer
            User Answer: $userResponse
            Explanation: ${question.explanation}
            Trap: ${question.commonTrap}

            Please explain:
            1. Why the user's answer '$userResponse' is incorrect.
            2. The exact conceptual or mathematical root cause.
            3. A 2-step mental check to never repeat this mistake in GATE 2027.
        """.trimIndent()

        return routeRequest(prompt, AgentType.MISTAKE)
    }

    suspend fun generateDeepProgressDiagnosis(
        subjects: List<SubjectEntity>,
        topics: List<TopicEntity>,
        mistakes: List<MistakeEntity>,
        mockAttempts: List<MockAttemptEntity>,
        dailyLogs: List<DailyStudyLogEntity>
    ): String {
        val overallMastery = if (topics.isNotEmpty()) topics.map { it.masteryLevel }.average() else 0.0
        val weakTopics = topics.filter { it.masteryLevel < 50.0 }.take(5).joinToString(", ") { "${it.name} (${it.masteryLevel.toInt()}%)" }
        val strongTopics = topics.filter { it.masteryLevel >= 75.0 }.take(5).joinToString(", ") { "${it.name} (${it.masteryLevel.toInt()}%)" }
        val unresolvedMistakeCount = mistakes.count { !it.isResolved }
        val totalHours = dailyLogs.sumOf { it.hoursStudied }
        val totalQuestions = dailyLogs.sumOf { it.questionsSolved }
        val avgMockScore = if (mockAttempts.isNotEmpty()) mockAttempts.map { it.score }.average() else 0.0

        val prompt = """
            You are the Chief AI Strategist for GATE 2027 CSE (AIR 1 Mentor & Analytics Engine).
            Please generate a comprehensive, highly motivating, data-backed Progress & Preparation Diagnostic Report for this student.

            Student Telemetry:
            - Overall Syllabus Mastery: ${overallMastery.toInt()}%
            - Total Logged Study Hours: ${String.format(java.util.Locale.US, "%.1f", totalHours)} hrs
            - Total Practice Questions Solved: $totalQuestions
            - Mock Tests Attempted: ${mockAttempts.size} (Average Score: ${String.format(java.util.Locale.US, "%.1f", avgMockScore)} / 100)
            - Unresolved Mistake Book Entries: $unresolvedMistakeCount
            - Weak Topics Identified: ${if (weakTopics.isNotBlank()) weakTopics else "None recorded yet"}
            - High Mastery Topics: ${if (strongTopics.isNotBlank()) strongTopics else "Beginning phase"}

            Please produce a structured, high-impact diagnostic report formatted as follows:
            ### 🎯 1. Current Readiness & AIR Rank Band Prediction
            - Project the student's estimated score range and AIR band based on current telemetry.
            - Benchmark against the GATE CSE qualifying cutoff (~28-30 marks) and Top IIT cutoff (~65-75 marks).

            ### 🔍 2. Subject Weak Link & Vulnerability Audit
            - Highlight critical danger zones in high-weightage subjects (OS, DBMS, CN, COA, TOC, Algo, Discrete Math).
            - Identify where negative marks or conceptual gaps are most likely costing points.

            ### 📈 3. Tactical 7-Day Action Plan for 15+ Mark Boost
            - 3 concrete, high-yield action items with specific topic names, revision intervals, and practice recommendations.

            ### 💡 4. AIR 1 Mindset & Exam Philosophy
            - A crisp, inspiring parting word from a veteran GATE mentor.
        """.trimIndent()

        return routeRequest(prompt, AgentType.ANALYTICS)
    }
}

data class ActionRecommendation(
    val actionType: String,
    val title: String,
    val subject: String,
    val reason: String,
    val actionCommand: String,
    val targetId: String
)
