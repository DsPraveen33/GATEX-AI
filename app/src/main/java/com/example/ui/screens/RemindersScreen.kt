package com.example.ui.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.data.model.MockTestEntity
import com.example.data.model.ReminderScheduleEntity
import com.example.data.model.ReminderType
import com.example.notification.NotificationHelper
import com.example.ui.MainViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RemindersScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val reminders by viewModel.reminders.collectAsStateWithLifecycle()
    val mockTests by viewModel.mockTests.collectAsStateWithLifecycle()
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()

    var showCreateDialog by remember { mutableStateOf(false) }
    var editingReminder by remember { mutableStateOf<ReminderScheduleEntity?>(null) }
    var selectedFilter by remember { mutableStateOf("ALL") }

    var hasPermission by remember {
        mutableStateOf(NotificationHelper.hasNotificationPermission(context))
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasPermission = isGranted
            if (isGranted) {
                Toast.makeText(context, "Notifications enabled successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Notification permission is needed for study reminders", Toast.LENGTH_LONG).show()
            }
        }
    )

    val filteredReminders = remember(reminders, selectedFilter) {
        when (selectedFilter) {
            "PRACTICE" -> reminders.filter { it.type == ReminderType.DAILY_PRACTICE }
            "MOCKS" -> reminders.filter { it.type == ReminderType.MOCK_TEST }
            "REVISION" -> reminders.filter { it.type == ReminderType.SPACED_REVISION }
            else -> reminders
        }
    }

    Scaffold(
        containerColor = NavyDeep,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingReminder = null
                    showCreateDialog = true
                },
                containerColor = ElectricCyan,
                contentColor = NavyDeep,
                modifier = Modifier.testTag("add_reminder_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AddAlarm, contentDescription = "Add Reminder")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Schedule Reminder", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Header
            item {
                Text(
                    text = "Study Reminders & Push Alerts",
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 20.sp
                )
                Text(
                    text = "Never miss daily problem solving sessions or upcoming mock exam simulations",
                    color = TextSecondaryDark,
                    fontSize = 12.sp
                )
            }

            // Notification Permission Banner
            item {
                if (!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = BrightAmber.copy(alpha = 0.15f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BrightAmber.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = BrightAmber,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Enable Push Notifications",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Allow notifications to receive exact alarm reminders on your device",
                                        color = TextSecondaryDark,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Button(
                                onClick = {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrightAmber),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Allow", color = NavyDeep, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = NavyCard),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(NeonMint)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "System Alarm Scheduler: ACTIVE",
                                    color = NeonMint,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            FilledTonalButton(
                                onClick = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasPermission) {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        viewModel.triggerTestNotification()
                                        Toast.makeText(context, "Test notification triggered! Check status bar.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = ElectricBlue.copy(alpha = 0.25f),
                                    contentColor = ElectricCyan
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("test_notification_btn")
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Test Notification (2s)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Daily Free Complete Quiz Card
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF6366F1), RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FlashOn, contentDescription = null, tint = BrightAmber, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Daily Free Complete Quiz Alert",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            val isDailyQuizScheduled = reminders.any { it.id == 99999L && it.isEnabled }
                            Switch(
                                checked = isDailyQuizScheduled,
                                onCheckedChange = { isChecked ->
                                    viewModel.scheduleDailyFreeQuizNotification(hour = 8, minute = 0, isEnabled = isChecked)
                                    Toast.makeText(context, if (isChecked) "Daily Free Quiz Alert scheduled for 8:00 AM" else "Daily Free Quiz Alert disabled", Toast.LENGTH_SHORT).show()
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NeonMint,
                                    checkedTrackColor = NeonMint.copy(alpha = 0.5f)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Automatic push notification every morning (8:00 AM) to attempt the new 65-question / 100-mark paper with freshly updated questions and answers.",
                            color = TextSecondaryDark,
                            fontSize = 11.5.sp,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.triggerDailyQuizTestNotification()
                                    Toast.makeText(context, "🔔 Test Daily Quiz Notification sent!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Send Alert Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { viewModel.navigateTo(com.example.ui.ScreenDestination.QuestionStudio) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricCyan)
                            ) {
                                Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Download / Edit Qs", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Quick Schedule Templates Row
            item {
                Column {
                    Text(
                        text = "Quick Presets",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickScheduleChip(
                            title = "Daily 7:30 PM",
                            subtitle = "Daily Practice Sprint",
                            icon = Icons.Default.Timer,
                            color = ElectricCyan,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.scheduleDailyPracticeReminder(19, 30, "EVERYDAY", "All Syllabus Drill")
                                Toast.makeText(context, "Scheduled Daily 7:30 PM Practice Reminder!", Toast.LENGTH_SHORT).show()
                            }
                        )
                        QuickScheduleChip(
                            title = "Weekend Mock",
                            subtitle = "Full 3-Hr Exam (9 AM)",
                            icon = Icons.Default.Quiz,
                            color = BrightAmber,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                val firstMock = mockTests.firstOrNull() ?: MockTestEntity(
                                    id = 1,
                                    title = "GATE 2027 CSE Full Mock #1",
                                    testType = "FULL_GATE",
                                    questionIdsJson = "[]"
                                )
                                viewModel.scheduleMockTestReminder(firstMock, "", 9, 0)
                                Toast.makeText(context, "Scheduled Weekend 9:00 AM Mock Test Alert!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }

            // Filter Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val filters = listOf(
                        "ALL" to "All Reminders (${reminders.size})",
                        "PRACTICE" to "Daily Practice (${reminders.count { it.type == ReminderType.DAILY_PRACTICE }})",
                        "MOCKS" to "Mock Tests (${reminders.count { it.type == ReminderType.MOCK_TEST }})",
                        "REVISION" to "Spaced Revision (${reminders.count { it.type == ReminderType.SPACED_REVISION }})"
                    )
                    items(filters) { (key, label) ->
                        FilterChip(
                            selected = selectedFilter == key,
                            onClick = { selectedFilter = key },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricCyan,
                                selectedLabelColor = NavyDeep,
                                containerColor = NavyCard,
                                labelColor = TextSecondaryDark
                            )
                        )
                    }
                }
            }

            // Reminders List
            if (filteredReminders.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = NavyCard),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(30.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.AlarmOff,
                                contentDescription = null,
                                tint = TextSecondaryDark,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No Reminders Found",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Schedule a daily practice drill or upcoming mock test reminder using the button below.",
                                color = TextSecondaryDark,
                                fontSize = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(filteredReminders, key = { it.id }) { reminder ->
                    ReminderItemCard(
                        reminder = reminder,
                        onToggle = { isEnabled ->
                            viewModel.toggleReminder(reminder, isEnabled)
                        },
                        onEdit = {
                            editingReminder = reminder
                            showCreateDialog = true
                        },
                        onDelete = {
                            viewModel.deleteReminder(reminder)
                            Toast.makeText(context, "Reminder removed", Toast.LENGTH_SHORT).show()
                        },
                        onTestTrigger = {
                            viewModel.triggerTestNotification(
                                title = reminder.title,
                                message = reminder.message,
                                type = reminder.type
                            )
                            Toast.makeText(context, "Triggering test reminder in 2 seconds...", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateOrEditReminderDialog(
            existingReminder = editingReminder,
            mockTests = mockTests,
            subjects = subjects,
            onDismiss = {
                showCreateDialog = false
                editingReminder = null
            },
            onSave = { newReminder ->
                viewModel.saveReminder(newReminder)
                showCreateDialog = false
                editingReminder = null
                Toast.makeText(context, "Reminder saved & scheduled!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun QuickScheduleChip(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        modifier = modifier
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(text = subtitle, color = TextSecondaryDark, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun ReminderItemCard(
    reminder: ReminderScheduleEntity,
    onToggle: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTestTrigger: () -> Unit
) {
    val typeColor = when (reminder.type) {
        ReminderType.DAILY_PRACTICE -> ElectricCyan
        ReminderType.MOCK_TEST -> BrightAmber
        ReminderType.SPACED_REVISION -> RoyalPurple
        ReminderType.CUSTOM_SESSION -> NeonMint
    }

    val typeLabel = when (reminder.type) {
        ReminderType.DAILY_PRACTICE -> "DAILY PRACTICE"
        ReminderType.MOCK_TEST -> "MOCK TEST"
        ReminderType.SPACED_REVISION -> "SPACED REVISION"
        ReminderType.CUSTOM_SESSION -> "CUSTOM SESSION"
    }

    val formattedTime = remember(reminder.hour, reminder.minute) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, reminder.hour)
            set(Calendar.MINUTE, reminder.minute)
        }
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (reminder.isEnabled) typeColor.copy(alpha = 0.4f) else Color(0xFF334155),
                RoundedCornerShape(16.dp)
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Row 1: Badges & Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = typeColor.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = typeLabel,
                            color = typeColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (reminder.scheduledDate.isNotBlank()) "Date: ${reminder.scheduledDate}" else reminder.daysOfWeek,
                        color = TextSecondaryDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Switch(
                    checked = reminder.isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = typeColor,
                        checkedTrackColor = typeColor.copy(alpha = 0.4f),
                        uncheckedThumbColor = TextSecondaryDark,
                        uncheckedTrackColor = NavyDark
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Time & Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedTime,
                    color = if (reminder.isEnabled) Color.White else TextSecondaryDark,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reminder.title,
                        color = if (reminder.isEnabled) Color.White else TextSecondaryDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    if (reminder.targetName.isNotBlank()) {
                        Text(
                            text = "Target: ${reminder.targetName}",
                            color = typeColor.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Message Body
            Text(
                text = reminder.message,
                color = TextSecondaryDark,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Footer Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalButton(
                    onClick = onTestTrigger,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = NavySurface,
                        contentColor = ElectricCyan
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Trigger Now (2s)", fontSize = 11.sp)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextSecondaryDark, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrEditReminderDialog(
    existingReminder: ReminderScheduleEntity?,
    mockTests: List<MockTestEntity>,
    subjects: List<com.example.data.model.SubjectEntity>,
    onDismiss: () -> Unit,
    onSave: (ReminderScheduleEntity) -> Unit
) {
    var title by remember { mutableStateOf(existingReminder?.title ?: "Daily GATE CSE Practice Sprint") }
    var message by remember { mutableStateOf(existingReminder?.message ?: "Time to solve 10 questions and maintain your study streak!") }
    var reminderType by remember { mutableStateOf(existingReminder?.type ?: ReminderType.DAILY_PRACTICE) }
    var selectedHour by remember { mutableIntStateOf(existingReminder?.hour ?: 19) }
    var selectedMinute by remember { mutableIntStateOf(existingReminder?.minute ?: 30) }
    var daysOfWeek by remember { mutableStateOf(existingReminder?.daysOfWeek ?: "EVERYDAY") }
    var scheduledDate by remember { mutableStateOf(existingReminder?.scheduledDate ?: "") }
    var targetId by remember { mutableStateOf(existingReminder?.targetId ?: "") }
    var targetName by remember { mutableStateOf(existingReminder?.targetName ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NavyCard,
        title = {
            Text(
                text = if (existingReminder != null) "Edit Study Reminder" else "Schedule Push Reminder",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Reminder Type Selector
                item {
                    Text("Reminder Category", color = TextSecondaryDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    val types = listOf(
                        ReminderType.DAILY_PRACTICE to "Daily Practice",
                        ReminderType.MOCK_TEST to "Mock Exam",
                        ReminderType.SPACED_REVISION to "Revision Deck",
                        ReminderType.CUSTOM_SESSION to "Custom Study"
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(types) { (type, label) ->
                            FilterChip(
                                selected = reminderType == type,
                                onClick = {
                                    reminderType = type
                                    when (type) {
                                        ReminderType.DAILY_PRACTICE -> {
                                            title = "Daily GATE CSE Practice Sprint"
                                            message = "Time to solve 10 questions and maintain your study streak!"
                                            daysOfWeek = "EVERYDAY"
                                        }
                                        ReminderType.MOCK_TEST -> {
                                            title = "GATE CSE Mock Exam Alert"
                                            message = "Your scheduled 3-Hour full test starts soon. Prepare your scratchpad!"
                                            daysOfWeek = "WEEKENDS"
                                        }
                                        ReminderType.SPACED_REVISION -> {
                                            title = "Night Revision & Flashcards"
                                            message = "Review due flashcards and mistake trap patterns before sleeping."
                                            daysOfWeek = "EVERYDAY"
                                        }
                                        ReminderType.CUSTOM_SESSION -> {
                                            title = "GATE CSE Study Session"
                                            message = "Time for focused conceptual deep dive and numericals."
                                        }
                                    }
                                },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElectricCyan,
                                    selectedLabelColor = NavyDeep,
                                    containerColor = NavySurface,
                                    labelColor = TextSecondaryDark
                                )
                            )
                        }
                    }
                }

                // If Mock Test, select which mock test
                if (reminderType == ReminderType.MOCK_TEST && mockTests.isNotEmpty()) {
                    item {
                        Text("Select Target Mock Exam", color = TextSecondaryDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(mockTests) { mock ->
                                FilterChip(
                                    selected = targetId == mock.id.toString(),
                                    onClick = {
                                        targetId = mock.id.toString()
                                        targetName = mock.title
                                        title = "Upcoming Mock: ${mock.title}"
                                    },
                                    label = { Text(mock.title.take(24) + if (mock.title.length > 24) "..." else "", fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BrightAmber,
                                        selectedLabelColor = NavyDeep,
                                        containerColor = NavySurface,
                                        labelColor = TextSecondaryDark
                                    )
                                )
                            }
                        }
                    }
                }

                // Title Input
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Reminder Title", color = TextSecondaryDark, fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = Color(0xFF334155)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Message Input
                item {
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Notification Message", color = TextSecondaryDark, fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = Color(0xFF334155)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Time Selection (Hour & Minute)
                item {
                    Text("Alarm Time (24h)", color = TextSecondaryDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Hour Selector
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Hour: ${String.format(Locale.getDefault(), "%02d", selectedHour)}:00", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Slider(
                                value = selectedHour.toFloat(),
                                onValueChange = { selectedHour = it.toInt() },
                                valueRange = 0f..23f,
                                steps = 22,
                                colors = SliderDefaults.colors(
                                    thumbColor = ElectricCyan,
                                    activeTrackColor = ElectricCyan,
                                    inactiveTrackColor = NavyDark
                                )
                            )
                        }

                        // Minute Selector
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Minute: :${String.format(Locale.getDefault(), "%02d", selectedMinute)}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Slider(
                                value = (selectedMinute / 5).toFloat(),
                                onValueChange = { selectedMinute = (it.toInt() * 5).coerceIn(0, 55) },
                                valueRange = 0f..11f,
                                steps = 10,
                                colors = SliderDefaults.colors(
                                    thumbColor = BrightAmber,
                                    activeTrackColor = BrightAmber,
                                    inactiveTrackColor = NavyDark
                                )
                            )
                        }
                    }

                    // Formatted time display
                    val displayTime = remember(selectedHour, selectedMinute) {
                        val cal = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, selectedHour)
                            set(Calendar.MINUTE, selectedMinute)
                        }
                        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time)
                    }
                    Text(
                        text = "Alarm will ring at: $displayTime",
                        color = ElectricCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Recurrence Selector
                item {
                    Text("Repeat Schedule", color = TextSecondaryDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    val repeatOptions = listOf("EVERYDAY", "WEEKDAYS", "WEEKENDS", "ONE_TIME")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(repeatOptions) { option ->
                            FilterChip(
                                selected = if (option == "ONE_TIME") scheduledDate.isNotBlank() else (daysOfWeek == option && scheduledDate.isBlank()),
                                onClick = {
                                    if (option == "ONE_TIME") {
                                        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
                                        scheduledDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(tomorrow.time)
                                        daysOfWeek = "CUSTOM"
                                    } else {
                                        daysOfWeek = option
                                        scheduledDate = ""
                                    }
                                },
                                label = {
                                    Text(
                                        when (option) {
                                            "EVERYDAY" -> "Everyday"
                                            "WEEKDAYS" -> "Mon - Fri"
                                            "WEEKENDS" -> "Sat - Sun"
                                            "ONE_TIME" -> "Tomorrow (One-time)"
                                            else -> option
                                        },
                                        fontSize = 11.sp
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElectricCyan,
                                    selectedLabelColor = NavyDeep,
                                    containerColor = NavySurface,
                                    labelColor = TextSecondaryDark
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) return@Button
                    val reminder = ReminderScheduleEntity(
                        id = existingReminder?.id ?: 0L,
                        title = title.trim(),
                        message = message.trim(),
                        type = reminderType,
                        targetId = targetId,
                        targetName = targetName,
                        hour = selectedHour,
                        minute = selectedMinute,
                        scheduledDate = scheduledDate,
                        daysOfWeek = daysOfWeek,
                        isEnabled = true
                    )
                    onSave(reminder)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Schedule Alarm", color = NavyDeep, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondaryDark)
            }
        }
    )
}
