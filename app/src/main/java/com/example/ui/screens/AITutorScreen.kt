package com.example.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.ui.ChatMessage
import com.example.ui.MainViewModel
import com.example.ui.components.GatexOrbLogo
import com.example.ui.components.TechnicalMessageRenderer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AITutorScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isLoading by viewModel.isAILoading.collectAsStateWithLifecycle()
    val currentRole by viewModel.tutorRole.collectAsStateWithLifecycle()
    val selectedModel by viewModel.selectedModel.collectAsStateWithLifecycle()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val clipboardManager = LocalClipboardManager.current
    var showAttachmentSheet by remember { mutableStateOf(false) }
    var showModelMenu by remember { mutableStateOf(false) }

    // Upload & Attachment States
    var attachedUri by remember { mutableStateOf<Uri?>(null) }
    var attachedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var attachedFileName by remember { mutableStateOf<String?>(null) }
    var attachedFileType by remember { mutableStateOf<String?>(null) } // "IMAGE", "CAMERA", "DOCUMENT", "CODE"
    var showCodeInputDialog by remember { mutableStateOf(false) }
    var rawCodeInput by remember { mutableStateOf("") }
    var activeZoomImageUri by remember { mutableStateOf<String?>(null) }

    // Photo Picker Launcher (Modern Zero-Permission Android Photo Picker)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            attachedUri = uri
            attachedBitmap = null
            attachedFileType = "IMAGE"
            attachedFileName = "Question_Screenshot_${(System.currentTimeMillis() % 10000)}.jpg"
            if (inputText.isBlank()) {
                inputText = "Please analyze this uploaded GATE question diagram step-by-step and solve it."
            }
        }
    }

    // Camera Capture Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            attachedBitmap = bitmap
            attachedUri = null
            attachedFileType = "CAMERA"
            attachedFileName = "Camera_Capture_${(System.currentTimeMillis() % 10000)}.jpg"
            if (inputText.isBlank()) {
                inputText = "Please analyze this question photo captured from my textbook/notes."
            }
        }
    }

    // Document / PDF Picker Launcher
    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            attachedUri = uri
            attachedBitmap = null
            attachedFileType = "DOCUMENT"
            attachedFileName = uri.lastPathSegment?.substringAfterLast('/') ?: "GATE_Study_Notes.pdf"
            if (inputText.isBlank()) {
                inputText = "Please analyze these study notes and extract the top 5 high-yield GATE CSE questions."
            }
        }
    }

    fun handleSend(customText: String = inputText) {
        val textToSend = customText.ifBlank {
            when (attachedFileType) {
                "IMAGE", "CAMERA" -> "Please analyze this uploaded GATE question diagram step-by-step with verified formulas."
                "DOCUMENT" -> "Please extract key formulas, high-yield concepts, and potential GATE trap questions from this document."
                "CODE" -> "Please analyze this code snippet for output, time/space complexity, and potential edge cases."
                else -> "Explain this topic for GATE CSE."
            }
        }

        val base64 = when {
            attachedBitmap != null -> {
                val stream = java.io.ByteArrayOutputStream()
                attachedBitmap!!.compress(Bitmap.CompressFormat.JPEG, 85, stream)
                android.util.Base64.encodeToString(stream.toByteArray(), android.util.Base64.NO_WRAP)
            }
            attachedUri != null -> {
                try {
                    context.contentResolver.openInputStream(attachedUri!!)?.use { stream ->
                        android.util.Base64.encodeToString(stream.readBytes(), android.util.Base64.NO_WRAP)
                    }
                } catch (e: Exception) {
                    null
                }
            }
            else -> null
        }

        viewModel.sendTutorMessage(
            userText = textToSend,
            imageUri = attachedUri?.toString(),
            base64Image = base64,
            attachmentName = attachedFileName,
            attachmentType = attachedFileType
        )

        inputText = ""
        attachedUri = null
        attachedBitmap = null
        attachedFileName = null
        attachedFileType = null
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- BRANDING & HEADER ---
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GatexOrbLogo(size = 30.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "GATEX Gemini Tutor",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Multi-Turn AI Study Agent • GATE CSE",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Gemini Model Selector Pill
                        Box {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .clickable { showModelMenu = true }
                                    .testTag("gemini_model_selector")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = when {
                                            selectedModel.contains("pro", ignoreCase = true) -> Icons.Default.Psychology
                                            selectedModel.contains("lite", ignoreCase = true) -> Icons.Default.Bolt
                                            selectedModel.contains("flash", ignoreCase = true) -> Icons.Default.AutoAwesome
                                            else -> Icons.Default.Tune
                                        },
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = when (selectedModel) {
                                            "gemini-3.1-pro-preview" -> "3.1 Pro"
                                            "gemini-3.5-flash" -> "3.5 Flash"
                                            "gemini-3.1-flash-lite-preview" -> "3.1 Flash-Lite"
                                            else -> "Auto Model"
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = showModelMenu,
                                onDismissRequest = { showModelMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text("Auto Router (Recommended)", fontWeight = FontWeight.Bold)
                                            Text("Routes by task complexity", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    },
                                    onClick = {
                                        viewModel.setSelectedModel("AUTO")
                                        showModelMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text("Gemini 3.5 Flash", fontWeight = FontWeight.Bold)
                                            Text("General tasks, balanced speed & reasoning", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    },
                                    onClick = {
                                        viewModel.setSelectedModel("gemini-3.5-flash")
                                        showModelMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text("Gemini 3.1 Pro Preview", fontWeight = FontWeight.Bold)
                                            Text("Complex STEM, derivations, proofs & code", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    },
                                    onClick = {
                                        viewModel.setSelectedModel("gemini-3.1-pro-preview")
                                        showModelMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text("Gemini 3.1 Flash Lite", fontWeight = FontWeight.Bold)
                                            Text("Ultra-fast responses & quick lookups", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    },
                                    onClick = {
                                        viewModel.setSelectedModel("gemini-3.1-flash-lite-preview")
                                        showModelMenu = false
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        IconButton(
                            onClick = { viewModel.clearChat() },
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("clear_chat_btn")
                        ) {
                            Icon(
                                Icons.Default.DeleteSweep,
                                contentDescription = "Clear Conversation",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }

                // Chatbot Role Switcher Strip
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                ) {
                    val roles = listOf(
                        "AIR 1 Exam Coach" to "🎯 AIR 1 Coach",
                        "IIT CSE Professor" to "🎓 IIT Professor",
                        "Socratic Tutor" to "💡 Socratic",
                        "Code & Algo Lab" to "💻 Code Lab",
                        "Rapid Revision" to "⚡ Fast Revision",
                        "Telugu Mentor" to "🌐 తెలుగు Mentor"
                    )
                    items(roles) { (roleKey, roleLabel) ->
                        val isSelected = currentRole.equals(roleKey, ignoreCase = true) || (roleKey == "AIR 1 Exam Coach" && currentRole == "Exam Coach") || (roleKey == "IIT CSE Professor" && currentRole == "Professor Mode")
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier.clickable {
                                viewModel.setTutorRole(roleKey)
                            }
                        ) {
                            Text(
                                text = roleLabel,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // --- CONVERSATION AREA ---
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp)
        ) {
            // New user guidance suggestions
            if (messages.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                GatexOrbLogo(size = 28.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "GATEX AI Tutor",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Your personal AI study agent for GATE 2027 CSE.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Suggested Prompts:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            val quickPrompts = listOf(
                                "Explain a GATE concept",
                                "Give me 10 practice questions",
                                "Analyze my weak topics",
                                "Create today's study plan",
                                "Solve a PYQ",
                                "Start a quiz"
                            )

                            quickPrompts.forEach { prompt ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                        .clickable {
                                            viewModel.sendTutorMessage(prompt)
                                        },
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.outlineVariant
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.ArrowForward,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = prompt,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            items(messages) { msg ->
                ChatBubble(
                    message = msg,
                    onActionClick = { action ->
                        viewModel.executeAgentAction(action)
                    },
                    onCopy = {
                        clipboardManager.setText(AnnotatedString(msg.text))
                    },
                    onImageClick = { imageUri ->
                        activeZoomImageUri = imageUri
                    }
                )
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "GATEX AI is formulating answer with GATE CSE context...",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // --- THUMB-ACCESSIBLE BOTTOM CONTROL DOCK ---
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shadowElevation = 10.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                // 1. Thumb Row 1: Pedagogy & Action Modes
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(bottom = 6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val modes = listOf(
                        "⚡ Direct Solve" to "Give me direct, step-by-step rigorous solution with formula and GATE trap.",
                        "🎯 Socratic Hint" to "Guide me with Socratic hints first rather than giving away the full answer.",
                        "📝 Exam Grader" to "Grade my answer according to official GATE CSE marking scheme and penalties.",
                        "💡 Concept Explainer" to "Explain this concept from intuitive fundamentals with visual/ASCII diagrams.",
                        "⚠️ Trap Audit" to "List the most dangerous tricks, edge cases, and pitfalls students fall into for this topic."
                    )
                    items(modes) { (modeTitle, promptPrefix) ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                            modifier = Modifier.clickable {
                                inputText = if (inputText.isBlank()) promptPrefix else "$promptPrefix: $inputText"
                            }
                        ) {
                            Text(
                                text = modeTitle,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // 2. Thumb Row 2: High-Yield Topic & PYQ Quick Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val promptShortcuts = listOf(
                        "⚡ Master Theorem" to "Explain Master Theorem for divide and conquer with all 3 cases and GATE examples.",
                        "🔑 3NF vs BCNF" to "How to quickly test whether a relation is in 3NF or BCNF with candidate keys?",
                        "🧮 AMAT & Cache" to "Explain Average Memory Access Time (AMAT) with multi-level cache formula.",
                        "🚥 Banker's Algorithm" to "Explain Banker's Algorithm safety condition with a step-by-step example.",
                        "🌐 Sliding Window" to "Derive efficiency formula for Go-Back-N and Selective Repeat protocols.",
                        "🎯 Practice 3 PYQs" to "Give me 3 high-yield GATE CSE PYQ questions with detailed solutions and trap warnings.",
                        "💻 Pointer Arithmetic" to "Explain tricky C pointer arithmetic and array decay with GATE PYQ examples."
                    )
                    items(promptShortcuts) { (chipLabel, fullPrompt) ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.clickable {
                                inputText = fullPrompt
                            }
                        ) {
                            Text(
                                text = chipLabel,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                // Active Attachment Preview Dock
                if (attachedUri != null || attachedBitmap != null || attachedFileName != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    if (attachedBitmap != null) {
                                        Image(
                                            bitmap = attachedBitmap!!.asImageBitmap(),
                                            contentDescription = "Camera Photo Thumbnail",
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else if (attachedUri != null) {
                                        AsyncImage(
                                            model = attachedUri,
                                            contentDescription = "Attachment Thumbnail",
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (attachedFileType == "CODE") Icons.Default.Code else Icons.Default.PictureAsPdf,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = attachedFileName ?: "Attached Problem",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = when (attachedFileType) {
                                                "CAMERA" -> "📸 Camera Photo (Ready for Gemini OCR & Solve)"
                                                "IMAGE" -> "🖼️ Screenshot / Diagram (Multimodal Vision)"
                                                "DOCUMENT" -> "📄 Study Document / PDF Notes"
                                                "CODE" -> "💻 C / Data Structure Code"
                                                else -> "📎 Attached File"
                                            },
                                            fontSize = 10.5.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        attachedUri = null
                                        attachedBitmap = null
                                        attachedFileName = null
                                        attachedFileType = null
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Remove Attachment",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Quick Prompt Shortcuts for this attachment
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val attachPrompts = listOf(
                                    "🔍 Step-by-Step Solve" to "Please solve this question step-by-step with verified formulas and final answer.",
                                    "⚠️ Find Traps" to "What are the common traps and edge cases in this exact problem?",
                                    "📐 Formulas & Rules" to "Extract all relevant formulas, theorems, and state transition rules for this.",
                                    "💡 Socratic Hint" to "Give me a guiding hint to solve this problem without spoiling the complete answer."
                                )
                                items(attachPrompts) { (label, prompt) ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                        modifier = Modifier.clickable {
                                            handleSend(prompt)
                                        }
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. Thumb Row 3: Main Typing Capsule & Quick Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Attachment Action
                    IconButton(
                        onClick = { showAttachmentSheet = true },
                        modifier = Modifier
                            .padding(bottom = 2.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (attachedUri != null || attachedBitmap != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            )
                    ) {
                        Icon(
                            imageVector = if (attachedUri != null || attachedBitmap != null) Icons.Default.CheckCircle else Icons.Default.AddCircleOutline,
                            contentDescription = "Attach Question or Notes",
                            tint = if (attachedUri != null || attachedBitmap != null) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Text Input Capsule
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = if (inputText.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .animateContentSize()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            OutlinedTextField(
                                value = inputText,
                                onValueChange = { inputText = it },
                                placeholder = {
                                    Text(
                                        if (attachedUri != null || attachedBitmap != null) "Ask about attached image/notes..." else "Ask concept, formula, code or PYQ...",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("ai_tutor_input_field"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    disabledBorderColor = Color.Transparent,
                                    errorBorderColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                ),
                                maxLines = 5,
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 13.5.sp,
                                    lineHeight = 18.sp
                                )
                            )

                            if (inputText.isNotEmpty()) {
                                IconButton(
                                    onClick = { inputText = "" },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Clear text",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Send Action with Gradient
                    val hasContent = inputText.isNotBlank() || attachedUri != null || attachedBitmap != null || attachedFileName != null
                    val isReady = hasContent && !isLoading
                    IconButton(
                        onClick = {
                            if (isReady) {
                                handleSend()
                            }
                        },
                        enabled = isReady,
                        modifier = Modifier
                            .padding(bottom = 2.dp)
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (isReady) {
                                    Brush.linearGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.tertiary
                                        )
                                    )
                                } else {
                                    Brush.linearGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.surfaceVariant,
                                            MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    )
                                }
                            )
                            .testTag("send_ai_tutor_msg_btn")
                    ) {
                        Icon(
                            imageVector = if (isLoading) Icons.Default.HourglassTop else Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send prompt",
                            tint = if (isReady) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    // Attachment Modal Bottom Sheet with Real Upload Options
    if (showAttachmentSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAttachmentSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Attach to GATEX AI Agent",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Multimodal Vision & Document Processing",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { showAttachmentSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close Sheet")
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))

                // Primary Upload Options Grid / List
                ListItem(
                    headlineContent = { Text("📸 Camera Snap", fontWeight = FontWeight.Bold) },
                    supportingContent = { Text("Take live photo of textbook problem or handwritten answer") },
                    leadingContent = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    },
                    modifier = Modifier.clickable {
                        showAttachmentSheet = false
                        cameraLauncher.launch(null)
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                ListItem(
                    headlineContent = { Text("🖼️ Choose Photo / Screenshot", fontWeight = FontWeight.Bold) },
                    supportingContent = { Text("Pick diagram, circuit, or test paper from gallery") },
                    leadingContent = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        }
                    },
                    modifier = Modifier.clickable {
                        showAttachmentSheet = false
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                ListItem(
                    headlineContent = { Text("📄 Upload PDF / Notes File", fontWeight = FontWeight.Bold) },
                    supportingContent = { Text("Select syllabus doc or lecture slides from device storage") },
                    leadingContent = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.tertiaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                        }
                    },
                    modifier = Modifier.clickable {
                        showAttachmentSheet = false
                        documentPickerLauncher.launch("*/*")
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                ListItem(
                    headlineContent = { Text("💻 Paste Code / Math LaTeX Snippet", fontWeight = FontWeight.Bold) },
                    supportingContent = { Text("Input tricky C recursion, SQL query, or discrete formula") },
                    leadingContent = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Code, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    },
                    modifier = Modifier.clickable {
                        showAttachmentSheet = false
                        showCodeInputDialog = true
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "🎯 Quick Load High-Yield GATE Question Presets:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                val samplePresets = listOf(
                    "5-Stage Pipeline Hazard Timing Trace" to "Analyze a 5-stage MIPS pipeline (IF, ID, EX, MEM, WB) with 3 back-to-back RAW data dependencies. Calculate total clock cycles with and without operand forwarding.",
                    "BCNF vs 3NF Lossless Join Table" to "Relation R(A, B, C, D, E) with FDs: {A -> BC, CD -> E, B -> D, E -> A}. Find all candidate keys, verify highest normal form, and perform BCNF decomposition.",
                    "TCP Reno Fast Recovery Window Trace" to "A TCP connection experiencing Congestion Window progression with Threshold = 32 KB. If 3 duplicate ACKs occur at 48 KB, calculate the new cwnd and ssthresh values.",
                    "Dijkstra vs Bellman-Ford Negative Cycle" to "Given a directed graph with 6 vertices containing a negative weight edge. Explain why Dijkstra fails and trace step 1-3 of Bellman-Ford algorithm."
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(samplePresets) { (title, fullText) ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                            modifier = Modifier.clickable {
                                showAttachmentSheet = false
                                attachedFileName = "$title.diag"
                                attachedFileType = "PRESET"
                                inputText = fullText
                            }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Tap to load & solve with AI",
                                    fontSize = 9.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Code & Snippet Paste Dialog
    if (showCodeInputDialog) {
        AlertDialog(
            onDismissRequest = { showCodeInputDialog = false },
            title = {
                Text("Paste Code or Math Equation", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column {
                    Text(
                        "Enter or paste the code snippet, recurrence relation, or logic formula to analyze:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = rawCodeInput,
                        onValueChange = { rawCodeInput = it },
                        placeholder = { Text("int fun(int n) {\n  if(n <= 1) return 1;\n  return fun(n-1) + fun(n-2);\n}", fontFamily = FontFamily.Monospace, fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCodeInputDialog = false
                        if (rawCodeInput.isNotBlank()) {
                            attachedFileName = "Code_Snippet_${(System.currentTimeMillis() % 1000)}.c"
                            attachedFileType = "CODE"
                            inputText = "Please analyze this code snippet for output, time/space complexity, and recursive tree:\n\n```c\n$rawCodeInput\n```"
                            rawCodeInput = ""
                        }
                    }
                ) {
                    Text("Attach Code")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCodeInputDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Image Zoom Modal Dialog
    if (activeZoomImageUri != null) {
        Dialog(onDismissRequest = { activeZoomImageUri = null }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Question Diagram Preview",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        IconButton(onClick = { activeZoomImageUri = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close Zoom")
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    AsyncImage(
                        model = activeZoomImageUri,
                        contentDescription = "Full Size Question Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 380.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    onActionClick: (com.example.ui.AgentAction) -> Unit = {},
    onCopy: () -> Unit,
    onImageClick: (String) -> Unit = {}
) {
    val isUser = message.sender == "USER"
    val clipboardManager = LocalClipboardManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
        }

        Card(
            shape = RoundedCornerShape(
                topStart = if (isUser) 18.dp else 4.dp,
                topEnd = if (isUser) 4.dp else 18.dp,
                bottomStart = 18.dp,
                bottomEnd = 18.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isUser) 1.dp else 2.dp),
            modifier = Modifier
                .widthIn(max = 520.dp)
                .border(
                    width = 1.dp,
                    color = if (isUser) Color.Transparent else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(18.dp)
                )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (!isUser) {
                    // AI Tutor Header Tag
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = message.roleName ?: "GATEX AI TUTOR",
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            val modelLabel = when {
                                message.modelName?.contains("pro", ignoreCase = true) == true -> "3.1 Pro"
                                message.modelName?.contains("lite", ignoreCase = true) == true -> "3.1 Lite"
                                message.modelName?.contains("flash", ignoreCase = true) == true -> "3.5 Flash"
                                message.modelName == "domain-offline" -> "GATE Engine"
                                else -> "CSE 2027"
                            }
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                            ) {
                                Text(
                                    text = modelLabel,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = onCopy,
                            modifier = Modifier.size(22.dp)
                        ) {
                            Icon(
                                Icons.Default.ContentCopy,
                                contentDescription = "Copy Entire Response",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        thickness = 0.8.dp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (isUser) {
                    if (!message.imageUri.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.2f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clickable { onImageClick(message.imageUri) }
                        ) {
                            Column {
                                AsyncImage(
                                    model = message.imageUri,
                                    contentDescription = "Attached question image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 200.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.ZoomIn,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.85f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Tap to zoom diagram",
                                        fontSize = 10.sp,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }
                            }
                        }
                    } else if (!message.attachmentName.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (message.attachmentType == "CODE") Icons.Default.Code else Icons.Default.AttachFile,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = message.attachmentName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Text(
                        text = message.text,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    TechnicalMessageRenderer(
                        content = message.text,
                        onCopyCode = { code ->
                            clipboardManager.setText(AnnotatedString(code))
                        }
                    )
                }

                // Render Action Cards if present
                if (!isUser && message.actions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "⚡ Suggested Action Drills:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        message.actions.forEach { action ->
                            Button(
                                onClick = { onActionClick(action) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("agent_action_btn_${action.type}")
                            ) {
                                Icon(
                                    imageVector = when (action.type) {
                                        "START_TEST" -> Icons.Default.Bolt
                                        "OPEN_MISTAKES" -> Icons.Default.Cancel
                                        "OPEN_REVISION" -> Icons.Default.Repeat
                                        "OPEN_PYQ" -> Icons.Default.HistoryEdu
                                        "OPEN_FORMULAS" -> Icons.Default.Functions
                                        else -> Icons.Default.PlayArrow
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = action.label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StructuredMarkdownRenderer(
    rawContent: String,
    onCopyCode: (String) -> Unit
) {
    TechnicalMessageRenderer(
        content = rawContent,
        onCopyCode = onCopyCode
    )
}

@Composable
fun CodeSnippetCard(code: String, onCopy: () -> Unit) {
    Surface(
        color = Color(0xFF1E1E2E),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF282A36))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Code / Logic",
                    color = Color(0xFF8BE9FD),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "Copy code",
                        tint = Color.LightGray,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
            Text(
                text = code,
                color = Color(0xFFF8F8F2),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 16.sp,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}

/**
 * Parses bold markdown markers **text** and inline code `code` into an AnnotatedString.
 */
fun parseInlineMarkdown(text: String): AnnotatedString {
    val builder = AnnotatedString.Builder()
    val boldPattern = Regex("""\*\*(.*?)\*\*|`([^`]+)`""")
    var lastIndex = 0

    val matches = boldPattern.findAll(text)
    for (match in matches) {
        if (match.range.first > lastIndex) {
            builder.append(text.substring(lastIndex, match.range.first))
        }

        val boldText = match.groups[1]?.value
        val inlineCode = match.groups[2]?.value

        if (boldText != null) {
            builder.pushStyle(androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold))
            builder.append(boldText)
            builder.pop()
        } else if (inlineCode != null) {
            builder.pushStyle(
                androidx.compose.ui.text.SpanStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold
                )
            )
            builder.append(inlineCode)
            builder.pop()
        }

        lastIndex = match.range.last + 1
    }

    if (lastIndex < text.length) {
        builder.append(text.substring(lastIndex))
    }

    return builder.toAnnotatedString()
}
