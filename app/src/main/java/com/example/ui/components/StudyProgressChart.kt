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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.DailyStudyLogEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

/**
 * Interactive Study Progress Chart showing daily preparation hours vs GATE 2027 target.
 * Rendered using Jetpack Compose Canvas with dynamic bar animations, target marker line,
 * day selector, and quick study session logging.
 */
@Composable
fun StudyProgressChartCard(
    dailyLogs: List<DailyStudyLogEntity>,
    targetGoalHours: Double = 4.0,
    onLogStudyHours: (hours: Double, questions: Int, subject: String, notes: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showLogDialog by remember { mutableStateOf(false) }
    var selectedLogIndex by remember { mutableIntStateOf(-1) }
    var chartDaysSpan by remember { mutableIntStateOf(7) } // 7 or 14 days

    // Filter logs for the active span
    val displayLogs = remember(dailyLogs, chartDaysSpan) {
        if (dailyLogs.isEmpty()) {
            emptyList()
        } else {
            dailyLogs.takeLast(chartDaysSpan)
        }
    }

    // Selected log or default to today's (last item)
    val activeLog = remember(displayLogs, selectedLogIndex) {
        if (selectedLogIndex in displayLogs.indices) {
            displayLogs[selectedLogIndex]
        } else {
            displayLogs.lastOrNull()
        }
    }

    // Key statistics
    val totalHoursThisWeek = remember(displayLogs) {
        displayLogs.sumOf { it.hoursStudied }
    }
    val avgHoursPerDay = remember(displayLogs) {
        if (displayLogs.isNotEmpty()) totalHoursThisWeek / displayLogs.size else 0.0
    }
    val targetMetDays = remember(displayLogs) {
        displayLogs.count { it.hoursStudied >= it.targetHours }
    }
    val adherencePercent = remember(displayLogs, targetMetDays) {
        if (displayLogs.isNotEmpty()) ((targetMetDays.toDouble() / displayLogs.size) * 100).toInt() else 0
    }
    val totalQuestionsSolved = remember(displayLogs) {
        displayLogs.sumOf { it.questionsSolved }
    }

    val todayDateStr = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
    val todayLog = displayLogs.find { it.date == todayDateStr }
    val todayHours = todayLog?.hoursStudied ?: 0.0
    val todayTarget = todayLog?.targetHours ?: targetGoalHours
    val todayProgressPercent = if (todayTarget > 0) ((todayHours / todayTarget) * 100).toInt() else 0

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Brush.horizontalGradient(listOf(ElectricCyan.copy(alpha = 0.5f), BrightAmber.copy(alpha = 0.3f))), RoundedCornerShape(20.dp))
            .testTag("study_progress_chart_card")
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // --- HEADER & LOG BUTTON ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "DAILY STUDY & GATE 2027 TARGET",
                            fontWeight = FontWeight.Bold,
                            color = ElectricCyan,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "Preparation Hours vs ${targetGoalHours.toInt()}h Daily Target",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }

                Button(
                    onClick = { showLogDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonMint),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("log_hours_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = NavyDeep,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Log Hours",
                        color = NavyDeep,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- TODAY'S PROGRESS GAUGE BAR ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(NavyDark)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Today's Target: ",
                                color = TextSecondaryDark,
                                fontSize = 12.sp
                            )
                            Text(
                                text = String.format(Locale.getDefault(), "%.1fh / %.1fh", todayHours, todayTarget),
                                fontWeight = FontWeight.Bold,
                                color = if (todayHours >= todayTarget) NeonMint else BrightAmber,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            if (todayHours >= todayTarget) {
                                Surface(
                                    color = NeonMint.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "🎯 TARGET MET",
                                        color = NeonMint,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            } else {
                                val remaining = (todayTarget - todayHours).coerceAtLeast(0.0)
                                Text(
                                    text = "(${String.format(Locale.getDefault(), "%.1fh", remaining)} left)",
                                    color = BrightAmber,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { (todayHours / todayTarget).toFloat().coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (todayHours >= todayTarget) NeonMint else ElectricCyan,
                            trackColor = NavyCardBorder
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "$todayProgressPercent%",
                        fontWeight = FontWeight.Black,
                        color = if (todayHours >= todayTarget) NeonMint else ElectricCyan,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // --- TIMESPAN SELECTOR (7 Days / 14 Days) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weekly Telemetry & Target Alignment",
                    color = TextSecondaryDark,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(NavyDark)
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SpanFilterChip(
                        label = "7 Days",
                        isSelected = chartDaysSpan == 7,
                        onClick = {
                            chartDaysSpan = 7
                            selectedLogIndex = -1
                        }
                    )
                    SpanFilterChip(
                        label = "14 Days",
                        isSelected = chartDaysSpan == 14,
                        onClick = {
                            chartDaysSpan = 14
                            selectedLogIndex = -1
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- COMPOSE CANVAS CHART ---
            if (displayLogs.isNotEmpty()) {
                StudyBarChartCanvas(
                    logs = displayLogs,
                    targetGoalHours = targetGoalHours,
                    selectedIndex = selectedLogIndex,
                    onSelectIndex = { index ->
                        selectedLogIndex = if (selectedLogIndex == index) -1 else index
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .testTag("study_canvas_chart")
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NavyDark),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No study logs available yet. Log your first session!",
                        color = TextSecondaryDark,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- CHART LEGEND ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendIndicator(color = NeonMint, label = "Target Met (≥ Target)")
                Spacer(modifier = Modifier.width(14.dp))
                LegendIndicator(color = BrightAmber, label = "In Progress (< Target)")
                Spacer(modifier = Modifier.width(14.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .height(2.dp)
                            .background(CrimsonAlert)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "GATE Target Line",
                        color = TextSecondaryDark,
                        fontSize = 10.sp
                    )
                }
            }

            // --- SELECTED DAY DETAIL EXPANSION ---
            AnimatedVisibility(
                visible = activeLog != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                activeLog?.let { log ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = NavyDark),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, if (log.hoursStudied >= log.targetHours) NeonMint.copy(alpha = 0.4f) else ElectricCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = formatDisplayDate(log.date),
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 13.sp
                                    )
                                }

                                val isMet = log.hoursStudied >= log.targetHours
                                Surface(
                                    color = if (isMet) NeonMint.copy(alpha = 0.2f) else BrightAmber.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (isMet) "✓ Goal Achieved (${String.format(Locale.getDefault(), "%.0f%%", (log.hoursStudied / log.targetHours) * 100)})" else "⏳ In Progress (${String.format(Locale.getDefault(), "%.0f%%", (log.hoursStudied / log.targetHours) * 100)})",
                                        color = if (isMet) NeonMint else BrightAmber,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                DetailStatBadge(
                                    label = "Studied",
                                    value = "${log.hoursStudied}h",
                                    color = if (log.hoursStudied >= log.targetHours) NeonMint else ElectricCyan,
                                    modifier = Modifier.weight(1f)
                                )
                                DetailStatBadge(
                                    label = "Target",
                                    value = "${log.targetHours}h",
                                    color = Color.White,
                                    modifier = Modifier.weight(1f)
                                )
                                DetailStatBadge(
                                    label = "Questions",
                                    value = "${log.questionsSolved}",
                                    color = BrightAmber,
                                    modifier = Modifier.weight(1f)
                                )
                                DetailStatBadge(
                                    label = "Focus",
                                    value = "${log.focusScore}%",
                                    color = NeonMint,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            if (log.subjectsStudied.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = null,
                                        tint = TextSecondaryDark,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Topics: ${log.subjectsStudied}",
                                        color = TextSecondaryDark,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            if (log.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "“${log.notes}”",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // --- STATS SUMMARY ROW ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryMetricItem(
                    label = "Total Hours",
                    value = String.format(Locale.getDefault(), "%.1fh", totalHoursThisWeek),
                    color = ElectricCyan,
                    modifier = Modifier.weight(1f)
                )
                SummaryMetricItem(
                    label = "Daily Avg",
                    value = String.format(Locale.getDefault(), "%.1fh/d", avgHoursPerDay),
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                SummaryMetricItem(
                    label = "Target Hit",
                    value = "$adherencePercent%",
                    color = if (adherencePercent >= 70) NeonMint else BrightAmber,
                    modifier = Modifier.weight(1f)
                )
                SummaryMetricItem(
                    label = "PYQs/MCQs",
                    value = "$totalQuestionsSolved",
                    color = NeonMint,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // --- LOG HOURS DIALOG ---
    if (showLogDialog) {
        LogStudySessionDialog(
            defaultTarget = targetGoalHours,
            onDismiss = { showLogDialog = false },
            onConfirm = { hours, questions, subject, notes ->
                onLogStudyHours(hours, questions, subject, notes)
                showLogDialog = false
            }
        )
    }
}

/**
 * Custom Canvas-rendered Dual Metric Bar & Target Line Chart
 */
@Composable
private fun StudyBarChartCanvas(
    logs: List<DailyStudyLogEntity>,
    targetGoalHours: Double,
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation progress
    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(logs.size) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    val maxHours = remember(logs, targetGoalHours) {
        val maxLogged = logs.maxOfOrNull { max(it.hoursStudied, it.targetHours) } ?: targetGoalHours
        (max(maxLogged, targetGoalHours) + 1.5).coerceAtLeast(6.0)
    }

    BoxWithConstraints(modifier = modifier) {
        val density = androidx.compose.ui.platform.LocalDensity.current
        val chartWidth = constraints.maxWidth.toFloat()
        val chartHeight = constraints.maxHeight.toFloat()

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(logs.size) {
                    detectTapGestures { offset ->
                        val leftPadding = 32f
                        val rightPadding = 16f
                        val bottomPadding = 32f
                        val topPadding = 16f
                        val availableWidth = chartWidth - leftPadding - rightPadding

                        if (logs.isNotEmpty()) {
                            val slotWidth = availableWidth / logs.size
                            val touchX = offset.x - leftPadding
                            if (touchX >= 0 && touchX <= availableWidth) {
                                val clickedIdx = (touchX / slotWidth).toInt().coerceIn(0, logs.size - 1)
                                onSelectIndex(clickedIdx)
                            }
                        }
                    }
                }
        ) {
            val leftPadding = 36.dp.toPx()
            val rightPadding = 16.dp.toPx()
            val bottomPadding = 28.dp.toPx()
            val topPadding = 18.dp.toPx()

            val plotWidth = size.width - leftPadding - rightPadding
            val plotHeight = size.height - topPadding - bottomPadding

            // --- 1. HORIZONTAL GRID LINES & LABELS ---
            val gridSteps = 4
            for (i in 0..gridSteps) {
                val stepHours = (maxHours / gridSteps) * i
                val y = topPadding + plotHeight - (plotHeight * (stepHours / maxHours).toFloat())

                // Line
                drawLine(
                    color = Color.White.copy(alpha = 0.08f),
                    start = Offset(leftPadding, y),
                    end = Offset(size.width - rightPadding, y),
                    strokeWidth = 1.dp.toPx()
                )

                // Label
                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.argb(140, 150, 170, 200)
                        textSize = 24f
                        isAntiAlias = true
                        textAlign = android.graphics.Paint.Align.RIGHT
                    }
                    drawText("${stepHours.toInt()}h", leftPadding - 8.dp.toPx(), y + 8f, paint)
                }
            }

            // --- 2. TARGET GOAL REFERENCE LINE (DASHED) ---
            val targetY = topPadding + plotHeight - (plotHeight * (targetGoalHours / maxHours).toFloat())
            drawLine(
                color = CrimsonAlert.copy(alpha = 0.85f),
                start = Offset(leftPadding, targetY),
                end = Offset(size.width - rightPadding, targetY),
                strokeWidth = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
            )

            // Target line badge text
            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.argb(230, 255, 80, 80)
                    textSize = 22f
                    isAntiAlias = true
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    textAlign = android.graphics.Paint.Align.RIGHT
                }
                drawText("Target ${targetGoalHours.toInt()}h", size.width - rightPadding, targetY - 6f, paint)
            }

            // --- 3. BARS FOR EACH DAY ---
            if (logs.isNotEmpty()) {
                val barSlotWidth = plotWidth / logs.size
                val barWidth = (barSlotWidth * 0.52f).coerceIn(12.dp.toPx(), 28.dp.toPx())

                logs.forEachIndexed { index, log ->
                    val centerX = leftPadding + (index * barSlotWidth) + (barSlotWidth / 2f)
                    val barLeft = centerX - (barWidth / 2f)
                    val isSelected = selectedIndex == index

                    // Target line for individual day if different
                    val dayTargetY = topPadding + plotHeight - (plotHeight * (log.targetHours / maxHours).toFloat())

                    // Calculate animated bar height
                    val barHeight = (plotHeight * (log.hoursStudied / maxHours).toFloat() * animationProgress.value).coerceAtLeast(4.dp.toPx())
                    val barTop = topPadding + plotHeight - barHeight

                    val isTargetMet = log.hoursStudied >= log.targetHours

                    // Bar Gradient
                    val barBrush = if (isTargetMet) {
                        Brush.verticalGradient(
                            colors = listOf(NeonMint, ElectricCyan),
                            startY = barTop,
                            endY = topPadding + plotHeight
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(BrightAmber, Color(0xFFF97316)),
                            startY = barTop,
                            endY = topPadding + plotHeight
                        )
                    }

                    // Bar Glow / Selection Highlight Background
                    if (isSelected) {
                        drawRoundRect(
                            color = ElectricCyan.copy(alpha = 0.15f),
                            topLeft = Offset(barLeft - 6.dp.toPx(), topPadding),
                            size = Size(barWidth + 12.dp.toPx(), plotHeight + bottomPadding - 4.dp.toPx()),
                            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                        )
                    }

                    // Background bar slot track
                    drawRoundRect(
                        color = NavyDark.copy(alpha = 0.8f),
                        topLeft = Offset(barLeft, topPadding),
                        size = Size(barWidth, plotHeight),
                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                    )

                    // Active filled bar
                    drawRoundRect(
                        brush = barBrush,
                        topLeft = Offset(barLeft, barTop),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                    )

                    // Individual day target notch if day target differs
                    drawLine(
                        color = Color.White.copy(alpha = 0.7f),
                        start = Offset(barLeft - 2.dp.toPx(), dayTargetY),
                        end = Offset(barLeft + barWidth + 2.dp.toPx(), dayTargetY),
                        strokeWidth = 2.dp.toPx()
                    )

                    // Hours value on top of bar
                    if (animationProgress.value > 0.8f) {
                        drawContext.canvas.nativeCanvas.apply {
                            val textPaint = android.graphics.Paint().apply {
                                color = if (isTargetMet) android.graphics.Color.parseColor("#00F5D4") else android.graphics.Color.parseColor("#FFBE0B")
                                textSize = if (logs.size > 10) 20f else 22f
                                isAntiAlias = true
                                typeface = android.graphics.Typeface.DEFAULT_BOLD
                                textAlign = android.graphics.Paint.Align.CENTER
                            }
                            drawText(
                                String.format(Locale.getDefault(), "%.1f", log.hoursStudied),
                                centerX,
                                (barTop - 6.dp.toPx()).coerceAtLeast(topPadding + 14f),
                                textPaint
                            )
                        }
                    }

                    // Date label at the bottom
                    drawContext.canvas.nativeCanvas.apply {
                        val dayLabel = formatShortDay(log.date)
                        val labelPaint = android.graphics.Paint().apply {
                            color = if (isSelected) android.graphics.Color.WHITE else android.graphics.Color.argb(160, 160, 180, 210)
                            textSize = if (logs.size > 10) 18f else 22f
                            isAntiAlias = true
                            typeface = if (isSelected) android.graphics.Typeface.DEFAULT_BOLD else android.graphics.Typeface.DEFAULT
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                        drawText(dayLabel, centerX, size.height - 6.dp.toPx(), labelPaint)
                    }
                }
            }
        }
    }
}

@Composable
private fun SpanFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) ElectricCyan else Color.Transparent,
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() }
    ) {
        Text(
            text = label,
            color = if (isSelected) NavyDeep else TextSecondaryDark,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun LegendIndicator(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            color = TextSecondaryDark,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun DetailStatBadge(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = NavyCard,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                color = TextSecondaryDark,
                fontSize = 10.sp
            )
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                color = color,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun SummaryMetricItem(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = NavyDark,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontWeight = FontWeight.Black,
                color = color,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = TextSecondaryDark,
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

/**
 * Dialog to quickly log completed study hours, questions, subject, and notes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogStudySessionDialog(
    defaultTarget: Double,
    onDismiss: () -> Unit,
    onConfirm: (hours: Double, questions: Int, subject: String, notes: String) -> Unit
) {
    var hoursInput by remember { mutableStateOf("1.5") }
    var questionsInput by remember { mutableStateOf("10") }
    var selectedSubject by remember { mutableStateOf("Operating Systems") }
    var notesInput by remember { mutableStateOf("") }

    val quickHours = listOf(0.5, 1.0, 1.5, 2.0, 3.0, 4.0)
    val subjects = listOf(
        "Operating Systems",
        "DBMS",
        "Algorithms",
        "Computer Networks",
        "Digital Logic & COA",
        "Theory of Computation",
        "Compiler Design",
        "Engineering Mathematics"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = NavyCard),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ElectricCyan.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                .testTag("log_study_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = NeonMint,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Log Study Progress",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark)
                    }
                }

                Text(
                    text = "Track today's study hours against your GATE 2027 daily preparation goal.",
                    color = TextSecondaryDark,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                )

                // Quick Hour Chips
                Text(
                    text = "Quick Select Hours:",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickHours.forEach { h ->
                        Surface(
                            color = if (hoursInput == h.toString()) NeonMint else NavyDark,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { hoursInput = h.toString() }
                        ) {
                            Text(
                                text = "${h}h",
                                color = if (hoursInput == h.toString()) NavyDeep else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Custom Hours & Questions Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = hoursInput,
                        onValueChange = { hoursInput = it },
                        label = { Text("Hours Studied") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = NavyCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = questionsInput,
                        onValueChange = { questionsInput = it },
                        label = { Text("Questions Solved") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = NavyCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Subject Select
                Text(
                    text = "Subject Studied:",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                var expandedSubject by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedSubject,
                    onExpandedChange = { expandedSubject = !expandedSubject }
                ) {
                    OutlinedTextField(
                        value = selectedSubject,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSubject) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = NavyCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedSubject,
                        onDismissRequest = { expandedSubject = false },
                        modifier = Modifier.background(NavyCard)
                    ) {
                        subjects.forEach { subj ->
                            DropdownMenuItem(
                                text = { Text(subj, color = Color.White) },
                                onClick = {
                                    selectedSubject = subj
                                    expandedSubject = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Notes
                OutlinedTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it },
                    label = { Text("Topic Notes / Subtopic (Optional)") },
                    placeholder = { Text("e.g. Completed Banker's Algorithm & 5 PYQs", color = TextSecondaryDark) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = NavyCardBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = TextSecondaryDark)
                    }

                    Button(
                        onClick = {
                            val hrs = hoursInput.toDoubleOrNull() ?: 1.0
                            val qCount = questionsInput.toIntOrNull() ?: 0
                            onConfirm(hrs, qCount, selectedSubject, notesInput)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonMint),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save Log", color = NavyDeep, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun formatShortDay(dateStr: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateStr) ?: return dateStr
        val outSdf = SimpleDateFormat("E d", Locale.getDefault())
        outSdf.format(date)
    } catch (e: Exception) {
        dateStr
    }
}

private fun formatDisplayDate(dateStr: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateStr) ?: return dateStr
        val outSdf = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        outSdf.format(date)
    } catch (e: Exception) {
        dateStr
    }
}
