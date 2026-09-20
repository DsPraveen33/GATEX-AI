package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notification.NotificationHelper
import com.example.ui.MainViewModel
import com.example.ui.ScreenDestination
import com.example.ui.components.AICommandBarModal
import com.example.ui.components.GateVirtualCalculatorDialog
import com.example.ui.components.GatexOrbLogo
import com.example.ui.components.GatexSymbol
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermissionIfNeeded()
        handleNotificationIntent(intent)
        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val isSystemDark = androidx.compose.foundation.isSystemInDarkTheme()
            val isDark = when (themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemDark
            }

            GatexTheme(darkTheme = isDark) {
                MainAppScaffold(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        if (intent == null) return
        val destination = intent.getStringExtra(NotificationHelper.EXTRA_DESTINATION)
        val targetId = intent.getStringExtra(NotificationHelper.EXTRA_TARGET_ID)

        when (destination) {
            "PRACTICE" -> viewModel.navigateTo(ScreenDestination.Practice)
            "MOCK_TESTS" -> {
                val mockId = targetId?.toLongOrNull()
                if (mockId != null && mockId > 0) {
                    viewModel.navigateTo(ScreenDestination.ActiveMockTest(mockId))
                } else {
                    viewModel.navigateTo(ScreenDestination.MockTests)
                }
            }
            "REVISION" -> viewModel.navigateTo(ScreenDestination.RevisionDeck)
            "DASHBOARD" -> viewModel.navigateTo(ScreenDestination.Dashboard)
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                androidx.core.app.ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val isCalculatorOpen by viewModel.isCalculatorOpen.collectAsStateWithLifecycle()
    val calcDisplay by viewModel.calcDisplay.collectAsStateWithLifecycle()

    var showCommandBar by remember { mutableStateOf(false) }
    var showStudyHubSheet by remember { mutableStateOf(false) }

    val isFocusMode = currentScreen is ScreenDestination.FocusMode
    val isExamMode = currentScreen is ScreenDestination.ActiveMockTest
    val isLoginMode = currentScreen is ScreenDestination.Login

    Scaffold(
        topBar = {
            if (!isFocusMode && !isExamMode && !isLoginMode) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { showCommandBar = true }
                        ) {
                            GatexOrbLogo(size = 32.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "GATEX AI",
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 16.sp,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "GATE 2027 CSE • AI Study Agent",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { showCommandBar = true },
                            modifier = Modifier.testTag("top_bar_command_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Ask GATEX AI",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(
                            onClick = { viewModel.openCalculator() },
                            modifier = Modifier.testTag("top_bar_calculator_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = "GATE NAT Calculator",
                                tint = BrightAmber
                            )
                        }

                        IconButton(
                            onClick = { viewModel.navigateTo(ScreenDestination.Reminders) },
                            modifier = Modifier.testTag("top_bar_reminders_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddAlert,
                                contentDescription = "Study Reminders & Alarms",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = { viewModel.navigateTo(ScreenDestination.Settings) },
                            modifier = Modifier.testTag("top_bar_settings_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile & Settings",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            if (!isFocusMode && !isExamMode && !isLoginMode) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    tonalElevation = 6.dp
                ) {
                    val isHome = currentScreen is ScreenDestination.Dashboard
                    val isStudy = currentScreen is ScreenDestination.Syllabus ||
                            currentScreen is ScreenDestination.TopicDetail ||
                            currentScreen is ScreenDestination.Practice ||
                            currentScreen is ScreenDestination.PYQs ||
                            currentScreen is ScreenDestination.MockTests ||
                            currentScreen is ScreenDestination.FormulaBank ||
                            currentScreen is ScreenDestination.ConceptLab ||
                            currentScreen is ScreenDestination.CodingLab ||
                            currentScreen is ScreenDestination.RevisionDeck ||
                            currentScreen is ScreenDestination.MistakeBook ||
                            currentScreen is ScreenDestination.NotesKnowledgeBase

                    val isAI = currentScreen is ScreenDestination.AITutor
                    val isProgress = currentScreen is ScreenDestination.Analytics
                    val isProfile = currentScreen is ScreenDestination.Settings

                    // 1. HOME
                    NavigationBarItem(
                        selected = isHome,
                        onClick = { viewModel.navigateTo(ScreenDestination.Dashboard) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 10.sp, fontWeight = if (isHome) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_home")
                    )

                    // 2. STUDY
                    NavigationBarItem(
                        selected = isStudy,
                        onClick = { viewModel.navigateTo(ScreenDestination.Syllabus) },
                        icon = { Icon(Icons.Default.MenuBook, contentDescription = "Study") },
                        label = { Text("Study", fontSize = 10.sp, fontWeight = if (isStudy) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_study")
                    )

                    // 3. ✦ AI (Center Prominent AI Orb / Star Button)
                    NavigationBarItem(
                        selected = isAI,
                        onClick = { viewModel.navigateTo(ScreenDestination.AITutor) },
                        icon = {
                            GatexOrbLogo(size = 32.dp)
                        },
                        label = { Text("AI", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_ai")
                    )

                    // 4. PROGRESS
                    NavigationBarItem(
                        selected = isProgress,
                        onClick = { viewModel.navigateTo(ScreenDestination.Analytics) },
                        icon = { Icon(Icons.Default.Analytics, contentDescription = "Progress") },
                        label = { Text("Progress", fontSize = 10.sp, fontWeight = if (isProgress) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_progress")
                    )

                    // 5. PROFILE
                    NavigationBarItem(
                        selected = isProfile,
                        onClick = { viewModel.navigateTo(ScreenDestination.Settings) },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile", fontSize = 10.sp, fontWeight = if (isProfile) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_profile")
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    is ScreenDestination.Login -> LoginScreen(viewModel = viewModel)
                    is ScreenDestination.Dashboard -> DashboardScreen(viewModel = viewModel)
                    is ScreenDestination.Syllabus -> SyllabusScreen(viewModel = viewModel)
                    is ScreenDestination.TopicDetail -> TopicDetailScreen(topicId = screen.topicId, viewModel = viewModel)
                    is ScreenDestination.Practice -> PracticeScreen(viewModel = viewModel)
                    is ScreenDestination.PYQs -> PYQScreen(viewModel = viewModel)
                    is ScreenDestination.MockTests -> MockTestsListScreen(viewModel = viewModel)
                    is ScreenDestination.ActiveMockTest -> ActiveMockExamScreen(viewModel = viewModel)
                    is ScreenDestination.MockResult -> MockResultScreen(attemptId = screen.mockAttemptId, viewModel = viewModel)
                    is ScreenDestination.AITutor -> AITutorScreen(viewModel = viewModel)
                    is ScreenDestination.MistakeBook -> MistakeBookScreen(viewModel = viewModel)
                    is ScreenDestination.RevisionDeck -> RevisionDeckScreen(viewModel = viewModel)
                    is ScreenDestination.FormulaBank -> FormulaBankScreen(viewModel = viewModel)
                    is ScreenDestination.ConceptLab -> ConceptLabScreen(viewModel = viewModel)
                    is ScreenDestination.CodingLab -> CodingLabScreen(viewModel = viewModel)
                    is ScreenDestination.NotesKnowledgeBase -> NotesDocScreen(viewModel = viewModel)
                    is ScreenDestination.Analytics -> AnalyticsScreen(viewModel = viewModel)
                    is ScreenDestination.Settings -> SettingsScreen(viewModel = viewModel)
                    is ScreenDestination.Reminders -> RemindersScreen(viewModel = viewModel)
                    is ScreenDestination.QuestionStudio -> QuestionStudioScreen(viewModel = viewModel)
                    is ScreenDestination.FocusMode -> FocusModeScreen(
                        topicName = screen.topicName,
                        durationMinutes = screen.durationMinutes,
                        viewModel = viewModel,
                        onExit = { viewModel.navigateTo(ScreenDestination.Dashboard) }
                    )
                }
            }
        }
    }

    // --- GATE NAT VIRTUAL CALCULATOR DIALOG ---
    GateVirtualCalculatorDialog(
        isOpen = isCalculatorOpen,
        display = calcDisplay,
        onKeyPress = { viewModel.onCalcKeyPress(it) },
        onDismiss = { viewModel.closeCalculator() }
    )

    // --- UNIVERSAL AI COMMAND BAR (ASK GATEX...) ---
    AICommandBarModal(
        isOpen = showCommandBar,
        onDismiss = { showCommandBar = false },
        onNavigate = { dest -> viewModel.navigateTo(dest) },
        onExecuteAction = { prompt ->
            when {
                prompt.contains("remind", ignoreCase = true) || prompt.contains("alarm", ignoreCase = true) || prompt.contains("schedule", ignoreCase = true) -> viewModel.navigateTo(ScreenDestination.Reminders)
                prompt.contains("pyq", ignoreCase = true) -> viewModel.navigateTo(ScreenDestination.PYQs)
                prompt.contains("mock", ignoreCase = true) -> viewModel.navigateTo(ScreenDestination.MockTests)
                prompt.contains("revise", ignoreCase = true) || prompt.contains("flashcard", ignoreCase = true) -> viewModel.navigateTo(ScreenDestination.RevisionDeck)
                prompt.contains("mistake", ignoreCase = true) -> viewModel.navigateTo(ScreenDestination.MistakeBook)
                prompt.contains("formula", ignoreCase = true) -> viewModel.navigateTo(ScreenDestination.FormulaBank)
                prompt.contains("code", ignoreCase = true) || prompt.contains("c program", ignoreCase = true) -> viewModel.navigateTo(ScreenDestination.CodingLab)
                prompt.contains("lab", ignoreCase = true) || prompt.contains("visual", ignoreCase = true) -> viewModel.navigateTo(ScreenDestination.ConceptLab)
                prompt.contains("focus", ignoreCase = true) -> viewModel.navigateTo(ScreenDestination.FocusMode("Focused Study Drill", 25))
                prompt.contains("readiness", ignoreCase = true) || prompt.contains("progress", ignoreCase = true) -> viewModel.navigateTo(ScreenDestination.Analytics)
                else -> {
                    viewModel.navigateTo(ScreenDestination.AITutor)
                    viewModel.sendTutorMessage(prompt)
                }
            }
        }
    )

    // --- STUDY HUB BOTTOM SHEET ---
    if (showStudyHubSheet) {
        ModalBottomSheet(
            onDismissRequest = { showStudyHubSheet = false },
            containerColor = NavyDark,
            contentColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "GATEX Study Hub",
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 18.sp
                )
                Text(
                    text = "Curated Syllabus, Practice, PYQs, Mocks & Labs",
                    color = TextSecondaryDark,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                val hubItems = listOf(
                    Triple("Study Reminders & Push Alerts", Icons.Default.AddAlert, ScreenDestination.Reminders),
                    Triple("10 GATE CSE Subjects", Icons.Default.MenuBook, ScreenDestination.Syllabus),
                    Triple("Adaptive Practice Engine", Icons.Default.Quiz, ScreenDestination.Practice),
                    Triple("Official PYQ Archive (2015-2024)", Icons.Default.HistoryEdu, ScreenDestination.PYQs),
                    Triple("Mock Exam Simulator", Icons.Default.Timer, ScreenDestination.MockTests),
                    Triple("Spaced Flashcards & Revisions", Icons.Default.Style, ScreenDestination.RevisionDeck),
                    Triple("Mistake Book & Error Ledger", Icons.Default.WarningAmber, ScreenDestination.MistakeBook),
                    Triple("Formula & Theorem Bank", Icons.Default.Functions, ScreenDestination.FormulaBank),
                    Triple("Concept Visual Lab", Icons.Default.Sensors, ScreenDestination.ConceptLab),
                    Triple("C Programming & Trace Sandbox", Icons.Default.Code, ScreenDestination.CodingLab),
                    Triple("Notes & Knowledge Base", Icons.Default.NoteAlt, ScreenDestination.NotesKnowledgeBase)
                )

                hubItems.forEach { (title, icon, dest) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(NavySurface)
                            .clickable {
                                showStudyHubSheet = false
                                viewModel.navigateTo(dest)
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = icon, contentDescription = title, tint = ElectricCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
