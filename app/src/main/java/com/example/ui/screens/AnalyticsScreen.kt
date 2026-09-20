package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.*
import com.example.ui.MainViewModel
import com.example.ui.ScreenDestination
import com.example.ui.components.GatexOrbLogo
import com.example.ui.components.StudyProgressChartCard
import com.example.ui.components.TechnicalMessageRenderer
import com.example.ui.components.VisualProgressDashboardCard
import java.text.SimpleDateFormat
import java.util.*

data class MilestoneBadge(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean,
    val progressPercent: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(viewModel: MainViewModel) {
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    val topics by viewModel.topics.collectAsStateWithLifecycle()
    val mistakes by viewModel.mistakes.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val dailyStudyLogs by viewModel.dailyStudyLogs.collectAsStateWithLifecycle()
    val mockAttempts by viewModel.mockAttempts.collectAsStateWithLifecycle()
    val mockTests by viewModel.mockTests.collectAsStateWithLifecycle()
    val flashcards by viewModel.flashcards.collectAsStateWithLifecycle()
    val revisionsDue by viewModel.dueRevisions.collectAsStateWithLifecycle()
    val aiDiagnosis by viewModel.aiDiagnosisResult.collectAsStateWithLifecycle()
    val isGeneratingDiagnosis by viewModel.isGeneratingDiagnosis.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Overview, 1: Subject Matrix, 2: Mock & PYQ, 3: AI Diagnosis
    var selectedBadgeDetail by remember { mutableStateOf<MilestoneBadge?>(null) }
    var showLogHoursDialog by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var expandedSubjectId by remember { mutableStateOf<String?>(null) }

    val overallMastery = remember(topics) {
        if (topics.isNotEmpty()) topics.map { it.masteryLevel }.average() else 0.0
    }

    val totalHoursStudied = remember(dailyStudyLogs) {
        dailyStudyLogs.sumOf { it.hoursStudied }
    }

    val totalQuestionsSolved = remember(dailyStudyLogs) {
        dailyStudyLogs.sumOf { it.questionsSolved }
    }

    val resolvedMistakesCount = remember(mistakes) {
        mistakes.count { it.isResolved }
    }

    val unresolvedMistakesCount = remember(mistakes) {
        mistakes.count { !it.isResolved }
    }

    val todayDateStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val todayHoursStudied = remember(dailyStudyLogs, todayDateStr) {
        dailyStudyLogs.filter { it.date == todayDateStr }.sumOf { it.hoursStudied }
    }

    val targetGoalHours = (userProfile?.dailyStudyHoursGoal ?: 4).toDouble()

    // Estimated AIR Rank Band
    val rankPredictionTier = remember(overallMastery, totalQuestionsSolved, mockAttempts) {
        val avgMock = if (mockAttempts.isNotEmpty()) mockAttempts.map { it.score }.average() else 0.0
        when {
            overallMastery >= 85.0 || avgMock >= 75.0 -> "AIR < 100 • IIT Bombay / IISc Direct"
            overallMastery >= 70.0 || avgMock >= 60.0 -> "AIR 100 - 500 • Top Old IITs"
            overallMastery >= 55.0 || avgMock >= 45.0 -> "AIR 500 - 1500 • Top NITs / New IITs"
            overallMastery >= 35.0 || avgMock >= 30.0 -> "AIR 1500 - 4000 • Qualified"
            else -> "Foundation Phase • Target: AIR 1"
        }
    }

    val badges = remember(totalQuestionsSolved, totalHoursStudied, mockAttempts, resolvedMistakesCount, flashcards) {
        listOf(
            MilestoneBadge(
                id = "explorer",
                title = "GATE Explorer",
                description = "Initiated your GATE 2027 CSE preparation journey.",
                icon = "🚀",
                isUnlocked = totalHoursStudied > 0 || totalQuestionsSolved > 0,
                progressPercent = if (totalHoursStudied > 0 || totalQuestionsSolved > 0) 1.0f else 0.0f
            ),
            MilestoneBadge(
                id = "pyq_hunter",
                title = "PYQ Hunter",
                description = "Solved 25+ practice and official GATE questions.",
                icon = "🎯",
                isUnlocked = totalQuestionsSolved >= 25,
                progressPercent = (totalQuestionsSolved / 25.0f).coerceIn(0.0f, 1.0f)
            ),
            MilestoneBadge(
                id = "mistake_slayer",
                title = "Mistake Slayer",
                description = "Converted weaknesses into strengths by resolving mistakes in the ledger.",
                icon = "🛡️",
                isUnlocked = resolvedMistakesCount >= 3,
                progressPercent = (resolvedMistakesCount / 3.0f).coerceIn(0.0f, 1.0f)
            ),
            MilestoneBadge(
                id = "mock_gladiator",
                title = "Mock Gladiator",
                description = "Completed at least 1 full-length or subject mock test simulation.",
                icon = "⚔️",
                isUnlocked = mockAttempts.isNotEmpty(),
                progressPercent = if (mockAttempts.isNotEmpty()) 1.0f else 0.0f
            ),
            MilestoneBadge(
                id = "deep_focus",
                title = "Deep Focus 10H",
                description = "Accumulated 10+ hours of concentrated deep study time.",
                icon = "🔥",
                isUnlocked = totalHoursStudied >= 10.0,
                progressPercent = (totalHoursStudied / 10.0f).toFloat().coerceIn(0.0f, 1.0f)
            ),
            MilestoneBadge(
                id = "formula_wizard",
                title = "Formula Wizard",
                description = "Mastered spaced repetition flashcard and formula decks.",
                icon = "📐",
                isUnlocked = flashcards.isNotEmpty(),
                progressPercent = 1.0f
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // --- HEADER BAR ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Performance & Progress",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 22.sp
                    )
                    Text(
                        text = "GATE 2027 CSE • Real-Time Telemetry & Readiness",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilledTonalButton(
                        onClick = { showLogHoursDialog = true },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("log_hours_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Log Hours", fontSize = 12.sp)
                    }
                }
            }
        }

        // --- TOP HERO: GATE 2027 CSE READINESS & AIR RANK PREDICTOR ---
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        RoundedCornerShape(20.dp)
                    )
                    .testTag("readiness_hero_card")
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Gradient Accent Background
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.tertiary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "EXAM READINESS INDEX",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "${overallMastery.toInt()}%",
                                        fontSize = 38.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Mastery",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                }
                            }

                            // Rank Projection Badge
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = "PROJECTED AIR BAND",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = rankPredictionTier,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Progress Bar
                        LinearProgressIndicator(
                            progress = { (overallMastery / 100.0).toFloat().coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Micro Stats Sub-Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Today's Study", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = String.format(java.util.Locale.US, "%.1f / %.0f hrs", todayHoursStudied, targetGoalHours),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (todayHoursStudied >= targetGoalHours) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Syllabus Completed", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                val completedCount = topics.count { it.masteryLevel >= 75.0 }
                                Text(
                                    text = "$completedCount / ${topics.size} Topics",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Qualifying Cutoff", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "28.5 / 100 Marks",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- NAVIGATION TABS ---
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 0.dp,
                containerColor = Color.Transparent,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MaterialTheme.colorScheme.primary,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("📊 Overview", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium, fontSize = 13.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("📚 Subject Matrix", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium, fontSize = 13.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("🎯 Mocks & PYQs", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium, fontSize = 13.sp) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("🤖 AI Strategy", fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Medium, fontSize = 13.sp) }
                )
            }
        }

        // --- TAB CONTENT ---
        when (selectedTab) {
            0 -> {
                // ==========================================
                // TAB 0: OVERVIEW & TELEMETRY
                // ==========================================
                // 1. Key Metrics 6-Grid
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricCard(
                                title = "Total Study Time",
                                value = String.format(java.util.Locale.US, "%.1f hrs", totalHoursStudied),
                                icon = Icons.Default.Timer,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )
                            MetricCard(
                                title = "Questions Solved",
                                value = "$totalQuestionsSolved",
                                icon = Icons.Default.CheckCircleOutline,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.weight(1f)
                            )
                            MetricCard(
                                title = "Mock Avg Score",
                                value = if (mockAttempts.isNotEmpty()) String.format(java.util.Locale.US, "%.1f", mockAttempts.map { it.score }.average()) else "N/A",
                                icon = Icons.Default.Quiz,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricCard(
                                title = "Unresolved Mistakes",
                                value = "$unresolvedMistakesCount",
                                icon = Icons.Default.ErrorOutline,
                                tint = if (unresolvedMistakesCount > 0) MaterialTheme.colorScheme.error else Color(0xFF10B981),
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.navigateTo(ScreenDestination.MistakeBook) }
                            )
                            MetricCard(
                                title = "Due Revisions",
                                value = "${revisionsDue.size}",
                                icon = Icons.Default.Repeat,
                                tint = if (revisionsDue.isNotEmpty()) Color(0xFFF59E0B) else Color(0xFF10B981),
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.navigateTo(ScreenDestination.RevisionDeck) }
                            )
                            MetricCard(
                                title = "Streak Days",
                                value = "${userProfile?.streakDays ?: 1} 🔥",
                                icon = Icons.Default.LocalFireDepartment,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // 2. Visual Progress Dashboard (Charts & Trajectories)
                item {
                    VisualProgressDashboardCard(
                        mockAttempts = mockAttempts,
                        mockTests = mockTests,
                        subjects = subjects,
                        topics = topics,
                        mistakes = mistakes,
                        onSelectSubject = { subj ->
                            viewModel.navigateTo(ScreenDestination.TopicDetail(subj.id))
                        },
                        onTakeMockClick = {
                            viewModel.navigateTo(ScreenDestination.MockTests)
                        }
                    )
                }

                // 3. Study Hours Daily Progress Chart
                item {
                    StudyProgressChartCard(
                        dailyLogs = dailyStudyLogs,
                        targetGoalHours = targetGoalHours,
                        onLogStudyHours = { hrs, qCount, subj, notes ->
                            viewModel.logStudyHours(
                                hours = hrs,
                                questionsSolved = qCount,
                                subject = subj,
                                notes = notes
                            )
                        }
                    )
                }

                // 4. Milestone Badges Vault
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Milestone Badges & Honors",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 15.sp
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "${badges.count { it.isUnlocked }} / ${badges.size} Unlocked",
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(badges) { badge ->
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (badge.isUnlocked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                        ),
                                        modifier = Modifier
                                            .width(115.dp)
                                            .clickable { selectedBadgeDetail = badge }
                                            .border(
                                                1.dp,
                                                if (badge.isUnlocked) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                RoundedCornerShape(12.dp)
                                            )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(10.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = badge.icon,
                                                fontSize = 26.sp
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = badge.title,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                textAlign = TextAlign.Center,
                                                maxLines = 1
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            LinearProgressIndicator(
                                                progress = { badge.progressPercent },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(4.dp)
                                                    .clip(RoundedCornerShape(2.dp)),
                                                color = if (badge.isUnlocked) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                                                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // ==========================================
                // TAB 1: SUBJECT & TOPIC MASTERY MATRIX
                // ==========================================
                // Category Filter Chips
                item {
                    val categories = listOf("All", "Core CS", "Math & Discrete", "Theory & Compilers", "Aptitude")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { cat ->
                            FilterChip(
                                selected = selectedCategoryFilter == cat,
                                onClick = { selectedCategoryFilter = cat },
                                label = { Text(cat, fontSize = 12.sp) }
                            )
                        }
                    }
                }

                // Filtered Subjects Matrix
                val filteredSubjects = subjects.filter { subj ->
                    when (selectedCategoryFilter) {
                        "Core CS" -> subj.id in listOf("os", "dbms", "cn", "coa", "ds", "algo")
                        "Math & Discrete" -> subj.id in listOf("dm", "em", "math")
                        "Theory & Compilers" -> subj.id in listOf("toc", "cd")
                        "Aptitude" -> subj.id in listOf("ga", "aptitude")
                        else -> true
                    }
                }

                items(filteredSubjects) { subject ->
                    val subTopics = topics.filter { it.subjectId == subject.id }
                    val avgSubjMastery = if (subTopics.isNotEmpty()) subTopics.map { it.masteryLevel }.average() else 0.0
                    val isExpanded = expandedSubjectId == subject.id

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expandedSubjectId = if (isExpanded) null else subject.id
                                    },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = subject.code.take(3),
                                                fontWeight = FontWeight.Black,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = subject.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${subTopics.size} Topics • Weightage: ~${subject.totalWeightage.toInt()} Marks",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${avgSubjMastery.toInt()}%",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = when {
                                            avgSubjMastery >= 75.0 -> Color(0xFF10B981)
                                            avgSubjMastery >= 50.0 -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.error
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Expand",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { (avgSubjMastery / 100.0).toFloat().coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = when {
                                    avgSubjMastery >= 75.0 -> Color(0xFF10B981)
                                    avgSubjMastery >= 50.0 -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.error
                                },
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                            // Expanded Topics List
                            AnimatedVisibility(visible = isExpanded) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                    Text(
                                        text = "Topics & Detailed Confidence:",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    subTopics.forEach { topic ->
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(10.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = topic.name,
                                                        fontSize = 12.5.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = "Importance: ${topic.importance} • Mastery: ${topic.masteryLevel.toInt()}%",
                                                        fontSize = 11.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }

                                                IconButton(
                                                    onClick = {
                                                        viewModel.navigateTo(ScreenDestination.TopicDetail(topic.id))
                                                    },
                                                    modifier = Modifier.size(30.dp)
                                                ) {
                                                    Icon(
                                                        Icons.AutoMirrored.Filled.ArrowForward,
                                                        contentDescription = "Open Topic",
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // ==========================================
                // TAB 2: MOCKS & PYQ ACCURACY
                // ==========================================
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Question Type Accuracy & Negative Marks",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // 3 Question types
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AccuracyTypeCard(
                                    typeName = "MCQ",
                                    description = "1/3 Negative Penalty",
                                    accuracy = "76%",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f)
                                )
                                AccuracyTypeCard(
                                    typeName = "MSQ",
                                    description = "No Negative Marks",
                                    accuracy = "62%",
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.weight(1f)
                                )
                                AccuracyTypeCard(
                                    typeName = "NAT",
                                    description = "Exact Numerical",
                                    accuracy = "68%",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Recent Mock Test Attempts
                item {
                    Text(
                        text = "Recent Mock Exam Submissions",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (mockAttempts.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Quiz, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No Mock Attempts Yet", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Take a full-length or subject mock to unlock score projections.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { viewModel.navigateTo(ScreenDestination.MockTests) }
                                ) {
                                    Text("Start Mock Test")
                                }
                            }
                        }
                    }
                } else {
                    items(mockAttempts.reversed()) { attempt ->
                        val matchedMock = mockTests.find { it.id == attempt.mockTestId }
                        val attemptTitle = matchedMock?.title ?: "GATE Full Length Mock #${attempt.mockTestId}"
                        val dateFormatted = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(attempt.timestamp))
                        val accPercent = if (attempt.attemptedCount > 0) (attempt.correctCount.toDouble() / attempt.attemptedCount * 100).toInt() else 0

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = attemptTitle,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Attempted: $dateFormatted • Accuracy: $accPercent%",
                                        fontSize = 11.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "${attempt.score} / ${attempt.totalMarks}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                // ==========================================
                // TAB 3: AI STUDY DIAGNOSTICS
                // ==========================================
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                GatexOrbLogo(size = 28.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "AI Deep Study Diagnostics",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Powered by Gemini 3.5 Flash & 3.1 Pro",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Text(
                                text = "Analyze your live mastery, logged study hours, mistake patterns, and mock test scores to generate a tactical 7-day score boost roadmap.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 17.sp
                            )

                            Button(
                                onClick = { viewModel.generateProgressDiagnosis() },
                                enabled = !isGeneratingDiagnosis,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("generate_ai_diagnosis_btn")
                            ) {
                                if (isGeneratingDiagnosis) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Synthesizing Telemetry...")
                                } else {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(if (aiDiagnosis != null) "Re-run AI Diagnosis" else "Generate AI Diagnostic Report")
                                }
                            }
                        }
                    }
                }

                // AI Diagnostic Output Card
                if (aiDiagnosis != null) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "AI Performance Strategy Report",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    val clipboard = LocalClipboardManager.current
                                    IconButton(
                                        onClick = {
                                            clipboard.setText(androidx.compose.ui.text.AnnotatedString(aiDiagnosis ?: ""))
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                                    }
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                                TechnicalMessageRenderer(
                                    content = aiDiagnosis ?: "",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // --- LOG STUDY HOURS DIALOG ---
    if (showLogHoursDialog) {
        var inputHours by remember { mutableStateOf("2.5") }
        var inputQuestions by remember { mutableStateOf("15") }
        var selectedSubj by remember { mutableStateOf(subjects.firstOrNull()?.name ?: "Operating Systems") }
        var notesText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showLogHoursDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Log Study Session", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = inputHours,
                        onValueChange = { inputHours = it },
                        label = { Text("Hours Studied (e.g. 2.5)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = inputQuestions,
                        onValueChange = { inputQuestions = it },
                        label = { Text("Questions Solved (e.g. 15)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        label = { Text("Notes / Topic studied (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val hrs = inputHours.toDoubleOrNull() ?: 1.0
                        val qCount = inputQuestions.toIntOrNull() ?: 10
                        viewModel.logStudyHours(
                            hours = hrs,
                            questionsSolved = qCount,
                            subject = selectedSubj,
                            notes = notesText
                        )
                        showLogHoursDialog = false
                    }
                ) {
                    Text("Save Log")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogHoursDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Badge detail dialog
    if (selectedBadgeDetail != null) {
        val b = selectedBadgeDetail!!
        AlertDialog(
            onDismissRequest = { selectedBadgeDetail = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(b.icon, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(b.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = b.description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        text = if (b.isUnlocked) "Status: ✅ Unlocked & Earned!" else "Status: 🔒 In Progress (${(b.progressPercent * 100).toInt()}%)",
                        fontWeight = FontWeight.Bold,
                        color = if (b.isUnlocked) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedBadgeDetail = null }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(14.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun AccuracyTypeCard(
    typeName: String,
    description: String,
    accuracy: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, tint.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(typeName, fontWeight = FontWeight.Black, fontSize = 13.sp, color = tint)
            Text(description, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            Spacer(modifier = Modifier.height(4.dp))
            Text(accuracy, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
