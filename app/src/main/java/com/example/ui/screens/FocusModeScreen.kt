package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.QuestionEntity
import com.example.ui.MainViewModel
import com.example.ui.ScreenDestination
import com.example.ui.components.AIOrbState
import com.example.ui.components.AIStudyOrb
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.util.Locale

/**
 * Focus Mode: Distraction-free Study Runner
 * Shows only the core timer, focused questions, instant feedback, and ambient focus ring.
 */
@Composable
fun FocusModeScreen(
    topicName: String = "Operating Systems — Deadlocks",
    durationMinutes: Int = 25,
    viewModel: MainViewModel,
    onExit: () -> Unit
) {
    val allQuestions by viewModel.allQuestions.collectAsStateWithLifecycle()

    val focusQuestions = remember(allQuestions) {
        if (allQuestions.isNotEmpty()) allQuestions.take(5) else emptyList()
    }

    var currentQuestionIdx by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }
    var correctCount by remember { mutableIntStateOf(0) }

    // Timer State
    var secondsRemaining by remember { mutableIntStateOf(durationMinutes * 60) }
    var isPaused by remember { mutableStateOf(false) }
    var sessionCompleted by remember { mutableStateOf(false) }

    LaunchedEffect(isPaused, secondsRemaining) {
        if (!isPaused && secondsRemaining > 0 && !sessionCompleted) {
            delay(1000L)
            secondsRemaining--
            if (secondsRemaining <= 0) {
                sessionCompleted = true
            }
        }
    }

    val currentQ = focusQuestions.getOrNull(currentQuestionIdx)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeep)
            .padding(18.dp)
            .testTag("focus_mode_screen")
    ) {
        if (!sessionCompleted) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Header with Ambient Focus Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onExit,
                        modifier = Modifier.testTag("exit_focus_mode_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Exit Focus", tint = TextSecondaryDark)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AIStudyOrb(state = if (isPaused) AIOrbState.Idle else AIOrbState.Speaking, size = 20.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "FOCUS MODE ACTIVE",
                                fontWeight = FontWeight.Black,
                                color = NeonMint,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = topicName,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }

                    IconButton(
                        onClick = { isPaused = !isPaused },
                        modifier = Modifier.testTag("pause_resume_focus_btn")
                    ) {
                        Icon(
                            imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (isPaused) "Resume" else "Pause",
                            tint = ElectricCyan
                        )
                    }
                }

                // Center Distraction-Free Card & Timer
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Big Modern Minimalist Timer
                    val mins = secondsRemaining / 60
                    val secs = secondsRemaining % 60
                    val timeStr = String.format(Locale.getDefault(), "%02d:%02d", mins, secs)

                    Text(
                        text = timeStr,
                        fontWeight = FontWeight.Black,
                        fontSize = 44.sp,
                        color = if (secondsRemaining < 180) CrimsonAlert else ElectricCyan,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isPaused) "PAUSED" else "DEEP CONCENTRATION DRILL",
                        color = if (isPaused) BrightAmber else TextSecondaryDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Question Card
                    if (currentQ != null) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = NavyCard),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, ElectricCyan.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Question ${currentQuestionIdx + 1} of ${focusQuestions.size}",
                                        color = ElectricCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = currentQ.questionType.name,
                                        color = TextSecondaryDark,
                                        fontSize = 11.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = currentQ.questionText,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    lineHeight = 19.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Options
                                val options = remember(currentQ) {
                                    try {
                                        val arr = org.json.JSONArray(currentQ.optionsJson)
                                        (0 until arr.length()).map { arr.getString(it) }
                                    } catch (e: Exception) {
                                        listOf("Option A", "Option B", "Option C", "Option D")
                                    }
                                }

                                options.forEachIndexed { optIdx, optText ->
                                    val optLetter = ('A' + optIdx).toString()
                                    val isChosen = selectedOption == optLetter
                                    val isCorrect = optLetter.equals(currentQ.correctAnswer, ignoreCase = true)

                                    val optionBg = when {
                                        !isSubmitted && isChosen -> ElectricCyan.copy(alpha = 0.2f)
                                        isSubmitted && isCorrect -> NeonMint.copy(alpha = 0.25f)
                                        isSubmitted && isChosen && !isCorrect -> CrimsonAlert.copy(alpha = 0.25f)
                                        else -> NavySurface
                                    }

                                    val optionBorder = when {
                                        !isSubmitted && isChosen -> ElectricCyan
                                        isSubmitted && isCorrect -> NeonMint
                                        isSubmitted && isChosen && !isCorrect -> CrimsonAlert
                                        else -> Color.Transparent
                                    }

                                    Surface(
                                        color = optionBg,
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .border(1.dp, optionBorder, RoundedCornerShape(10.dp))
                                            .clickable(enabled = !isSubmitted) {
                                                selectedOption = optLetter
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "$optLetter.",
                                                fontWeight = FontWeight.Bold,
                                                color = if (isChosen) ElectricCyan else TextSecondaryDark,
                                                fontSize = 13.sp
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = optText,
                                                color = Color.White,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (!isSubmitted) {
                        Button(
                            onClick = {
                                isSubmitted = true
                                val isCorrect = selectedOption.equals(currentQ?.correctAnswer, ignoreCase = true)
                                if (isCorrect) correctCount++
                            },
                            enabled = selectedOption != null,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonMint, contentColor = NavyDeep),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("focus_submit_btn")
                        ) {
                            Text("Check Answer", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = {
                                if (currentQuestionIdx < focusQuestions.size - 1) {
                                    currentQuestionIdx++
                                    selectedOption = null
                                    isSubmitted = false
                                } else {
                                    sessionCompleted = true
                                    val spentHours = (durationMinutes * 60 - secondsRemaining).toDouble() / 3600.0
                                    viewModel.logStudyHours(
                                        hours = if (spentHours > 0.1) spentHours else 0.4,
                                        questionsSolved = focusQuestions.size,
                                        subject = topicName,
                                        notes = "Completed Focus Mode study session ($correctCount/${focusQuestions.size} correct)."
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = NavyDeep),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("focus_next_btn")
                        ) {
                            Text(
                                text = if (currentQuestionIdx < focusQuestions.size - 1) "Next Question" else "Complete Focus Session",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            // Session Complete Card
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AIStudyOrb(state = AIOrbState.Success, size = 64.dp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "FOCUS SESSION COMPLETE!",
                    fontWeight = FontWeight.Black,
                    color = NeonMint,
                    fontSize = 20.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Logged $durationMinutes minutes of deep work to your GATE 2027 daily tracker.",
                    color = TextSecondaryDark,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Score", color = TextSecondaryDark, fontSize = 11.sp)
                            Text("$correctCount / ${focusQuestions.size}", fontWeight = FontWeight.Bold, color = ElectricCyan, fontSize = 18.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Focus Time", color = TextSecondaryDark, fontSize = 11.sp)
                            Text("${durationMinutes}m", fontWeight = FontWeight.Bold, color = NeonMint, fontSize = 18.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Accuracy", color = TextSecondaryDark, fontSize = 11.sp)
                            val acc = if (focusQuestions.isNotEmpty()) (correctCount * 100) / focusQuestions.size else 100
                            Text("$acc%", fontWeight = FontWeight.Bold, color = BrightAmber, fontSize = 18.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onExit,
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = NavyDeep),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Return to Dashboard", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
