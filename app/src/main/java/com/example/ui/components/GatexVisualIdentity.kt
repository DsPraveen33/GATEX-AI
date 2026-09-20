package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.ScreenDestination
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * AI Study Orb State Definitions
 */
enum class AIOrbState {
    Idle,
    Thinking,
    Planning,
    Speaking,
    Error,
    Success
}

/**
 * Clean Neural G + AI Star Vector Symbol
 */
@Composable
fun GatexOrbLogo(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp
) {
    Box(
        modifier = modifier
            .size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val cx = w / 2f
            val cy = h / 2f
            val r = w * 0.44f

            // Outer subtle radial aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF6366F1).copy(alpha = 0.35f), Color.Transparent),
                    center = Offset(cx, cy),
                    radius = r * 1.25f
                ),
                center = Offset(cx, cy),
                radius = r * 1.25f
            )

            // Rounded Hexagon / Polygon Base
            val hexPath = Path()
            val sides = 6
            for (i in 0 until sides) {
                val angle = Math.toRadians((60.0 * i) - 30.0)
                val x = cx + (r * cos(angle)).toFloat()
                val y = cy + (r * sin(angle)).toFloat()
                if (i == 0) hexPath.moveTo(x, y) else hexPath.lineTo(x, y)
            }
            hexPath.close()

            // Draw Hexagon background with rich vibrant gradient
            drawPath(
                path = hexPath,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF4F46E5), Color(0xFF06B6D4), Color(0xFF7C3AED)),
                    start = Offset(0f, 0f),
                    end = Offset(w, h)
                )
            )

            // Inner sphere / nucleus
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF38BDF8), Color(0xFF4F46E5), Color(0xFF1E1B4B)),
                    center = Offset(cx - r * 0.2f, cy - r * 0.2f),
                    radius = r * 0.75f
                ),
                center = Offset(cx, cy),
                radius = r * 0.72f
            )

            // Outer border stroke
            drawPath(
                path = hexPath,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF93C5FD), Color(0xFF818CF8), Color(0xFF38BDF8))
                ),
                style = Stroke(width = (size.value * 0.04f).dp.toPx())
            )
        }

        // Centered "G" letter
        Text(
            text = "G",
            fontWeight = FontWeight.Black,
            color = Color.White,
            fontSize = (size.value * 0.44f).sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun GatexSymbol(
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    tint: Color = ElectricCyan
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.25f))
            .background(
                Brush.linearGradient(
                    colors = listOf(NavyCard, NavySurface)
                )
            )
            .border(1.dp, tint.copy(alpha = 0.5f), RoundedCornerShape(size * 0.25f)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.75f)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val radius = this.size.width * 0.38f

            // 4 Neural nodes around perimeter
            val nodeAngles = listOf(0.0, Math.PI / 2, Math.PI, 3 * Math.PI / 2)
            val nodePositions = nodeAngles.map { angle ->
                Offset(
                    center.x + (radius * cos(angle)).toFloat(),
                    center.y + (radius * sin(angle)).toFloat()
                )
            }

            // Neural connection lines to center
            nodePositions.forEach { pos ->
                drawLine(
                    color = tint.copy(alpha = 0.45f),
                    start = center,
                    end = pos,
                    strokeWidth = 1.5.dp.toPx()
                )
            }

            // Outer connecting ring
            drawCircle(
                color = tint.copy(alpha = 0.25f),
                center = center,
                radius = radius,
                style = Stroke(width = 1.dp.toPx())
            )

            // Outer node circles
            nodePositions.forEach { pos ->
                drawCircle(
                    color = tint,
                    center = pos,
                    radius = 2.5.dp.toPx()
                )
            }

            // Center AI Four-Point Star
            val starRadius = radius * 0.55f
            val starPath = Path().apply {
                moveTo(center.x, center.y - starRadius)
                quadraticTo(center.x, center.y, center.x + starRadius, center.y)
                quadraticTo(center.x, center.y, center.x, center.y + starRadius)
                quadraticTo(center.x, center.y, center.x - starRadius, center.y)
                quadraticTo(center.x, center.y, center.x, center.y - starRadius)
                close()
            }
            drawPath(path = starPath, color = tint)
        }
    }
}

/**
 * Animated GATEX AI Study Orb with 6 dynamic operational states
 */
