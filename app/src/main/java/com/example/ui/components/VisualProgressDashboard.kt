package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * Recharts-inspired Visual Dashboard for GATE 2027 CSE.
 * Renders smooth Area/Line charts with interactive tooltips, reference cutoff lines,
 * subject mastery horizontal distributions, and mock score trajectory tracking.
 */
@Composable
fun VisualProgressDashboardCard(
    mockAttempts: List<MockAttemptEntity>,
    mockTests: List<MockTestEntity>,
    subjects: List<SubjectEntity>,
    topics: List<TopicEntity>,
    mistakes: List<MistakeEntity>,
    onSelectSubject: (SubjectEntity) -> Unit = {},
    onTakeMockClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableIntStateOf(0) } // 0: Mock Score History, 1: Subject Mastery Trends, 2: Accuracy & AIR Analysis
    var selectedMockIndex by remember { mutableIntStateOf(-1) }

    val sortedAttempts = remember(mockAttempts) {
        mockAttempts.sortedBy { it.timestamp }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, NavyCardBorder, RoundedCornerShape(20.dp))
            .testTag("visual_progress_dashboard_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ElectricCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShowChart,
                            contentDescription = "Visual Dashboard",
                            tint = ElectricCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Visual Performance Dashboard",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Telemetry & Score Trajectory Engine",
                            color = TextSecondaryDark,
                            fontSize = 11.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = NavySurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(NeonMint)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "${sortedAttempts.size} Mocks Logged",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tab Selector (Recharts View Mode)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(NavySurface)
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val tabs = listOf("Mock Score Curve", "Subject Mastery", "AIR & Accuracy")
                tabs.forEachIndexed { idx, title ->
                    val isSelected = activeTab == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) ElectricCyan else Color.Transparent)
                            .clickable {
                                activeTab = idx
                                selectedMockIndex = -1
                            }
                            .padding(vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            color = if (isSelected) NavyDeep else TextSecondaryDark,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            AnimatedContent(
                targetState = activeTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "DashboardTabTransition"
            ) { currentTab ->
                when (currentTab) {
                    0 -> MockScoreHistoryChart(
                        attempts = sortedAttempts,
                        mockTests = mockTests,
                        selectedIndex = selectedMockIndex,
                        onSelectIndex = { selectedMockIndex = it },
                        onTakeMockClick = onTakeMockClick
                    )
                    1 -> SubjectMasteryTrendsView(
                        subjects = subjects,
                        topics = topics,
                        onSelectSubject = onSelectSubject
                    )
                    2 -> AirAndAccuracyAnalysisView(
                        attempts = sortedAttempts,
                        mistakes = mistakes,
                        onTakeMockClick = onTakeMockClick
                    )
                }
            }
        }
    }
}

/**
 * 1. RECHARTS-STYLE MOCK EXAM SCORE HISTORY CHART
 * Smooth Area/Line Chart with reference cutoff lines, data points, interactive scrubbing, and tooltips.
 */
