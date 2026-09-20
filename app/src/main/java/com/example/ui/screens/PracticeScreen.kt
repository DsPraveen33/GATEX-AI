package com.example.ui.screens

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.*
import com.example.ui.MainViewModel
import com.example.ui.ScreenDestination
import com.example.ui.theme.*
import org.json.JSONArray

@Composable
fun PracticeScreen(viewModel: MainViewModel) {
    val allQuestions by viewModel.allQuestions.collectAsStateWithLifecycle()
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()

    var selectedSubjectFilter by remember { mutableStateOf<String?>(null) }
    var selectedTypeFilter by remember { mutableStateOf<QuestionType?>(null) }

    val filteredQuestions = remember(allQuestions, selectedSubjectFilter, selectedTypeFilter) {
        allQuestions.filter { q ->
            (selectedSubjectFilter == null || q.subjectId == selectedSubjectFilter) &&
            (selectedTypeFilter == null || q.questionType == selectedTypeFilter)
        }
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    val currentQuestion = filteredQuestions.getOrNull(currentIndex)

    val userAns by viewModel.userPracticeAnswer.collectAsStateWithLifecycle()
    val isSubmitted by viewModel.isAnswerSubmitted.collectAsStateWithLifecycle()
    val confidence by viewModel.practiceConfidence.collectAsStateWithLifecycle()

    var selectedMistakeCategory by remember { mutableStateOf(MistakeCategory.CONCEPTUAL) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeep)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // --- HEADER & VIRTUAL CALCULATOR LAUNCHER ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Adaptive Practice Engine",
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "MCQ • MSQ • NAT with Immediate Trap Analytics",
                        color = TextSecondaryDark,
                        fontSize = 12.sp
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilledTonalButton(
                        onClick = { viewModel.navigateTo(ScreenDestination.QuestionStudio) },
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF6366F1).copy(alpha = 0.25f), contentColor = Color(0xFF818CF8)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.EditNote, contentDescription = "Studio", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Studio", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.openCalculator() },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyCardHighlight, contentColor = ElectricCyan),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = "Calc", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("NAT Calc", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- SUBJECT FILTERS ---
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedSubjectFilter == null,
                        onClick = { selectedSubjectFilter = null },
                        label = { Text("All Subjects") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricCyan,
                            selectedLabelColor = NavyDeep
                        )
                    )
                }
                items(subjects) { subj ->
                    FilterChip(
                        selected = selectedSubjectFilter == subj.id,
                        onClick = { selectedSubjectFilter = subj.id },
                        label = { Text(subj.code) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricCyan,
                            selectedLabelColor = NavyDeep
                        )
                    )
                }
            }
        }

        if (currentQuestion == null) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No questions found for the selected filter.",
                            color = TextSecondaryDark,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            // --- QUESTION CARD ---
            item {
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = ElectricBlue.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = currentQuestion.questionType.name,
                                        color = ElectricCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = BrightAmber.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "${currentQuestion.marks} Mark${if (currentQuestion.marks > 1) "s" else ""}",
                                        color = BrightAmber,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (currentQuestion.year > 0) {
                                    Text(
                                        text = "GATE ${currentQuestion.year}",
                                        color = NeonMint,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.toggleBookmark(currentQuestion.id, currentQuestion.isBookmarked) }
                                ) {
                                    Icon(
                                        imageVector = if (currentQuestion.isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                        contentDescription = "Bookmark",
                                        tint = if (currentQuestion.isBookmarked) BrightAmber else TextSecondaryDark
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Question ${currentIndex + 1} of ${filteredQuestions.size}",
                            color = TextSecondaryDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = currentQuestion.questionText,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Options for MCQ / MSQ
                        if (currentQuestion.questionType == QuestionType.NAT) {
                            OutlinedTextField(
                                value = userAns,
                                onValueChange = { viewModel.setPracticeAnswer(it) },
                                label = { Text("Enter Numerical Answer") },
                                enabled = !isSubmitted,
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElectricCyan,
                                    unfocusedBorderColor = Color(0xFF334155),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            val optionsList = remember(currentQuestion.optionsJson) {
                                try {
                                    val arr = JSONArray(currentQuestion.optionsJson)
                                    (0 until arr.length()).map { arr.getString(it) }
                                } catch (e: Exception) {
                                    emptyList()
                                }
                            }

                            optionsList.forEach { opt ->
                                val optKey = opt.take(1).uppercase()
                                val isSelected = if (currentQuestion.questionType == QuestionType.MSQ) {
                                    userAns.split(",").map { it.trim().uppercase() }.contains(optKey)
                                } else {
                                    userAns.startsWith(optKey, ignoreCase = true)
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) ElectricBlue.copy(alpha = 0.25f) else NavySurface)
                                        .border(
                                            1.dp,
                                            if (isSelected) ElectricCyan else Color.Transparent,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable(enabled = !isSubmitted) {
                                            if (currentQuestion.questionType == QuestionType.MSQ) {
                                                val currentSet = userAns.split(",").map { it.trim().uppercase() }.filter { it.isNotBlank() }.toMutableSet()
                                                if (currentSet.contains(optKey)) currentSet.remove(optKey) else currentSet.add(optKey)
                                                viewModel.setPracticeAnswer(currentSet.sorted().joinToString(","))
                                            } else {
                                                viewModel.setPracticeAnswer(optKey)
                                            }
                                        }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) ElectricCyan else NavyDark),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = optKey,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) NavyDeep else Color.White,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = opt,
                                        color = TextPrimaryDark,
                                        fontSize = 13.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Confidence Selector (1-5) before submitting
                        if (!isSubmitted) {
                            Text(
                                text = "Your Confidence Level:",
                                color = TextSecondaryDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val labels = listOf("1 (Guess)", "2 (Low)", "3 (Med)", "4 (High)", "5 (Certain)")
                                labels.forEachIndexed { idx, label ->
                                    val level = idx + 1
                                    Button(
                                        onClick = { viewModel.setPracticeConfidence(level) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (confidence == level) BrightAmber else NavySurface,
                                            contentColor = if (confidence == level) NavyDeep else Color.White
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text(text = "$level", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    viewModel.submitPracticeAnswer(
                                        question = currentQuestion,
                                        timeSpent = 45,
                                        mistakeCategory = selectedMistakeCategory
                                    )
                                },
                                enabled = userAns.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = NavyDeep),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().testTag("submit_practice_answer_btn")
                            ) {
                                Text("Check Answer & Trap Analysis", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // --- EXPLANATION & TRAP BREAKDOWN ON SUBMIT ---
            if (isSubmitted) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = NavyCard),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, NeonMint.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = NeonMint)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Correct Answer: ${currentQuestion.correctAnswer}",
                                    fontWeight = FontWeight.Bold,
                                    color = NeonMint,
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Step-by-Step Mathematical Explanation:",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentQuestion.explanation,
                                color = TextPrimaryDark,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )

                            if (currentQuestion.commonTrap.isNotBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = CrimsonAlert.copy(alpha = 0.15f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier.padding(10.dp)) {
                                        Text(text = "⚠️ ", fontSize = 14.sp)
                                        Column {
                                            Text(text = "GATE Exam Trap:", fontWeight = FontWeight.Bold, color = CrimsonAlert, fontSize = 12.sp)
                                            Text(text = currentQuestion.commonTrap, color = TextPrimaryDark, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.sendTutorMessage("Can you explain why the answer to this question '${currentQuestion.questionText}' is ${currentQuestion.correctAnswer}? I answered '${userAns}'.")
                                        viewModel.navigateTo(ScreenDestination.AITutor)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricCyan)
                                ) {
                                    Text("Ask AI Tutor", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        if (currentIndex < filteredQuestions.size - 1) {
                                            currentIndex++
                                            viewModel.selectQuestion(filteredQuestions[currentIndex].id)
                                        } else {
                                            currentIndex = 0
                                            viewModel.selectQuestion(filteredQuestions.first().id)
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = NavyDeep)
                                ) {
                                    Text("Next Question", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
