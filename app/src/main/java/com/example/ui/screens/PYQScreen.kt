package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.QuestionEntity
import com.example.data.model.QuestionSource
import com.example.ui.MainViewModel
import com.example.ui.ScreenDestination
import com.example.ui.theme.*

@Composable
fun PYQScreen(viewModel: MainViewModel) {
    val officialPYQs by viewModel.officialPYQs.collectAsStateWithLifecycle()
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()

    var selectedYearFilter by remember { mutableStateOf<Int?>(null) }
    var selectedSubjectFilter by remember { mutableStateOf<String?>(null) }
    var expandedQuestionId by remember { mutableStateOf<Long?>(null) }

    val filteredList = remember(officialPYQs, selectedYearFilter, selectedSubjectFilter) {
        officialPYQs.filter { q ->
            (selectedYearFilter == null || q.year == selectedYearFilter) &&
            (selectedSubjectFilter == null || q.subjectId == selectedSubjectFilter)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeep)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            Text(
                text = "GATE Official PYQ Archive",
                fontWeight = FontWeight.Black,
                color = Color.White,
                fontSize = 20.sp
            )
            Text(
                text = "Verified Past Year Questions (2015-2024) • Never Fabricated",
                color = TextSecondaryDark,
                fontSize = 12.sp
            )
        }

        // --- YEAR FILTER CHIPS ---
        item {
            val years = listOf(2024, 2023, 2022, 2021)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedYearFilter == null,
                        onClick = { selectedYearFilter = null },
                        label = { Text("All Years") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricCyan,
                            selectedLabelColor = NavyDeep
                        )
                    )
                }
                items(years) { yr ->
                    FilterChip(
                        selected = selectedYearFilter == yr,
                        onClick = { selectedYearFilter = if (selectedYearFilter == yr) null else yr },
                        label = { Text("GATE $yr") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricCyan,
                            selectedLabelColor = NavyDeep
                        )
                    )
                }
            }
        }

        // --- SUBJECT FILTER CHIPS ---
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
                        onClick = { selectedSubjectFilter = if (selectedSubjectFilter == subj.id) null else subj.id },
                        label = { Text(subj.code) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricCyan,
                            selectedLabelColor = NavyDeep
                        )
                    )
                }
            }
        }

        items(filteredList) { question ->
            val isExpanded = expandedQuestionId == question.id

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = NeonMint.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "GATE ${question.year}",
                                    color = NeonMint,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = ElectricBlue.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "${question.subjectId.uppercase()} • ${question.questionType.name}",
                                    color = ElectricCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.toggleBookmark(question.id, question.isBookmarked) }
                        ) {
                            Icon(
                                imageVector = if (question.isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (question.isBookmarked) BrightAmber else TextSecondaryDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = question.questionText,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                expandedQuestionId = if (isExpanded) null else question.id
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = ElectricCyan)
                        ) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isExpanded) "Hide Solution" else "View Official Solution", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                viewModel.selectQuestion(question.id)
                                viewModel.navigateTo(ScreenDestination.Practice)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = NavyDeep),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Solve in Practice", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = Color(0xFF334155))
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Answer: ${question.correctAnswer}",
                            fontWeight = FontWeight.Bold,
                            color = NeonMint,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = question.explanation,
                            color = TextPrimaryDark,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )

                        if (question.commonTrap.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = CrimsonAlert.copy(alpha = 0.15f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(modifier = Modifier.padding(8.dp)) {
                                    Text(text = "⚠️ ", fontSize = 12.sp)
                                    Text(text = "Trap: ${question.commonTrap}", color = TextPrimaryDark, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
