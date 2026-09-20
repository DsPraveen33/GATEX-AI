package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.log2

@Composable
fun ConceptLabScreen(viewModel: MainViewModel) {
    var selectedLab by remember { mutableStateOf("PAGE_REPLACEMENT") } // PAGE_REPLACEMENT, SLIDING_WINDOW, AVL_ROTATION, CPU_SCHEDULING, SUBNETTING

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            Text(
                text = "Interactive Concept & Visualizer Lab",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp
            )
            Text(
                text = "Simulate Algorithms, Compute Efficiency & Master Tricky Numerical Edge Cases",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }

        // --- LAB SELECTOR CHIPS ---
        item {
            val labs = listOf(
                "PAGE_REPLACEMENT" to "Page Replacement (LRU/FIFO)",
                "SLIDING_WINDOW" to "Sliding Window Protocol",
                "AVL_ROTATION" to "AVL Tree Rotations",
                "CPU_SCHEDULING" to "CPU Gantt Scheduler",
                "SUBNETTING" to "CIDR Subnet Calculator"
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(labs) { (key, label) ->
                    FilterChip(
                        selected = selectedLab == key,
                        onClick = { selectedLab = key },
                        label = { Text(label, fontSize = 12.sp) }
                    )
                }
            }
        }

        when (selectedLab) {
            "PAGE_REPLACEMENT" -> item { InteractivePageReplacementVisualizer() }
            "SLIDING_WINDOW" -> item { InteractiveSlidingWindowVisualizer() }
            "AVL_ROTATION" -> item { InteractiveAvlTreeVisualizer() }
            "CPU_SCHEDULING" -> item { CpuSchedulingVisualizer() }
            "SUBNETTING" -> item { SubnettingVisualizer() }
        }
    }
}

