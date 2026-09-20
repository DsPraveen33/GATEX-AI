package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.ui.MainViewModel
import com.example.ui.ScreenDestination
import com.example.ui.theme.*

@Composable
fun TopicDetailScreen(topicId: String, viewModel: MainViewModel) {
    val topics by viewModel.topics.collectAsStateWithLifecycle()
    val topic = topics.find { it.id == topicId } ?: topics.firstOrNull()

    if (topic == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ElectricCyan)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeep)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // --- TOP NAVIGATION ---
        item {
            IconButton(onClick = { viewModel.navigateTo(ScreenDestination.Syllabus) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
        }

        // --- TOPIC HEADER CARD ---
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ElectricCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
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
                                text = topic.subjectId.uppercase(),
                                color = ElectricCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CrimsonAlert.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "${topic.importance} PRIORITY",
                                color = CrimsonAlert,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = topic.name,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Estimated Time: ${topic.estimatedHours} Hours",
                            color = BrightAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Mastery: ${topic.masteryLevel.toInt()}%",
                            color = NeonMint,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { (topic.masteryLevel / 100.0).toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = NeonMint,
                        trackColor = NavyDark
                    )
                }
            }
        }

        // --- SUBTOPICS BREAKDOWN ---
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Key Subtopics in Official GATE CSE",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    topic.subtopicsList.split(",").forEach { sub ->
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = sub.trim(), color = TextPrimaryDark, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // --- ACTION SHORTCUTS ---
        item {
            Text(
                text = "Topic Workspace Actions",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 15.sp
            )
        }

        item {
            ActionTile(
                title = "Ask AI Tutor to Explain",
                subtitle = "Intuition, proofs, theorems, and GATE traps",
                icon = Icons.Default.Psychology,
                iconColor = RoyalPurple,
                onClick = {
                    viewModel.sendTutorMessage("Please explain ${topic.name} in GATE CSE with intuition, key formulas, step-by-step example, and common traps.")
                    viewModel.navigateTo(ScreenDestination.AITutor)
                }
            )
        }

        item {
            ActionTile(
                title = "Solve Topic PYQs",
                subtitle = "Historical GATE questions (2015-2024)",
                icon = Icons.Default.HistoryEdu,
                iconColor = BrightAmber,
                onClick = { viewModel.navigateTo(ScreenDestination.PYQs) }
            )
        }

        item {
            ActionTile(
                title = "Launch Practice Quiz",
                subtitle = "Adaptive MCQs, MSQs, and NAT questions",
                icon = Icons.Default.Quiz,
                iconColor = NeonMint,
                onClick = { viewModel.navigateTo(ScreenDestination.Practice) }
            )
        }

        item {
            ActionTile(
                title = "Interactive Concept Lab",
                subtitle = "Visual simulation & algorithmic execution",
                icon = Icons.Default.Sensors,
                iconColor = ElectricCyan,
                onClick = { viewModel.navigateTo(ScreenDestination.ConceptLab) }
            )
        }
    }
}

@Composable
fun ActionTile(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                    Text(text = subtitle, color = TextSecondaryDark, fontSize = 11.sp)
                }
            }

            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondaryDark)
        }
    }
}