@Composable
private fun MockScoreHistoryChart(
    attempts: List<MockAttemptEntity>,
    mockTests: List<MockTestEntity>,
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit,
    onTakeMockClick: () -> Unit
) {
    if (attempts.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(NavySurface)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Quiz,
                    contentDescription = null,
                    tint = TextSecondaryDark,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No mock exams taken yet",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Text(
                    text = "Complete your first GATE CSE simulation to render trend charts",
                    color = TextSecondaryDark,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onTakeMockClick,
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = NavyDeep),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("Start Mock Simulator", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
        return
    }

    val selectedAttempt = if (selectedIndex in attempts.indices) {
        attempts[selectedIndex]
    } else {
        attempts.lastOrNull()
    }

    val highestScore = remember(attempts) { attempts.maxOfOrNull { it.score } ?: 0.0 }
    val latestScore = attempts.lastOrNull()?.score ?: 0.0
    val scoreDelta = if (attempts.size > 1) {
        latestScore - attempts[attempts.size - 2].score
    } else 0.0

    Column {
        // High-level Stats Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(NavySurface)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "CURRENT SCORE", color = TextSecondaryDark, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = String.format(Locale.US, "%.1f", latestScore),
                        color = ElectricCyan,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(text = " / 100", color = TextSecondaryDark, fontSize = 11.sp, modifier = Modifier.padding(bottom = 3.dp))
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "PEAK SCORE", color = TextSecondaryDark, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                Text(
                    text = String.format(Locale.US, "%.1f", highestScore),
                    color = NeonMint,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(text = "TRAJECTORY", color = TextSecondaryDark, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (scoreDelta >= 0) NeonMint.copy(alpha = 0.2f) else CrimsonAlert.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = if (scoreDelta >= 0) "+${String.format(Locale.US, "%.1f", scoreDelta)}" else String.format(Locale.US, "%.1f", scoreDelta),
                        color = if (scoreDelta >= 0) NeonMint else CrimsonAlert,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Recharts Canvas Chart Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(NavySurface)
                .padding(horizontal = 10.dp, vertical = 12.dp)
        ) {
            val scores = attempts.map { it.score.toFloat() }
            val maxScale = max(100f, (scores.maxOrNull() ?: 50f) + 10f)

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(attempts) {
                        detectTapGestures { tapOffset ->
                            val width = size.width
                            val step = if (attempts.size > 1) width / (attempts.size - 1) else width
                            val tappedIdx = if (attempts.size > 1) {
                                ((tapOffset.x + step / 2) / step).toInt().coerceIn(0, attempts.size - 1)
                            } else 0
                            onSelectIndex(tappedIdx)
                        }
                    }
            ) {
                val chartWidth = size.width
                val chartHeight = size.height - 24f // leave room for x-axis labels
                val count = attempts.size

                // 1. Draw Grid Lines (Y-Axis)
                val gridLevels = listOf(25f, 50f, 75f, 100f)
                gridLevels.forEach { lvl ->
                    val y = chartHeight - (lvl / maxScale * chartHeight)
                    drawLine(
                        color = NavyCardBorder.copy(alpha = 0.5f),
                        start = Offset(0f, y),
                        end = Offset(chartWidth, y),
                        strokeWidth = 1f
                    )
                }

                // 2. Reference Line: GATE Cutoff (28.5 marks)
                val cutoffY = chartHeight - (28.5f / maxScale * chartHeight)
                drawLine(
                    color = BrightAmber.copy(alpha = 0.7f),
                    start = Offset(0f, cutoffY),
                    end = Offset(chartWidth, cutoffY),
                    strokeWidth = 1.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )

                // 3. Reference Line: Top 500 Target (60 marks)
                val topTargetY = chartHeight - (60.0f / maxScale * chartHeight)
                drawLine(
                    color = NeonMint.copy(alpha = 0.6f),
                    start = Offset(0f, topTargetY),
                    end = Offset(chartWidth, topTargetY),
                    strokeWidth = 1.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                )

                if (count == 1) {
                    val ptX = chartWidth / 2f
                    val ptY = chartHeight - (scores[0] / maxScale * chartHeight)
                    drawCircle(color = ElectricCyan, radius = 6.dp.toPx(), center = Offset(ptX, ptY))
                    drawCircle(color = Color.White, radius = 3.dp.toPx(), center = Offset(ptX, ptY))
                    return@Canvas
                }

                // Build Points
                val points = scores.mapIndexed { idx, s ->
                    val x = (idx.toFloat() / (count - 1)) * chartWidth
                    val y = chartHeight - (s / maxScale * chartHeight)
                    Offset(x, y)
                }

                // 4. Fill Area Gradient (Recharts Area style)
                val fillPath = Path().apply {
                    moveTo(points.first().x, chartHeight)
                    points.forEach { pt -> lineTo(pt.x, pt.y) }
                    lineTo(points.last().x, chartHeight)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(ElectricCyan.copy(alpha = 0.35f), ElectricCyan.copy(alpha = 0.02f)),
                        startY = 0f,
                        endY = chartHeight
                    )
                )

                // 5. Smooth Curve Stroke
                val linePath = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    for (i in 0 until points.size - 1) {
                        val p0 = points[i]
                        val p1 = points[i + 1]
                        val controlPt1 = Offset(p0.x + (p1.x - p0.x) / 2f, p0.y)
                        val controlPt2 = Offset(p0.x + (p1.x - p0.x) / 2f, p1.y)
                        cubicTo(controlPt1.x, controlPt1.y, controlPt2.x, controlPt2.y, p1.x, p1.y)
                    }
                }

                drawPath(
                    path = linePath,
                    color = ElectricCyan,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // 6. Draw Data Dots
                points.forEachIndexed { idx, pt ->
                    val isHovered = (selectedIndex == idx) || (selectedIndex == -1 && idx == points.size - 1)
                    if (isHovered) {
                        drawCircle(
                            color = ElectricCyan.copy(alpha = 0.4f),
                            radius = 9.dp.toPx(),
                            center = pt
                        )
                    }
                    drawCircle(
                        color = if (isHovered) NeonMint else ElectricCyan,
                        radius = 4.5.dp.toPx(),
                        center = pt
                    )
                    drawCircle(
                        color = NavyDeep,
                        radius = 2.dp.toPx(),
                        center = pt
                    )
                }
            }

            // Legend indicators
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp, 2.dp).background(BrightAmber))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(text = "Cutoff 28.5", color = TextSecondaryDark, fontSize = 9.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp, 2.dp).background(NeonMint))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(text = "Top 500 (60)", color = TextSecondaryDark, fontSize = 9.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Interactive Selected Tooltip Detail Card
        selectedAttempt?.let { attempt ->
            val matchingTest = mockTests.find { it.id == attempt.mockTestId }
            val sdf = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
            val dateStr = sdf.format(Date(attempt.timestamp))
            val accuracy = if (attempt.attemptedCount > 0) {
                ((attempt.correctCount.toDouble() / attempt.attemptedCount) * 100).toInt()
            } else 0

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = NavySurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = matchingTest?.title ?: "Mock Simulation Attempt",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = dateStr,
                            color = TextSecondaryDark,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "SCORE", color = TextSecondaryDark, fontSize = 9.sp)
                            Text(
                                text = "${attempt.score} / ${attempt.totalMarks.toInt()}",
                                color = ElectricCyan,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "ACCURACY", color = TextSecondaryDark, fontSize = 9.sp)
                            Text(
                                text = "$accuracy%",
                                color = if (accuracy >= 75) NeonMint else BrightAmber,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "CORRECT / ATTEMPTED", color = TextSecondaryDark, fontSize = 9.sp)
                            Text(
                                text = "${attempt.correctCount} / ${attempt.attemptedCount}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "ESTIMATED AIR", color = TextSecondaryDark, fontSize = 9.sp)
                            val estAir = when {
                                attempt.score >= 70 -> "< 200"
                                attempt.score >= 60 -> "200 - 600"
                                attempt.score >= 50 -> "600 - 1800"
                                attempt.score >= 40 -> "1800 - 4500"
                                attempt.score >= 28.5 -> "Qualified"
                                else -> "Not Qualified"
                            }
                            Text(
                                text = estAir,
                                color = if (attempt.score >= 50) NeonMint else BrightAmber,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 2. SUBJECT-WISE MASTERY & COMPETENCY TRENDS
 * Comprehensive multi-bar progress and telemetry for all 10 GATE CSE disciplines.
 */
@Composable
private fun SubjectMasteryTrendsView(
    subjects: List<SubjectEntity>,
    topics: List<TopicEntity>,
    onSelectSubject: (SubjectEntity) -> Unit
) {
    val displaySubjects = remember(subjects) {
        if (subjects.isNotEmpty()) subjects else listOf(
            SubjectEntity("os", "Operating System", "08", "terminal", 12.0, "#10B981", 8),
            SubjectEntity("algo", "Algorithms", "05", "account_tree", 12.0, "#EC4899", 5),
            SubjectEntity("dbms", "Databases", "09", "storage", 10.0, "#00E5FF", 9),
            SubjectEntity("cn", "Computer Networks", "10", "lan", 11.0, "#A855F7", 10),
            SubjectEntity("em", "Engineering Mathematics", "01", "functions", 15.0, "#00E5FF", 1)
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "10 GATE CSE Disciplines • Preparedness Telemetry",
            color = TextSecondaryDark,
            fontSize = 11.sp
        )

        displaySubjects.forEach { subj ->
            val subTopics = topics.filter { it.subjectId == subj.id }
            val mastery = if (subTopics.isNotEmpty()) {
                subTopics.map { it.masteryLevel }.average()
            } else {
                when (subj.id) {
                    "os" -> 74.0
                    "algo" -> 48.0
                    "dbms" -> 68.0
                    "cn" -> 58.0
                    "em" -> 62.0
                    "coa" -> 45.0
                    "toc" -> 65.0
                    "pds" -> 80.0
                    "dl" -> 78.0
                    "cd" -> 52.0
                    else -> 60.0
                }
            }

            val statusColor = when {
                mastery >= 75 -> NeonMint
                mastery >= 50 -> ElectricCyan
                else -> BrightAmber
            }

            val statusLabel = when {
                mastery >= 75 -> "Mastered"
                mastery >= 50 -> "On Track"
                else -> "Needs Drill"
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = NavySurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onSelectSubject(subj) }
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = statusColor.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = subj.code,
                                    color = statusColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = subj.name,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = statusLabel,
                                color = statusColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${mastery.toInt()}%",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Recharts-style gradient bar
                    LinearProgressIndicator(
                        progress = { (mastery / 100.0).toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = statusColor,
                        trackColor = NavyDark
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Weightage: ${subj.totalWeightage.toInt()}% in GATE CSE",
                            color = TextSecondaryDark,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "Tap to open drill →",
                            color = ElectricCyan,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * 3. ACCURACY & AIR PROJECTION ANALYSIS
 * Stacked response distribution and error ledger metrics.
 */
@Composable
private fun AirAndAccuracyAnalysisView(
    attempts: List<MockAttemptEntity>,
    mistakes: List<MistakeEntity>,
    onTakeMockClick: () -> Unit
) {
    val totalAttempted = attempts.sumOf { it.attemptedCount }
    val totalCorrect = attempts.sumOf { it.correctCount }
    val totalIncorrect = attempts.sumOf { it.incorrectCount }
    val overallAccuracy = if (totalAttempted > 0) ((totalCorrect.toDouble() / totalAttempted) * 100).toInt() else 0

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Stacked Accuracy Bar
        Text(text = "Overall Question Response Distribution", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)

        if (totalAttempted > 0) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(NavySurface)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                ) {
                    val correctWeight = (totalCorrect.toFloat() / totalAttempted).coerceIn(0.01f, 1f)
                    val incorrectWeight = (totalIncorrect.toFloat() / totalAttempted).coerceIn(0.01f, 1f)

                    Box(
                        modifier = Modifier
                            .weight(correctWeight)
                            .fillMaxHeight()
                            .background(NeonMint)
                    )
                    Box(
                        modifier = Modifier
                            .weight(incorrectWeight)
                            .fillMaxHeight()
                            .background(CrimsonAlert)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(NeonMint))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Correct: $totalCorrect ($overallAccuracy%)", color = Color.White, fontSize = 11.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CrimsonAlert))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Incorrect: $totalIncorrect (${100 - overallAccuracy}%)", color = Color.White, fontSize = 11.sp)
                    }
                }
            }
        }

        // Metacognitive Mistake Distribution
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "Error Trap Ledger Analysis", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Logged mistakes categorized to eliminate negative marking in GATE 2027.", color = TextSecondaryDark, fontSize = 10.sp)

                Spacer(modifier = Modifier.height(10.dp))

                val conceptualCount = mistakes.count { it.category == MistakeCategory.CONCEPTUAL }
                val calculationCount = mistakes.count { it.category == MistakeCategory.CALCULATION }
                val timeCount = mistakes.count { it.category == MistakeCategory.TIME_PRESSURE }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MistakeMetricChip(label = "Conceptual", count = conceptualCount, color = CrimsonAlert, modifier = Modifier.weight(1f))
                    MistakeMetricChip(label = "Calculation", count = calculationCount, color = BrightAmber, modifier = Modifier.weight(1f))
                    MistakeMetricChip(label = "Time Trap", count = timeCount, color = RoyalPurple, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun MistakeMetricChip(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count.toString(), color = color, fontWeight = FontWeight.Black, fontSize = 16.sp)
            Text(text = label, color = TextSecondaryDark, fontSize = 9.sp, maxLines = 1)
        }
    }
}
