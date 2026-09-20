package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.StudyTaskEntity
import com.example.ui.MainViewModel
import com.example.ui.ScreenDestination
import com.example.ui.components.*

@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    val todayTasks by viewModel.todayTasks.collectAsStateWithLifecycle()
    val dueRevisions by viewModel.dueRevisions.collectAsStateWithLifecycle()
    val unresolvedMistakes by viewModel.unresolvedMistakes.collectAsStateWithLifecycle()
    val nextAction by viewModel.nextAction.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val dailyStudyLogs by viewModel.dailyStudyLogs.collectAsStateWithLifecycle()
    val mockAttempts by viewModel.mockAttempts.collectAsStateWithLifecycle()

    val userName = userProfile?.name?.ifBlank { "Aspirant" } ?: "Aspirant"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
    ) {
        // --- 1. GREETING & STATUS HEADER ---
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Hello, $userName 👋",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Ready to master GATE CSE today?",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // --- 2. COUNTDOWN BANNER ---
        item {
            CountdownBanner(targetDateStr = userProfile?.targetExamDate ?: "2027-02-06")
        }

        // --- 2.5 DAILY FULL ATTEMPT QUIZ HERO CARD ---
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.5.dp,
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(Color(0xFF6366F1), Color(0xFFEC4899), Color(0xFFF59E0B))
                        ),
                        RoundedCornerShape(18.dp)
                    )
                    .testTag("daily_full_attempt_quiz_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF6366F1).copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "DAILY FULL ATTEMPT QUIZ",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF6366F1),
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }

                        val todayAttempted = mockAttempts.any {
                            val attemptDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(it.timestamp))
                            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                            attemptDate == today
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (todayAttempted) Color(0xFF10B981).copy(alpha = 0.15f) else MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = if (todayAttempted) "✅ Completed Today" else "⚡ Today's Challenge",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (todayAttempted) Color(0xFF10B981) else MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "GATE 2027 CSE Practice & Full Exam Simulation",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Official 100-Mark 65-Question GATE Paper (180 Mins) or daily sectional drills (OS, DBMS, CN, TOC, DSA, Math, GA) with instant marks, negative marking analysis & detailed step-by-step solutions.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Exam Pattern & Duration Chips
                    var selectedQuizLength by remember { mutableIntStateOf(65) } // 65 (100M/180m), 30 (50M/90m), 15 (25M/25m), 10 (15M/15m)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = selectedQuizLength == 65,
                            onClick = { selectedQuizLength = 65 },
                            label = { Text("🏆 65 Qs (100M • 180m)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF4F46E5),
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = selectedQuizLength == 30,
                            onClick = { selectedQuizLength = 30 },
                            label = { Text("🔥 30 Qs (90m)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = selectedQuizLength == 15,
                            onClick = { selectedQuizLength = 15 },
                            label = { Text("🎯 15 Qs (25m)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                        )
                        FilterChip(
                            selected = selectedQuizLength == 10,
                            onClick = { selectedQuizLength = 10 },
                            label = { Text("⚡ 10 Qs (15m)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (selectedQuizLength == 65) {
                                viewModel.startOfficial100MarkFullGatePaper()
                            } else {
                                val mins = when (selectedQuizLength) {
                                    10 -> 15
                                    15 -> 25
                                    30 -> 90
                                    else -> 180
                                }
                                viewModel.startDailyFullAttemptQuiz(
                                    questionCount = selectedQuizLength,
                                    durationMinutes = mins
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("start_daily_full_attempt_quiz_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedQuizLength == 65) Color(0xFF4338CA) else Color(0xFF4F46E5)
                        )
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (selectedQuizLength == 65) {
                                "Start Official GATE Exam (65 Qs • 100 Marks • 180 Mins)"
                            } else {
                                "Start Practice Drill ($selectedQuizLength Questions)"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Management Row: Download Today's Qs, Edit Qs & Answers, Notification
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.downloadAndSyncDailyQuestions { count ->
                                    android.widget.Toast.makeText(context, "✅ Downloaded $count fresh questions for today!", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF6366F1))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("📥 Sync Qs", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.navigateTo(ScreenDestination.QuestionStudio) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF10B981))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("✏️ Edit Bank", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.triggerDailyQuizTestNotification()
                                android.widget.Toast.makeText(context, "🔔 Push notification sent!", android.widget.Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFF59E0B))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("🔔 Alert", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- 3. WHAT SHOULD I STUDY NOW (AI HERO CARD) ---
        item {
            WhatShouldIDoNowCard(
                recommendation = nextAction,
                onActionClick = { rec ->
                    when (rec.actionCommand) {
                        "OPEN_REVISION" -> viewModel.navigateTo(ScreenDestination.RevisionDeck)
                        "OPEN_MISTAKES" -> viewModel.navigateTo(ScreenDestination.MistakeBook)
                        "OPEN_PRACTICE" -> viewModel.navigateTo(ScreenDestination.Practice)
                        "OPEN_PYQ" -> viewModel.navigateTo(ScreenDestination.PYQs)
                        else -> viewModel.navigateTo(ScreenDestination.FocusMode("${rec.subject} — ${rec.title}", 25))
                    }
                }
            )
        }

        // --- 4. QUICK ACTION SHORTCUT TILES (2x2 Grid) ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionTile(
                        title = "Syllabus",
                        subtitle = "10 Subjects",
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        accentColor = Color(0xFF4F46E5),
                        modifier = Modifier.weight(1f),
                        testTag = "home_quick_syllabus",
                        onClick = { viewModel.navigateTo(ScreenDestination.Syllabus) }
                    )
                    QuickActionTile(
                        title = "PYQ Bank",
                        subtitle = "2018-2024",
                        icon = Icons.Default.Assignment,
                        accentColor = Color(0xFF7C3AED),
                        modifier = Modifier.weight(1f),
                        testTag = "home_quick_pyq",
                        onClick = { viewModel.navigateTo(ScreenDestination.PYQs) }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionTile(
                        title = "Mock Tests",
                        subtitle = "${mockAttempts.size} Attempted",
                        icon = Icons.Default.Timer,
                        accentColor = Color(0xFF059669),
                        modifier = Modifier.weight(1f),
                        testTag = "home_quick_mocks",
                        onClick = { viewModel.navigateTo(ScreenDestination.MockTests) }
                    )
                    QuickActionTile(
                        title = "Mistakes",
                        subtitle = "${unresolvedMistakes.size} Unresolved",
                        icon = Icons.Default.ErrorOutline,
                        accentColor = Color(0xFFD97706),
                        modifier = Modifier.weight(1f),
                        testTag = "home_quick_mistakes",
                        onClick = { viewModel.navigateTo(ScreenDestination.MistakeBook) }
                    )
                }
            }
        }

        // --- 5. 25-MIN FOCUS MODE BANNER ---
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .clickable {
                        viewModel.navigateTo(ScreenDestination.FocusMode("Operating Systems — CPU Scheduling", 25))
                    }
                    .testTag("dashboard_focus_mode_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Focus Mode",
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "25-Min Deep Focus Mode",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "LIVE",
                                        color = Color(0xFF059669),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Distraction-free timer, focused questions & instant scoring",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Start Focus",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // --- 6. TODAY'S STUDY MISSION (DAILY PLANNER) ---
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircleOutline,
                                contentDescription = "Mission",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Today's Study Mission",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp
                            )
                        }

                        val completedCount = todayTasks.count { it.isCompleted }
                        Text(
                            text = "$completedCount/${todayTasks.size} Done",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (todayTasks.isEmpty()) {
                        Text(
                            text = "No study tasks scheduled for today. Explore subjects to start!",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        todayTasks.forEach { task ->
                            TaskRowItem(
                                task = task,
                                onToggle = { viewModel.toggleTask(task.id, task.isCompleted) }
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }
        }

        // --- 7. GATE CSE SUBJECTS LIST ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GATE CSE Subjects",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp
                )
                TextButton(
                    onClick = { viewModel.navigateTo(ScreenDestination.Syllabus) },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("View All (10)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        items(subjects.take(4)) { subject ->
            SubjectProgressCard(
                name = subject.name,
                code = subject.code,
                weightage = subject.totalWeightage,
                colorHex = subject.colorHex,
                onClick = { viewModel.navigateTo(ScreenDestination.Syllabus) }
            )
        }
    }
}

@Composable
fun QuickActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    testTag: String = "",
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 13.sp,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun TaskRowItem(
    task: StudyTaskEntity,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .clickable { onToggle() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp,
                fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.SemiBold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${task.durationMinutes} min",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "• ${task.taskType}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun SubjectProgressCard(
    name: String,
    code: String,
    weightage: Double,
    colorHex: String,
    onClick: () -> Unit
) {
    val color = remember(colorHex) {
        try {
            Color(android.graphics.Color.parseColor(colorHex))
        } catch (e: Exception) {
            Color(0xFF4F46E5)
        }
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = code,
                        color = color,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = name,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Weightage: ~${weightage.toInt()}% in GATE",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open Subject",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