@Composable
fun AIStudyOrb(
    state: AIOrbState = AIOrbState.Idle,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ai_orb_anim")

    // Subtle breath / pulse scale
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = if (state == AIOrbState.Thinking) 0.85f else 0.95f,
        targetValue = if (state == AIOrbState.Thinking) 1.15f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (state == AIOrbState.Thinking) 700 else 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Orbital rotation
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (state == AIOrbState.Planning) 3000 else 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val orbColor = when (state) {
        AIOrbState.Idle -> ElectricCyan
        AIOrbState.Thinking -> BrightAmber
        AIOrbState.Planning -> RoyalPurple
        AIOrbState.Speaking -> NeonMint
        AIOrbState.Error -> CrimsonAlert
        AIOrbState.Success -> NeonMint
    }

    Box(
        modifier = modifier
            .size(size)
            .scale(pulseScale),
        contentAlignment = Alignment.Center
    ) {
        // Outer diffuse glow ring
        Box(
            modifier = Modifier
                .size(size * 1.1f)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(orbColor.copy(alpha = 0.35f), Color.Transparent)
                    )
                )
        )

        // Canvas for animated orbiting particles
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val baseRadius = this.size.width * 0.38f

            // Inner glowing nucleus
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(orbColor, orbColor.copy(alpha = 0.4f), Color.Transparent),
                    center = center,
                    radius = baseRadius * 0.9f
                ),
                center = center,
                radius = baseRadius * 0.9f
            )

            // Orbiting satellite particles
            val particleCount = if (state == AIOrbState.Planning) 4 else 2
            val radAngle = Math.toRadians(rotationAngle.toDouble())

            for (i in 0 until particleCount) {
                val offsetAngle = radAngle + (i * (2 * Math.PI / particleCount))
                val px = center.x + (baseRadius * cos(offsetAngle)).toFloat()
                val py = center.y + (baseRadius * sin(offsetAngle)).toFloat()

                drawCircle(
                    color = Color.White.copy(alpha = 0.9f),
                    center = Offset(px, py),
                    radius = 2.2.dp.toPx()
                )
            }
        }

        // Center Neural Star Emblem
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = "AI Active",
            tint = NavyDeep,
            modifier = Modifier.size(size * 0.45f)
        )
    }
}

/**
 * Universal AI Command Bar Modal (Ask GATEX... / Ctrl+K)
 * Directly parses natural language intent and routes to any agent / exam tool immediately.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AICommandBarModal(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onNavigate: (ScreenDestination) -> Unit,
    onExecuteAction: (String) -> Unit
) {
    if (!isOpen) return

    var query by remember { mutableStateOf("") }

    val promptSuggestions = listOf(
        "Give me 10 OS PYQs" to ScreenDestination.PYQs,
        "What should I revise?" to ScreenDestination.RevisionDeck,
        "Explain deadlock condition" to ScreenDestination.AITutor,
        "Create a 30-minute study plan" to ScreenDestination.Dashboard,
        "Show my repeated mistakes" to ScreenDestination.MistakeBook,
        "Start a full-length mock" to ScreenDestination.MockTests,
        "Launch Focus Mode (OS Deadlocks)" to ScreenDestination.TopicDetail("os_deadlocks"),
        "Open Formula Bank" to ScreenDestination.FormulaBank,
        "Open Concept Lab" to ScreenDestination.ConceptLab,
        "Open C Coding Sandbox" to ScreenDestination.CodingLab,
        "Check GATE 2027 Readiness" to ScreenDestination.Analytics
    )

    val filteredSuggestions = remember(query) {
        if (query.isBlank()) {
            promptSuggestions
        } else {
            promptSuggestions.filter { (prompt, _) ->
                prompt.contains(query, ignoreCase = true)
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = NavyDark),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.5.dp,
                    Brush.horizontalGradient(listOf(ElectricCyan, RoyalPurple)),
                    RoundedCornerShape(20.dp)
                )
                .testTag("ai_command_bar_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header with Orb
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AIStudyOrb(state = AIOrbState.Planning, size = 32.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "GATEX AI COMMAND BAR",
                                fontWeight = FontWeight.Black,
                                color = ElectricCyan,
                                fontSize = 13.sp,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Ask anything or command study actions",
                                color = TextSecondaryDark,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Input Field
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = {
                        Text(
                            text = "Ask GATEX: e.g., '10 OS PYQs', 'Revise DBMS'...",
                            color = TextSecondaryDark,
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = ElectricCyan
                        )
                    },
                    trailingIcon = {
                        if (query.isNotBlank()) {
                            IconButton(onClick = {
                                onExecuteAction(query)
                                onDismiss()
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Submit",
                                    tint = NeonMint
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = NavyCardBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = NavySurface,
                        unfocusedContainerColor = NavySurface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_command_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "QUICK AGENT SHORTCUTS",
                    color = TextSecondaryDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredSuggestions) { (prompt, destination) ->
                        Surface(
                            color = NavyCard,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    onNavigate(destination)
                                    onDismiss()
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = BrightAmber,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = prompt,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = TextSecondaryDark,
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

/**
 * Intelligent Rescue Mode Recovery Banner (rebalances study without user punishment)
 */
@Composable
fun RescueModeBanner(
    missedTasksCount: Int = 2,
    onStartRescue: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (missedTasksCount <= 0) return

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                Brush.horizontalGradient(listOf(CrimsonAlert.copy(alpha = 0.8f), BrightAmber.copy(alpha = 0.8f))),
                RoundedCornerShape(16.dp)
            )
            .testTag("rescue_mode_banner")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Healing,
                        contentDescription = "Rescue Mode",
                        tint = BrightAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "RESCUE MODE ACTIVE",
                        fontWeight = FontWeight.Black,
                        color = BrightAmber,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CrimsonAlert.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "$missedTasksCount Missed Tasks",
                        color = CrimsonAlert,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Auto-Rebalanced Catch-Up Schedule",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 15.sp
            )

            Text(
                text = "No worries! GATEX AI has redistributed your overdue topics into two light 45-minute recovery sessions: Today (Revision & Weak PYQs) and Tomorrow.",
                color = TextSecondaryDark,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )

            Button(
                onClick = onStartRescue,
                colors = ButtonDefaults.buttonColors(containerColor = BrightAmber, contentColor = NavyDeep),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("launch_rescue_plan_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Launch 45-Min Recovery Drill",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
