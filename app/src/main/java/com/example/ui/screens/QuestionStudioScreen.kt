package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.QuestionEntity
import com.example.data.model.QuestionSource
import com.example.data.model.QuestionType
import com.example.ui.MainViewModel
import com.example.ui.theme.*
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionStudioScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val allQuestions by viewModel.allQuestions.collectAsStateWithLifecycle()
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("ALL") }
    var selectedType by remember { mutableStateOf<QuestionType?>(null) }

    var editingQuestion by remember { mutableStateOf<QuestionEntity?>(null) }
    var isAddingNewQuestion by remember { mutableStateOf(false) }
    var questionToDelete by remember { mutableStateOf<QuestionEntity?>(null) }

    val filteredQuestions = remember(allQuestions, searchQuery, selectedSubject, selectedType) {
        allQuestions.filter { q ->
            val matchQuery = searchQuery.isBlank() ||
                    q.questionText.contains(searchQuery, ignoreCase = true) ||
                    q.explanation.contains(searchQuery, ignoreCase = true) ||
                    q.subjectId.contains(searchQuery, ignoreCase = true)
            val matchSubject = selectedSubject == "ALL" || q.subjectId.equals(selectedSubject, ignoreCase = true)
            val matchType = selectedType == null || q.questionType == selectedType
            matchQuery && matchSubject && matchType
        }
    }

    Scaffold(
        containerColor = NavyDeep,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isAddingNewQuestion = true },
                containerColor = ElectricCyan,
                contentColor = NavyDeep,
                modifier = Modifier.testTag("add_custom_question_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Question")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Question", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 90.dp)
        ) {
            // --- HEADER ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Question Studio & Bank",
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Download daily fresh questions • Edit questions & answers • Custom bank",
                            color = TextSecondaryDark,
                            fontSize = 11.5.sp
                        )
                    }

                    IconButton(
                        onClick = {
                            viewModel.triggerDailyQuizTestNotification()
                            Toast.makeText(context, "🔔 Push notification sent!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = "Test Notification", tint = BrightAmber)
                    }
                }
            }

            // --- 1. DAILY DOWNLOAD & SYNC CARD ---
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF6366F1).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF6366F1).copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color(0xFF818CF8), modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Daily Fresh Question Sync", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Pack: ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())}", color = ElectricCyan, fontSize = 11.sp)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = NeonMint.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "${allQuestions.size} Total Stored",
                                    color = NeonMint,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Download today's curated pack of 15+ multi-subject high-yield questions (OS, DBMS, CN, DSA, TOC, Math, Aptitude) directly to your local offline database.",
                            color = TextSecondaryDark,
                            fontSize = 11.5.sp,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.downloadAndSyncDailyQuestions { count ->
                                        Toast.makeText(context, "✅ Downloaded & Synced $count fresh questions into your offline database!", Toast.LENGTH_LONG).show()
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("download_daily_questions_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Download Today's Pack", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.scheduleDailyFreeQuizNotification(hour = 8, minute = 0, isEnabled = true)
                                    Toast.makeText(context, "⏰ Daily Free Quiz reminder set for 08:00 AM everyday!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = BrightAmber)
                            ) {
                                Icon(Icons.Default.Alarm, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Auto-Daily 8AM", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // --- 2. SEARCH & FILTER BAR ---
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by concept, subject, formula...", color = TextSecondaryDark, fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = ElectricCyan) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondaryDark)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_questions_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // Subject Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedSubject == "ALL",
                            onClick = { selectedSubject = "ALL" },
                            label = { Text("All (${allQuestions.size})", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricCyan,
                                selectedLabelColor = NavyDeep
                            )
                        )
                    }
                    items(subjects) { sub ->
                        val count = allQuestions.count { it.subjectId.equals(sub.id, ignoreCase = true) }
                        FilterChip(
                            selected = selectedSubject.equals(sub.id, ignoreCase = true),
                            onClick = { selectedSubject = sub.id },
                            label = { Text("${sub.code} ($count)", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricCyan,
                                selectedLabelColor = NavyDeep
                            )
                        )
                    }
                }
            }

            // Question Type Chips
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = selectedType == null,
                        onClick = { selectedType = null },
                        label = { Text("All Types", fontSize = 11.sp) }
                    )
                    QuestionType.values().forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = if (selectedType == type) null else type },
                            label = { Text(type.name, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (type == QuestionType.MCQ) ElectricCyan else if (type == QuestionType.MSQ) RoyalPurple else BrightAmber,
                                selectedLabelColor = NavyDeep
                            )
                        )
                    }
                }
            }

            // --- 3. QUESTIONS LIST WITH EDIT & DELETE ACTIONS ---
            items(filteredQuestions) { q ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { editingQuestion = q }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = when (q.questionType) {
                                        QuestionType.MCQ -> ElectricCyan.copy(alpha = 0.2f)
                                        QuestionType.MSQ -> RoyalPurple.copy(alpha = 0.2f)
                                        QuestionType.NAT -> BrightAmber.copy(alpha = 0.2f)
                                    }
                                ) {
                                    Text(
                                        text = "${q.questionType.name} • ${q.marks}M",
                                        color = when (q.questionType) {
                                            QuestionType.MCQ -> ElectricCyan
                                            QuestionType.MSQ -> RoyalPurple
                                            QuestionType.NAT -> BrightAmber
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = q.subjectId.uppercase(),
                                    color = TextSecondaryDark,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row {
                                IconButton(
                                    onClick = { editingQuestion = q },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = ElectricCyan, modifier = Modifier.size(16.dp))
                                }
                                IconButton(
                                    onClick = { questionToDelete = q },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CrimsonAlert, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = q.questionText,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Correct Answer Badge
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = NeonMint.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonMint, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Correct Answer: ${q.correctAnswer}",
                                    color = NeonMint,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // --- EDIT / ADD QUESTION DIALOG ---
    if (editingQuestion != null || isAddingNewQuestion) {
        val targetQ = editingQuestion ?: QuestionEntity(
            id = 0,
            subjectId = if (selectedSubject != "ALL") selectedSubject else "os",
            topicId = "custom_topic",
            questionText = "",
            optionsJson = "[\"A. Option 1\", \"B. Option 2\", \"C. Option 3\", \"D. Option 4\"]",
            correctAnswer = "A",
            explanation = "",
            questionType = QuestionType.MCQ,
            marks = 2,
            source = QuestionSource.USER_CREATED,
            year = 2027
        )

        var text by remember(targetQ) { mutableStateOf(targetQ.questionText) }
        var type by remember(targetQ) { mutableStateOf(targetQ.questionType) }
        var subjectId by remember(targetQ) { mutableStateOf(targetQ.subjectId) }
        var marks by remember(targetQ) { mutableIntStateOf(targetQ.marks) }
        var correctAnswer by remember(targetQ) { mutableStateOf(targetQ.correctAnswer) }
        var explanation by remember(targetQ) { mutableStateOf(targetQ.explanation) }
        var optA by remember(targetQ) {
            val list = parseOptions(targetQ.optionsJson)
            mutableStateOf(list.getOrNull(0)?.removePrefix("A. ") ?: "")
        }
        var optB by remember(targetQ) {
            val list = parseOptions(targetQ.optionsJson)
            mutableStateOf(list.getOrNull(1)?.removePrefix("B. ") ?: "")
        }
        var optC by remember(targetQ) {
            val list = parseOptions(targetQ.optionsJson)
            mutableStateOf(list.getOrNull(2)?.removePrefix("C. ") ?: "")
        }
        var optD by remember(targetQ) {
            val list = parseOptions(targetQ.optionsJson)
            mutableStateOf(list.getOrNull(3)?.removePrefix("D. ") ?: "")
        }

        AlertDialog(
            onDismissRequest = {
                editingQuestion = null
                isAddingNewQuestion = false
            },
            title = {
                Text(
                    text = if (editingQuestion != null) "Edit Question & Answer" else "Add New Custom Question",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text("Question Text:", color = TextSecondaryDark, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = ElectricCyan,
                                unfocusedBorderColor = Color(0xFF334155)
                            )
                        )
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Type:", color = TextSecondaryDark, fontSize = 11.sp)
                                Row {
                                    QuestionType.values().forEach { t ->
                                        FilterChip(
                                            selected = type == t,
                                            onClick = { type = t },
                                            label = { Text(t.name, fontSize = 10.sp) }
                                        )
                                    }
                                }
                            }
                            Column(modifier = Modifier.weight(0.5f)) {
                                Text("Marks:", color = TextSecondaryDark, fontSize = 11.sp)
                                Row {
                                    listOf(1, 2).forEach { m ->
                                        FilterChip(
                                            selected = marks == m,
                                            onClick = { marks = m },
                                            label = { Text("${m}M", fontSize = 10.sp) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (type != QuestionType.NAT) {
                        item {
                            Text("Options:", color = TextSecondaryDark, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            OutlinedTextField(
                                value = optA,
                                onValueChange = { optA = it },
                                label = { Text("Option A") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = optB,
                                onValueChange = { optB = it },
                                label = { Text("Option B") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = optC,
                                onValueChange = { optC = it },
                                label = { Text("Option C") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = optD,
                                onValueChange = { optD = it },
                                label = { Text("Option D") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    item {
                        Text("Correct Answer (e.g. 'A' for MCQ, 'A,C' for MSQ, or '42' or '10.5:12.5' for NAT):", color = BrightAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = correctAnswer,
                            onValueChange = { correctAnswer = it },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = NeonMint,
                                unfocusedTextColor = NeonMint,
                                focusedBorderColor = NeonMint,
                                unfocusedBorderColor = Color(0xFF334155)
                            )
                        )
                    }

                    item {
                        Text("Explanation & Solution Steps:", color = TextSecondaryDark, fontSize = 11.sp)
                        OutlinedTextField(
                            value = explanation,
                            onValueChange = { explanation = it },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = ElectricCyan,
                                unfocusedBorderColor = Color(0xFF334155)
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (text.isBlank() || correctAnswer.isBlank()) {
                            Toast.makeText(context, "Please provide question text and correct answer", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val optionsJson = if (type == QuestionType.NAT) {
                            "[]"
                        } else {
                            JSONArray(listOf("A. $optA", "B. $optB", "C. $optC", "D. $optD")).toString()
                        }

                        val toSave = targetQ.copy(
                            questionText = text,
                            questionType = type,
                            subjectId = subjectId,
                            marks = marks,
                            optionsJson = optionsJson,
                            correctAnswer = correctAnswer.trim(),
                            explanation = explanation
                        )

                        viewModel.saveOrUpdateQuestion(toSave)
                        Toast.makeText(context, "✅ Question & Answer saved successfully!", Toast.LENGTH_SHORT).show()
                        editingQuestion = null
                        isAddingNewQuestion = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonMint, contentColor = NavyDeep)
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    editingQuestion = null
                    isAddingNewQuestion = false
                }) {
                    Text("Cancel", color = TextSecondaryDark)
                }
            },
            containerColor = NavyDark
        )
    }

    // --- DELETE CONFIRMATION DIALOG ---
    if (questionToDelete != null) {
        AlertDialog(
            onDismissRequest = { questionToDelete = null },
            title = { Text("Delete Question?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove this question from your local question bank?", color = TextSecondaryDark) },
            confirmButton = {
                Button(
                    onClick = {
                        val q = questionToDelete
                        if (q != null) {
                            viewModel.deleteQuestion(q.id)
                            Toast.makeText(context, "Question deleted", Toast.LENGTH_SHORT).show()
                        }
                        questionToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert, contentColor = Color.White)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { questionToDelete = null }) {
                    Text("Cancel", color = TextSecondaryDark)
                }
            },
            containerColor = NavyDark
        )
    }
}

private fun parseOptions(jsonStr: String): List<String> {
    return try {
        val arr = JSONArray(jsonStr)
        (0 until arr.length()).map { arr.getString(it) }
    } catch (e: Exception) {
        emptyList()
    }
}
