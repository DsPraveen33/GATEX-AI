package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.ScreenDestination
import java.util.Locale

@Composable
fun MockResultScreen(attemptId: Long, viewModel: MainViewModel) {
    val context = LocalContext.current
    val mockAttempts by viewModel.mockAttempts.collectAsStateWithLifecycle()
    val attempt = mockAttempts.find { it.id == attemptId } ?: mockAttempts.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "GATE CSE Scorecard & Diagnostic",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "Detailed Analysis & Estimated All-India Rank (AIR)",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }

                if (attempt != null) {
                    IconButton(onClick = {
                        val scoreText = """
                            🏆 GATEX AI — Mock Test Scorecard
                            Score: ${String.format(Locale.US, "%.2f", attempt.score)} / ${attempt.totalMarks.toInt()}
                            Correct: ${attempt.correctCount} | Incorrect: ${attempt.incorrectCount}
                            Accuracy: ${if (attempt.attemptedCount > 0) (attempt.correctCount * 100 / attempt.attemptedCount) else 0}%
                            Est. GATE 2027 AIR: ${estimateAIR(attempt.score)}
                        """.trimIndent()

                        val sendIntent = android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            putExtra(android.content.Intent.EXTRA_TEXT, scoreText)
                            putExtra(android.content.Intent.EXTRA_SUBJECT, "My GATE CSE Mock Scorecard")
                            type = "text/plain"
                            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(android.content.Intent.createChooser(sendIntent, "Share Scorecard").apply {
                            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                        })
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share Scorecard", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        if (attempt != null) {
            // --- MAIN SCORE CARD ---
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "TOTAL ASSESSMENT SCORE",
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = String.format(Locale.US, "%.2f", attempt.score),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Black,
                                fontSize = 38.sp
                            )
                            Text(
                                text = " / ${attempt.totalMarks.toInt()}",
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(14.dp))

                        // Stats Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ScoreStatItem(label = "Attempted", value = "${attempt.attemptedCount}", color = MaterialTheme.colorScheme.onPrimaryContainer)
                            ScoreStatItem(label = "Correct", value = "${attempt.correctCount}", color = Color(0xFF10B981))
                            ScoreStatItem(label = "Incorrect", value = "${attempt.incorrectCount}", color = MaterialTheme.colorScheme.error)
                            val accuracy = if (attempt.attemptedCount > 0) (attempt.correctCount.toDouble() / attempt.attemptedCount) * 100.0 else 0.0
                            ScoreStatItem(label = "Accuracy", value = "${accuracy.toInt()}%", color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }
            }

            // --- AIR & PERCENTILE ESTIMATOR CARD ---
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = Color(0xFFF59E0B))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "All-India Rank (AIR) & Percentile Projection",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val (airRange, percentile, categoryStatus) = calculateRankEstimate(attempt.score)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Projected GATE 2027 Rank", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(airRange, fontSize = 18.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Percentile Bracket", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(percentile, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = categoryStatus,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }

            // --- TIME ANALYSIS ---
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Time Management & Pace",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val mins = attempt.totalTimeSeconds / 60
                        val secs = attempt.totalTimeSeconds % 60
                        val avgTimePerQ = if (attempt.attemptedCount > 0) attempt.totalTimeSeconds / attempt.attemptedCount else 0
                        Text(
                            text = "Total Exam Time: ${mins}m ${secs}s • Avg Time / Question: ${avgTimePerQ}s",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // --- QUESTION BREAKDOWN & MARKING ---
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Marking & Penalty Breakdown",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Positive Marks Earned", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                            Text("+${String.format(Locale.US, "%.2f", attempt.score + (attempt.incorrectCount * 0.33))}", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Negative Penalty Incurred", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                            Text("-${String.format(Locale.US, "%.2f", (attempt.incorrectCount * 0.33))}", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Net Assessment Score", color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("${String.format(Locale.US, "%.2f", attempt.score)} / ${attempt.totalMarks.toInt()}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black, fontSize = 13.sp)
                        }
                    }
                }
            }

            // --- RECOVERY ACTIONS ---
            item {
                Button(
                    onClick = { viewModel.navigateTo(ScreenDestination.MistakeBook) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("review_mistakes_btn")
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Review Mistake Book (${attempt.incorrectCount} Errors)", fontWeight = FontWeight.Bold)
                }
            }

            item {
                Button(
                    onClick = { viewModel.navigateTo(ScreenDestination.MockTests) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("take_another_mock_btn")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Take Another Mock Test", fontWeight = FontWeight.Bold)
                }
            }

            item {
                OutlinedButton(
                    onClick = { viewModel.navigateTo(ScreenDestination.Dashboard) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back to Dashboard", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun estimateAIR(score: Double): String {
    return when {
        score >= 75.0 -> "AIR 1 - 100"
        score >= 60.0 -> "AIR 100 - 500"
        score >= 48.0 -> "AIR 500 - 1,500"
        score >= 35.0 -> "AIR 1,500 - 4,000"
        score >= 27.5 -> "AIR 4,000 - 9,000"
        else -> "AIR > 10,000"
    }
}

private fun calculateRankEstimate(score: Double): Triple<String, String, String> {
    return when {
        score >= 75.0 -> Triple("AIR 1 – 100", "99.9+ %ile", "🎯 Outstanding! Tier-1 IIT Direct Admission / PSU Shortlist bracket.")
        score >= 60.0 -> Triple("AIR 100 – 500", "99.5 %ile", "🚀 Excellent! High probability for top IITs M.Tech (CSE/AI).")
        score >= 48.0 -> Triple("AIR 500 – 1,500", "98.0 %ile", "📈 Very Good! Competitive for Top NITs and New IITs.")
        score >= 35.0 -> Triple("AIR 1,500 – 4,000", "94.5 %ile", "💡 Solid Foundation. Eliminate negative marks in NAT to cross 55+.")
        score >= 27.5 -> Triple("AIR 4,000 – 9,000", "88.0 %ile", "⚠️ Qualified. Needs rigorous focus on Core CS (OS, DBMS, Algo).")
        else -> Triple("AIR > 10,000", "< 80.0 %ile", "Needs foundational topic revision and PYQ mastery.")
    }
}

@Composable
fun ScoreStatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Black, color = color, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
    }
}
