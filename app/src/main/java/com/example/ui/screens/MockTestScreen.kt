package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MockTestEntity
import com.example.data.model.QuestionEntity
import com.example.data.model.QuestionType
import com.example.ui.MainViewModel
import com.example.ui.ScreenDestination
import com.example.ui.theme.*
import org.json.JSONArray
import java.util.Locale

@Composable
fun MockTestsListScreen(viewModel: MainViewModel) {
    val mockTests by viewModel.mockTests.collectAsStateWithLifecycle()
    val allQuestions by viewModel.allQuestions.collectAsStateWithLifecycle()
    val mockAttempts by viewModel.mockAttempts.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeep)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            Column {
                Text(
                    text = "GATE Exam Simulator & Test Center",
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 20.sp
                )
                Text(
                    text = "Adaptive Multi-Section Generator • PYQ Drills • Mistake Buster",
                    color = ElectricCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // --- ADAPTIVE QUIZ GENERATOR ENGINE CARD ---
        item {
            var selectedTestMode by remember { mutableStateOf("QUICK_QUIZ") }
            var selectedSubject by remember { mutableStateOf("os") }
            var questionCount by remember { mutableIntStateOf(10) }
            var durationMins by remember { mutableIntStateOf(15) }
            var avoidAttempted by remember { mutableStateOf(true) }
            var distributionMode by remember { mutableStateOf("SUBJECT_WEIGHTED") }
            var isExpanded by remember { mutableStateOf(true) }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, ElectricCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isExpanded = !isExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Quiz Generator Engine",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Configurable sections & intelligent repeat prevention",
                                    color = TextSecondaryDark,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = ElectricCyan
                        )
                    }

                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = Color(0xFF334155))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Test Mode Selection Chips
                        Text("Select Drill Type:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))

                        val modes = listOf(
                            "FULL_GATE" to "🏆 100M Full Exam",
                            "DAILY_QUIZ" to "🔥 Daily Quiz",
                            "SUBJECT_TEST" to "📚 Subject Test",
                            "QUICK_QUIZ" to "⚡ Quick Quiz",
                            "PYQ_TEST" to "🏛️ PYQ Drill",
                            "MISTAKE_TEST" to "🎯 Mistake Buster",
                            "REVISION_TEST" to "🔄 Spaced Revision"
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            modes.take(3).forEach { (mode, label) ->
                                FilterChip(
                                    selected = selectedTestMode == mode,
                                    onClick = {
                                        selectedTestMode = mode
                                        if (mode == "FULL_GATE") { questionCount = 65; durationMins = 180 }
                                        if (mode == "DAILY_QUIZ") { questionCount = 15; durationMins = 25 }
                                        if (mode == "SUBJECT_TEST") { questionCount = 15; durationMins = 25 }
                                    },
                                    label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ElectricCyan,
                                        selectedLabelColor = NavyDeep
                                    )
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            modes.drop(3).forEach { (mode, label) ->
                                FilterChip(
                                    selected = selectedTestMode == mode,
                                    onClick = {
                                        selectedTestMode = mode
                                        if (mode == "QUICK_QUIZ") { questionCount = 10; durationMins = 15 }
                                        if (mode == "PYQ_TEST") { questionCount = 20; durationMins = 30 }
                                        if (mode == "MISTAKE_TEST") { questionCount = 10; durationMins = 15 }
                                        if (mode == "REVISION_TEST") { questionCount = 10; durationMins = 15 }
                                    },
                                    label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ElectricCyan,
                                        selectedLabelColor = NavyDeep
                                    )
                                )
                            }
                        }

                        if (selectedTestMode == "SUBJECT_TEST") {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Target Subject:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            val subjects = listOf("os" to "OS", "dbms" to "DBMS", "dsa" to "DSA", "cn" to "CN", "toc" to "TOC", "coa" to "COA")
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                subjects.forEach { (id, name) ->
                                    FilterChip(
                                        selected = selectedSubject == id,
                                        onClick = { selectedSubject = id },
                                        label = { Text(name, fontSize = 10.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = BrightAmber,
                                            selectedLabelColor = NavyDeep
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Questions: $questionCount", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Duration: $durationMins Mins", color = BrightAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = questionCount.toFloat(),
                            onValueChange = {
                                questionCount = it.toInt()
                                durationMins = (it * 1.5).toInt().coerceAtLeast(10)
                            },
                            valueRange = 5f..35f,
                            steps = 5,
                            colors = SliderDefaults.colors(thumbColor = ElectricCyan, activeTrackColor = ElectricCyan)
                        )

                        // Avoid previously attempted toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { avoidAttempted = !avoidAttempted }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Intelligent Repeat Prevention", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("Filter out questions you already solved", color = TextSecondaryDark, fontSize = 10.sp)
                            }
                            Switch(
                                checked = avoidAttempted,
                                onCheckedChange = { avoidAttempted = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = ElectricCyan, checkedTrackColor = ElectricBlue)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                viewModel.generateCustomTest(
                                    testType = selectedTestMode,
                                    subjectId = selectedSubject,
                                    questionCount = questionCount,
                                    durationMinutes = durationMins,
                                    avoidAttempted = avoidAttempted,
                                    distributionMode = distributionMode
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = NavyDeep),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("generate_quiz_btn")
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("⚡ Generate & Start Drill", fontWeight = FontWeight.Black, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Curated Full Mock Exams",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 16.sp
            )
        }

        items(mockTests) { mock ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ElectricBlue.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = mock.testType.replace("_", " "),
                                color = ElectricCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Text(
                            text = "${mock.durationMinutes} Mins • ${mock.totalMarks.toInt()} Marks",
                            color = BrightAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = mock.title,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Standard GATE Marking: MCQ +1/-0.33, +2/-0.66 • NAT/MSQ No Negative",
                        color = TextSecondaryDark,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    var showScheduleModal by remember { mutableStateOf(false) }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showScheduleModal = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BrightAmber),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BrightAmber.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("schedule_mock_reminder_btn")
                        ) {
                            Icon(Icons.Default.AddAlert, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Set Alert", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.startMockTest(mock, allQuestions)
                                viewModel.navigateTo(ScreenDestination.ActiveMockTest(mock.id))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = NavyDeep),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.5f).testTag("launch_mock_test_btn")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Start Exam", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    if (showScheduleModal) {
                        var targetHour by remember { mutableIntStateOf(9) }
                        var targetMinute by remember { mutableIntStateOf(0) }
                        var scheduleType by remember { mutableStateOf("WEEKEND") } // "WEEKEND" or "TOMORROW"

                        AlertDialog(
                            onDismissRequest = { showScheduleModal = false },
                            containerColor = NavyCard,
                            title = {
                                Text("Schedule Mock Test Reminder", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            },
                            text = {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        text = "Exam: ${mock.title}",
                                        color = BrightAmber,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Set an exact push notification alarm so you are ready with scratchpad and calculator.",
                                        color = TextSecondaryDark,
                                        fontSize = 11.sp
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        FilterChip(
                                            selected = scheduleType == "WEEKEND",
                                            onClick = { scheduleType = "WEEKEND" },
                                            label = { Text("Weekend (Sat/Sun)") },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = BrightAmber,
                                                selectedLabelColor = NavyDeep
                                            )
                                        )
                                        FilterChip(
                                            selected = scheduleType == "TOMORROW",
                                            onClick = { scheduleType = "TOMORROW" },
                                            label = { Text("Tomorrow Morning") },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = BrightAmber,
                                                selectedLabelColor = NavyDeep
                                            )
                                        )
                                    }

                                    Text("Alarm Time: ${String.format(java.util.Locale.getDefault(), "%02d:%02d", targetHour, targetMinute)}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Slider(
                                        value = targetHour.toFloat(),
                                        onValueChange = { targetHour = it.toInt() },
                                        valueRange = 6f..22f,
                                        steps = 15,
                                        colors = SliderDefaults.colors(thumbColor = BrightAmber, activeTrackColor = BrightAmber)
                                    )
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        val scheduledDate = if (scheduleType == "TOMORROW") {
                                            val cal = java.util.Calendar.getInstance().apply { add(java.util.Calendar.DAY_OF_YEAR, 1) }
                                            java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(cal.time)
                                        } else ""
                                        viewModel.scheduleMockTestReminder(mock, scheduledDate, targetHour, targetMinute)
                                        showScheduleModal = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrightAmber, contentColor = NavyDeep)
                                ) {
                                    Text("Set Alarm", fontWeight = FontWeight.Bold)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showScheduleModal = false }) {
                                    Text("Cancel", color = TextSecondaryDark)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveMockExamScreen(viewModel: MainViewModel) {
    val session by viewModel.activeMockSession.collectAsStateWithLifecycle()
    val activeSession = session ?: return

    val questions = activeSession.questions
    val currentIndex = activeSession.currentQuestionIndex.coerceIn(0, (questions.size - 1).coerceAtLeast(0))
    val currentQuestion = questions.getOrNull(currentIndex)

    var showSubmitDialog by remember { mutableStateOf(false) }

    val hours = activeSession.timeRemainingSeconds / 3600
    val minutes = (activeSession.timeRemainingSeconds % 3600) / 60
    val seconds = activeSession.timeRemainingSeconds % 60
    val timeFormatted = if (hours > 0) {
        String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }

    var selectedSectionFilter by remember { mutableStateOf("ALL") } // "ALL", "GA", "CS"

    val displayedQuestions = remember(questions, selectedSectionFilter) {
        when (selectedSectionFilter) {
            "GA" -> questions.filter { it.subjectId == "ga" || questions.indexOf(it) < 10 }
            "CS" -> questions.filter { it.subjectId != "ga" && questions.indexOf(it) >= 10 }
            else -> questions
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeep)
    ) {
        // --- EXAM TOP BAR WITH TIMER & PALETTE SHORTCUT ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavyDark)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activeSession.mockTest.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1
                )
                Text(
                    text = "Q ${currentIndex + 1} of ${questions.size} • Total Marks: ${activeSession.mockTest.totalMarks.toInt()}M",
                    color = BrightAmber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.openCalculator() }) {
                    Icon(Icons.Default.Calculate, contentDescription = "Calc", tint = ElectricCyan)
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (activeSession.timeRemainingSeconds < 600) CrimsonAlert.copy(alpha = 0.25f) else BrightAmber.copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = null,
                            tint = if (activeSession.timeRemainingSeconds < 600) CrimsonAlert else BrightAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = timeFormatted,
                            color = if (activeSession.timeRemainingSeconds < 600) CrimsonAlert else BrightAmber,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // --- SECTION SWITCHER TABS (FOR 100-MARK GATE PAPERS) ---
        if (questions.size >= 30) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedSectionFilter == "ALL",
                    onClick = { selectedSectionFilter = "ALL" },
                    label = { Text("All (${questions.size} Qs)", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricCyan,
                        selectedLabelColor = NavyDeep
                    )
                )
                FilterChip(
                    selected = selectedSectionFilter == "GA",
                    onClick = {
                        selectedSectionFilter = "GA"
                        val firstGaIndex = questions.indexOfFirst { it.subjectId == "ga" || questions.indexOf(it) < 10 }
                        if (firstGaIndex >= 0) viewModel.selectMockQuestion(firstGaIndex)
                    },
                    label = { Text("Sec 1: General Aptitude (15M)", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BrightAmber,
                        selectedLabelColor = NavyDeep
                    )
                )
                FilterChip(
                    selected = selectedSectionFilter == "CS",
                    onClick = {
                        selectedSectionFilter = "CS"
                        val firstCsIndex = questions.indexOfFirst { it.subjectId != "ga" && questions.indexOf(it) >= 10 }
                        if (firstCsIndex >= 0) viewModel.selectMockQuestion(firstCsIndex)
                    },
                    label = { Text("Sec 2: Core CS (85M)", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonMint,
                        selectedLabelColor = NavyDeep
                    )
                )
            }
        }

        // --- QUESTION PALETTE CHIPS ---
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Quick Question Palette Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Question Palette (${displayedQuestions.size}):", color = TextSecondaryDark, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(NeonMint))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Answered", fontSize = 9.sp, color = TextSecondaryDark)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(RoyalPurple))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Review", fontSize = 9.sp, color = TextSecondaryDark)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(8),
                    modifier = Modifier.height(if (questions.size > 30) 120.dp else 90.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    itemsIndexed(displayedQuestions) { _, q ->
                        val globalIdx = questions.indexOf(q)
                        val isAnswered = !activeSession.userAnswers[q.id].isNullOrBlank()
                        val isMarked = activeSession.markedForReview.contains(q.id)
                        val isCurrent = globalIdx == currentIndex

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    when {
                                        isAnswered && isMarked -> RoyalPurple
                                        isAnswered -> NeonMint
                                        isMarked -> RoyalPurple.copy(alpha = 0.5f)
                                        else -> NavyCard
                                    }
                                )
                                .border(
                                    width = if (isCurrent) 2.dp else 0.5.dp,
                                    color = if (isCurrent) ElectricCyan else Color(0xFF334155),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .clickable { viewModel.selectMockQuestion(globalIdx) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${globalIdx + 1}",
                                color = if (isAnswered && !isMarked) NavyDeep else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            if (currentQuestion != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = NavyCard),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = ElectricCyan.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "${currentQuestion.questionType.name} • ${currentQuestion.marks} Mark${if (currentQuestion.marks > 1) "s" else ""}",
                                        color = ElectricCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                val markingScheme = when (currentQuestion.questionType) {
                                    QuestionType.MCQ -> if (currentQuestion.marks == 1) "-0.33 Marks" else "-0.66 Marks"
                                    QuestionType.MSQ -> "No Negative"
                                    QuestionType.NAT -> "No Negative"
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (markingScheme == "No Negative") NeonMint.copy(alpha = 0.15f) else CrimsonAlert.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "Penalty: $markingScheme",
                                        color = if (markingScheme == "No Negative") NeonMint else CrimsonAlert,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = currentQuestion.questionText,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            val currentAnswer = activeSession.userAnswers[currentQuestion.id] ?: ""

                            if (currentQuestion.questionType == QuestionType.NAT) {
                                OutlinedTextField(
                                    value = currentAnswer,
                                    onValueChange = { viewModel.setMockAnswer(currentQuestion.id, it) },
                                    label = { Text("Enter Numerical Value") },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ElectricCyan,
                                        unfocusedBorderColor = Color(0xFF334155),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                val options = remember(currentQuestion.optionsJson) {
                                    try {
                                        val arr = JSONArray(currentQuestion.optionsJson)
                                        (0 until arr.length()).map { arr.getString(it) }
                                    } catch (e: Exception) {
                                        emptyList()
                                    }
                                }

                                options.forEach { opt ->
                                    val optKey = opt.take(1).uppercase()
                                    val isSelected = if (currentQuestion.questionType == QuestionType.MSQ) {
                                        currentAnswer.split(",").map { it.trim().uppercase() }.contains(optKey)
                                    } else {
                                        currentAnswer.startsWith(optKey, ignoreCase = true)
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) ElectricBlue.copy(alpha = 0.3f) else NavySurface)
                                            .border(1.dp, if (isSelected) ElectricCyan else Color.Transparent, RoundedCornerShape(8.dp))
                                            .clickable {
                                                if (currentQuestion.questionType == QuestionType.MSQ) {
                                                    val set = currentAnswer.split(",").map { it.trim().uppercase() }.filter { it.isNotBlank() }.toMutableSet()
                                                    if (set.contains(optKey)) set.remove(optKey) else set.add(optKey)
                                                    viewModel.setMockAnswer(currentQuestion.id, set.sorted().joinToString(","))
                                                } else {
                                                    viewModel.setMockAnswer(currentQuestion.id, optKey)
                                                }
                                            }
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) ElectricCyan else NavyDark),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = optKey, color = if (isSelected) NavyDeep else Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = opt, color = TextPrimaryDark, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- BOTTOM EXAM NAVIGATION CONTROLS ---
        Surface(
            color = NavyDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        if (currentQuestion != null) {
                            viewModel.toggleMockMarkForReview(currentQuestion.id)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RoyalPurple)
                ) {
                    Text("Review", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        if (currentQuestion != null) {
                            viewModel.clearMockResponse(currentQuestion.id)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondaryDark)
                ) {
                    Text("Clear", fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        if (currentIndex < questions.size - 1) {
                            viewModel.selectMockQuestion(currentIndex + 1)
                        } else {
                            showSubmitDialog = true
                        }
                    },
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = NavyDeep)
                ) {
                    Text(if (currentIndex < questions.size - 1) "Save & Next" else "Review & Submit", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { showSubmitDialog = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert, contentColor = Color.White)
                ) {
                    Text("Submit", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showSubmitDialog) {
        AlertDialog(
            onDismissRequest = { showSubmitDialog = false },
            title = { Text("Submit GATE Mock Test?", color = Color.White) },
            text = {
                val answeredCount = activeSession.userAnswers.filter { it.value.isNotBlank() }.size
                Text(
                    "You have answered $answeredCount of ${questions.size} questions.\nAre you sure you want to submit and compute final scorecard?",
                    color = TextSecondaryDark
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitDialog = false
                        viewModel.submitMockTest()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonMint, contentColor = NavyDeep)
                ) {
                    Text("Yes, Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitDialog = false }) {
                    Text("Cancel", color = TextSecondaryDark)
                }
            },
            containerColor = NavyDark
        )
    }
}