@Composable
fun InteractivePageReplacementVisualizer() {
    val refString = remember { listOf(7, 0, 1, 2, 0, 3, 0, 4, 2, 3) }
    var currentStep by remember { mutableIntStateOf(0) }
    var selectedAlgo by remember { mutableStateOf("LRU") } // FIFO, LRU, OPTIMAL
    val frameCapacity = 3

    // Simulation calculation up to currentStep
    val framesHistory = remember(selectedAlgo, currentStep) {
        val history = mutableListOf<List<Int?>>()
        val hitMissList = mutableListOf<Boolean>() // true = hit, false = miss
        val currentFrames = mutableListOf<Int?>()
        val pageUsageOrder = mutableListOf<Int>() // for LRU

        for (i in 0..currentStep) {
            val page = refString[i]
            if (currentFrames.contains(page)) {
                hitMissList.add(true)
                if (selectedAlgo == "LRU") {
                    pageUsageOrder.remove(page)
                    pageUsageOrder.add(page)
                }
            } else {
                hitMissList.add(false)
                if (currentFrames.size < frameCapacity) {
                    currentFrames.add(page)
                    pageUsageOrder.add(page)
                } else {
                    when (selectedAlgo) {
                        "FIFO" -> {
                            currentFrames.removeAt(0)
                            currentFrames.add(page)
                        }
                        "LRU" -> {
                            val lruPage = pageUsageOrder.removeAt(0)
                            val idx = currentFrames.indexOf(lruPage)
                            if (idx != -1) currentFrames[idx] = page
                            pageUsageOrder.add(page)
                        }
                        "OPTIMAL" -> {
                            // Find page that won't be used for longest time in future
                            var farthestIdx = -1
                            var victim = currentFrames[0]
                            for (p in currentFrames) {
                                val nextUse = refString.subList(i + 1, refString.size).indexOf(p)
                                if (nextUse == -1) {
                                    victim = p
                                    break
                                } else if (nextUse > farthestIdx) {
                                    farthestIdx = nextUse
                                    victim = p
                                }
                            }
                            val idx = currentFrames.indexOf(victim)
                            if (idx != -1) currentFrames[idx] = page
                        }
                    }
                }
            }
            history.add(currentFrames.toList())
        }
        Pair(history, hitMissList)
    }

    val currentFrames = framesHistory.first.getOrElse(currentStep) { emptyList() }
    val isCurrentHit = framesHistory.second.getOrElse(currentStep) { false }
    val totalFaults = framesHistory.second.count { !it }
    val totalHits = framesHistory.second.count { it }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Page Replacement Simulator (3 Frames)",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isCurrentHit) Color(0xFF10B981).copy(alpha = 0.2f) else MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = if (isCurrentHit) "🎯 PAGE HIT" else "⚠️ PAGE FAULT",
                        color = if (isCurrentHit) Color(0xFF10B981) else MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Algorithm selector
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("FIFO", "LRU", "OPTIMAL").forEach { algo ->
                    FilterChip(
                        selected = selectedAlgo == algo,
                        onClick = {
                            selectedAlgo = algo
                            currentStep = 0
                        },
                        label = { Text(algo, fontSize = 11.sp) }
                    )
                }
            }

            // Reference stream horizontal display
            Text(text = "Reference String Stream:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                refString.forEachIndexed { index, num ->
                    val isCurrent = index == currentStep
                    val isPassed = index < currentStep
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when {
                                    isCurrent -> MaterialTheme.colorScheme.primary
                                    isPassed -> MaterialTheme.colorScheme.surfaceVariant
                                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                }
                            )
                            .clickable { currentStep = index },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = num.toString(),
                            color = if (isCurrent) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Frame Slots Visualizer
            Text(text = "Physical Frame Slots (RAM):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                (0 until frameCapacity).forEach { slot ->
                    val pageInSlot = currentFrames.getOrNull(slot)
                    val isJustLoaded = pageInSlot == refString[currentStep] && !isCurrentHit
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isJustLoaded) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .border(
                                1.5.dp,
                                if (isJustLoaded) MaterialTheme.colorScheme.primary else Color.Transparent,
                                RoundedCornerShape(10.dp)
                            )
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Frame $slot",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = pageInSlot?.toString() ?: "—",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (pageInSlot != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Step Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { if (currentStep > 0) currentStep-- },
                    enabled = currentStep > 0,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Prev Step", fontSize = 11.sp)
                }

                Text(
                    text = "Step ${currentStep + 1} of ${refString.size}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Button(
                    onClick = { if (currentStep < refString.size - 1) currentStep++ },
                    enabled = currentStep < refString.size - 1,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Next Step", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }

            // Cumulative Telemetry Box
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Total Faults", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "$totalFaults", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Total Hits", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "$totalHits", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val hitRatio = if (currentStep + 1 > 0) ((totalHits.toDouble() / (currentStep + 1)) * 100).toInt() else 0
                        Text(text = "Hit Ratio", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "$hitRatio%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveSlidingWindowVisualizer() {
    var windowSize by remember { mutableFloatStateOf(4f) }
    var propagationDelayMs by remember { mutableFloatStateOf(20f) }
    var transmissionDelayMs by remember { mutableFloatStateOf(5f) }

    val a = propagationDelayMs / transmissionDelayMs
    val efficiencyStopAndWait = 1.0 / (1.0 + 2.0 * a)
    val maxEfficiencyGBN = (windowSize / (1.0 + 2.0 * a)).coerceAtMost(1.0)
    val optimalWindowSize = ceil(1.0 + 2.0 * a).toInt()
    val seqBitsGBN = ceil(log2((windowSize + 1).toDouble())).toInt()
    val seqBitsSR = ceil(log2((2 * windowSize).toDouble())).toInt()

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Sliding Window Protocol Simulator (GATE CN)",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp
            )
            Text(
                text = "Computes parameter 'a = Tp / Tt', efficiency formulas, and required sequence number bits.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Sliders
            Text(text = "Sender Window Size (N): ${windowSize.toInt()} frames", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Slider(
                value = windowSize,
                onValueChange = { windowSize = it },
                valueRange = 1f..16f,
                steps = 14
            )

            Text(text = "Propagation Delay (Tp): ${propagationDelayMs.toInt()} ms", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Slider(
                value = propagationDelayMs,
                onValueChange = { propagationDelayMs = it },
                valueRange = 5f..50f
            )

            Text(text = "Transmission Delay (Tt): ${transmissionDelayMs.toInt()} ms", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Slider(
                value = transmissionDelayMs,
                onValueChange = { transmissionDelayMs = it },
                valueRange = 1f..20f
            )

            // Key Calculations Grid
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Parameter a = Tp / Tt = ${String.format(Locale.US, "%.2f", a)}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Optimal Window Size (100% Efficiency): N_opt = 1 + 2a = $optimalWindowSize frames",
                        color = Color(0xFF10B981),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Stop-and-Wait Efficiency: ${(efficiencyStopAndWait * 100).toInt()}%",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Go-Back-N (N=${windowSize.toInt()}) Efficiency: ${(maxEfficiencyGBN * 100).toInt()}%",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Text(
                        text = "Sequence Bits Required: GBN = $seqBitsGBN bits (ceil(log2(N+1))) | SR = $seqBitsSR bits (ceil(log2(2N)))",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun InteractiveAvlTreeVisualizer() {
    var selectedRotation by remember { mutableStateOf("LL") } // LL, RR, LR, RL

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "AVL Tree Rotations & Balance Factor Engine",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp
            )
            Text(
                text = "Balance Factor BF = height(Left) - height(Right) must be in {-1, 0, +1}.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("LL" to "LL (Right Rotate)", "RR" to "RR (Left Rotate)", "LR" to "LR (Double)", "RL" to "RL (Double)").forEach { (key, label) ->
                    FilterChip(
                        selected = selectedRotation == key,
                        onClick = { selectedRotation = key },
                        label = { Text(label, fontSize = 11.sp) }
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    when (selectedRotation) {
                        "LL" -> {
                            Text(text = "Imbalance Case: Inserted into Left Subtree of Left Child (BF = +2)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                            Text(text = "Solution: Single RIGHT ROTATION on root node.", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(text = "Before: 30(BF=+2) → 20(BF=+1) → 10", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                            Text(text = "After Right Rotation: 20 is root with Left Child = 10, Right Child = 30 (All BF = 0) ✅", color = MaterialTheme.colorScheme.onSurface, fontSize = 11.sp)
                        }
                        "RR" -> {
                            Text(text = "Imbalance Case: Inserted into Right Subtree of Right Child (BF = -2)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                            Text(text = "Solution: Single LEFT ROTATION on root node.", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(text = "Before: 10(BF=-2) → 20(BF=-1) → 30", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                            Text(text = "After Left Rotation: 20 is root with Left Child = 10, Right Child = 30 (All BF = 0) ✅", color = MaterialTheme.colorScheme.onSurface, fontSize = 11.sp)
                        }
                        "LR" -> {
                            Text(text = "Imbalance Case: Inserted into Right Subtree of Left Child (BF = +2 at root, -1 at child)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                            Text(text = "Solution: 1. Left Rotate child node, 2. Right Rotate root node.", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(text = "Before: 30 → 10 → 20", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                            Text(text = "After LR: 20 becomes balanced root.", color = MaterialTheme.colorScheme.onSurface, fontSize = 11.sp)
                        }
                        "RL" -> {
                            Text(text = "Imbalance Case: Inserted into Left Subtree of Right Child (BF = -2 at root, +1 at child)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                            Text(text = "Solution: 1. Right Rotate child node, 2. Left Rotate root node.", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(text = "Before: 10 → 30 → 20", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                            Text(text = "After RL: 20 becomes balanced root.", color = MaterialTheme.colorScheme.onSurface, fontSize = 11.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⭐ Maximum Height of AVL Tree with N nodes: h <= 1.44 log2(N+2) - 0.328",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun CpuSchedulingVisualizer() {
    var algorithm by remember { mutableStateOf("FCFS") } // FCFS, SJF, Round Robin
    var timeQuantum by remember { mutableIntStateOf(2) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "CPU Scheduling Simulation (GATE OS)",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("FCFS", "SJF", "Round Robin").forEach { algo ->
                    FilterChip(
                        selected = algorithm == algo,
                        onClick = { algorithm = algo },
                        label = { Text(algo, fontSize = 11.sp) }
                    )
                }
            }

            if (algorithm == "Round Robin") {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Time Quantum: $timeQuantum", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = { if (timeQuantum > 1) timeQuantum-- },
                        shape = RoundedCornerShape(6.dp)
                    ) { Text("-") }
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = { timeQuantum++ },
                        shape = RoundedCornerShape(6.dp)
                    ) { Text("+") }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Process Workload: P1 (BT=4), P2 (BT=3), P3 (BT=1)",
                color = Color(0xFFF59E0B),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Gantt Chart Bar Representation
            Text(text = "Simulated Gantt Chart Execution:", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            ) {
                val segments = when (algorithm) {
                    "FCFS" -> listOf("P1 (0-4)" to 4, "P2 (4-7)" to 3, "P3 (7-8)" to 1)
                    "SJF" -> listOf("P3 (0-1)" to 1, "P2 (1-4)" to 3, "P1 (4-8)" to 4)
                    else -> listOf("P1" to 2, "P2" to 2, "P3" to 1, "P1" to 2, "P2" to 1)
                }

                val colors = listOf(Color(0xFF3B82F6), Color(0xFF8B5CF6), Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFF06B6D4))

                segments.forEachIndexed { idx, (label, weight) ->
                    Box(
                        modifier = Modifier
                            .weight(weight.toFloat())
                            .fillMaxHeight()
                            .background(colors[idx % colors.size].copy(alpha = 0.35f))
                            .border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = label, color = MaterialTheme.colorScheme.onSurface, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Performance Metrics
            val (avgWT, avgTAT) = when (algorithm) {
                "FCFS" -> 3.67 to 6.33
                "SJF" -> 1.67 to 4.33
                else -> 2.67 to 5.33
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Average WT: $avgWT ms", color = Color(0xFF10B981), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = "Average TAT: $avgTAT ms", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SubnettingVisualizer() {
    var cidr by remember { mutableIntStateOf(27) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "CIDR & Subnet Range Calculator (GATE CN)",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "IP Prefix: 192.168.1.0 / $cidr", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Prefix Length: /$cidr", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    onClick = { if (cidr > 24) cidr-- },
                    shape = RoundedCornerShape(6.dp)
                ) { Text("-") }
                Spacer(modifier = Modifier.width(6.dp))
                Button(
                    onClick = { if (cidr < 30) cidr++ },
                    shape = RoundedCornerShape(6.dp)
                ) { Text("+") }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val hostBits = 32 - cidr
            val totalAddresses = 1 shl hostBits
            val usableHosts = (totalAddresses - 2).coerceAtLeast(0)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "Host Bits (h): $hostBits bits", color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
                Text(text = "Total Block Size: $totalAddresses IPs", color = Color(0xFFF59E0B), fontSize = 12.sp)
                Text(text = "Usable Host Range: $usableHosts hosts ($totalAddresses - 2)", color = Color(0xFF10B981), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = "Subnet Mask: 255.255.255.${256 - totalAddresses}", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
            }
        }
    }
}
