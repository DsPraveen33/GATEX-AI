package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SubjectEntity
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

data class SubjectNode(
    val id: String,
    val shortName: String,
    val fullName: String,
    val mastery: Int,
    val isWeak: Boolean = false,
    val angleDegrees: Double
)

/**
 * Knowledge Constellation View:
 * An interactive neural graph of all 10 GATE CSE subjects surrounding the GATEX AI core,
 * with connection pathways (Digital Logic -> COA -> OS -> DBMS -> CN; Math -> Algo -> PDS; TOC -> CD).
 */
@Composable
fun KnowledgeConstellationCard(
    subjects: List<SubjectEntity>,
    onSelectSubject: (SubjectEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSubjectId by remember { mutableStateOf<String?>(null) }

    // Map 10 GATE CSE Subjects to orbital angle positions
    val nodes = remember(subjects) {
        val defaultList = if (subjects.isNotEmpty()) {
            subjects
        } else {
            listOf(
                SubjectEntity("em", "Engineering Mathematics", "01", "functions", 15.0, "#00E5FF", 1),
                SubjectEntity("dl", "Digital Logic", "02", "memory", 10.0, "#A855F7", 2),
                SubjectEntity("coa", "Computer Organization & Arch", "03", "developer_board", 11.0, "#3B82F6", 3),
                SubjectEntity("pds", "Programming & Data Structures", "04", "data_object", 14.0, "#00E5FF", 4),
                SubjectEntity("algo", "Algorithms", "05", "account_tree", 12.0, "#EC4899", 5),
                SubjectEntity("toc", "Theory of Computation", "06", "insights", 9.0, "#F59E0B", 6),
                SubjectEntity("cd", "Compiler Design", "07", "build", 8.0, "#EF4444", 7),
                SubjectEntity("os", "Operating System", "08", "terminal", 12.0, "#10B981", 8),
                SubjectEntity("dbms", "Databases", "09", "storage", 10.0, "#00E5FF", 9),
                SubjectEntity("cn", "Computer Networks", "10", "lan", 11.0, "#A855F7", 10)
            )
        }

        val shortLabels = mapOf(
            "em" to "Math",
            "dl" to "Digital",
            "coa" to "COA",
            "pds" to "Prog & DS",
            "algo" to "Algo",
            "toc" to "TOC",
            "cd" to "Compiler",
            "os" to "OS",
            "dbms" to "DBMS",
            "cn" to "Networks"
        )

        val defaultMasteryMap = mapOf(
            "em" to 62, "dl" to 78, "coa" to 45, "pds" to 80,
            "algo" to 48, "toc" to 65, "cd" to 52, "os" to 74,
            "dbms" to 68, "cn" to 58
        )

        defaultList.mapIndexed { idx, subj ->
            val angle = (idx * (360.0 / defaultList.size)) - 90.0
            val mastery = defaultMasteryMap[subj.id] ?: 65
            SubjectNode(
                id = subj.id,
                shortName = shortLabels[subj.id] ?: subj.name.take(6),
                fullName = subj.name,
                mastery = mastery,
                isWeak = mastery < 55,
                angleDegrees = angle
            )
        }
    }

    val activeSelectedNode = remember(selectedSubjectId, nodes) {
        nodes.find { it.id == selectedSubjectId } ?: nodes.firstOrNull()
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                Brush.horizontalGradient(listOf(RoyalPurple.copy(alpha = 0.5f), ElectricCyan.copy(alpha = 0.5f))),
                RoundedCornerShape(20.dp)
            )
            .testTag("knowledge_constellation_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Hub,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "KNOWLEDGE CONSTELLATION",
                            fontWeight = FontWeight.Black,
                            color = ElectricCyan,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "10 Core Subject Mastery Network",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    color = NavyDark,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Interactive",
                        color = NeonMint,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Canvas Neural Constellation
            ConstellationCanvas(
                nodes = nodes,
                selectedId = selectedSubjectId,
                onSelectNode = { id -> selectedSubjectId = id },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .testTag("constellation_canvas")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Selected Node Details & Launch Action
            activeSelectedNode?.let { node ->
                Surface(
                    color = NavyDark,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, if (node.isWeak) BrightAmber.copy(alpha = 0.5f) else ElectricCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = node.fullName,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                if (node.isWeak) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "⚠ Weak Topic",
                                        color = BrightAmber,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Mastery: ${node.mastery}% • Spaced drills & PYQ readiness active",
                                color = TextSecondaryDark,
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = {
                                val foundSubj = subjects.find { it.id == node.id }
                                    ?: SubjectEntity(node.id, node.fullName, "01", "menu_book", 10.0, "#00E5FF", 1)
                                onSelectSubject(foundSubj)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = if (node.isWeak) BrightAmber else ElectricCyan),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Open",
                                color = NavyDeep,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConstellationCanvas(
    nodes: List<SubjectNode>,
    selectedId: String?,
    onSelectNode: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "constellation_anim")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    BoxWithConstraints(modifier = modifier) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        val center = Offset(width / 2f, height / 2f)
        val orbitRadius = (Math.min(width, height) / 2f) * 0.72f

        // Node positions map
        val nodePositions = remember(nodes, width, height) {
            nodes.map { node ->
                val rad = Math.toRadians(node.angleDegrees)
                val x = center.x + (orbitRadius * cos(rad)).toFloat()
                val y = center.y + (orbitRadius * sin(rad)).toFloat()
                node.id to Offset(x, y)
            }.toMap()
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(nodes.size) {
                    detectTapGestures { tapOffset ->
                        // Check which node is tapped
                        val hitNode = nodePositions.entries.find { (_, pos) ->
                            val dx = tapOffset.x - pos.x
                            val dy = tapOffset.y - pos.y
                            (dx * dx + dy * dy) <= (28.dp.toPx() * 28.dp.toPx())
                        }
                        if (hitNode != null) {
                            onSelectNode(hitNode.key)
                        }
                    }
                }
        ) {
            // 1. Orbital Ring
            drawCircle(
                color = ElectricCyan.copy(alpha = 0.12f),
                center = center,
                radius = orbitRadius,
                style = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f))
            )

            // 2. Inter-Subject Neural Connections
            val connections = listOf(
                "dl" to "coa",
                "coa" to "os",
                "os" to "dbms",
                "dbms" to "cn",
                "em" to "algo",
                "pds" to "algo",
                "toc" to "cd",
                "pds" to "os"
            )

            connections.forEach { (src, dst) ->
                val p1 = nodePositions[src]
                val p2 = nodePositions[dst]
                if (p1 != null && p2 != null) {
                    drawLine(
                        color = ElectricCyan.copy(alpha = 0.25f),
                        start = p1,
                        end = p2,
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }

            // 3. Spoke lines to GATEX AI Center
            nodePositions.values.forEach { p ->
                drawLine(
                    color = RoyalPurple.copy(alpha = 0.3f),
                    start = center,
                    end = p,
                    strokeWidth = 1.2.dp.toPx()
                )
            }

            // 4. Center GATEX Core Node
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(ElectricCyan, RoyalPurple, NavySurface),
                    center = center,
                    radius = 24.dp.toPx()
                ),
                center = center,
                radius = 20.dp.toPx()
            )
            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 24f
                    isAntiAlias = true
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                drawText("✦", center.x, center.y + 8f, paint)
            }

            // 5. Render Subject Nodes
            nodes.forEach { node ->
                val pos = nodePositions[node.id] ?: return@forEach
                val isSelected = node.id == selectedId
                val nodeRadius = if (isSelected) 18.dp.toPx() else 14.dp.toPx()

                val nodeColor = when {
                    node.isWeak -> BrightAmber
                    node.mastery >= 70 -> NeonMint
                    else -> ElectricCyan
                }

                // Pulsing glow for weak or selected nodes
                if (node.isWeak || isSelected) {
                    drawCircle(
                        color = nodeColor.copy(alpha = 0.35f),
                        center = pos,
                        radius = nodeRadius * pulse
                    )
                }

                // Node Body
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(nodeColor, NavyCard),
                        center = pos,
                        radius = nodeRadius
                    ),
                    center = pos,
                    radius = nodeRadius
                )

                // Node Border
                drawCircle(
                    color = if (isSelected) Color.White else nodeColor,
                    center = pos,
                    radius = nodeRadius,
                    style = Stroke(width = if (isSelected) 2.dp.toPx() else 1.2.dp.toPx())
                )

                // Node Short Label Text
                drawContext.canvas.nativeCanvas.apply {
                    val labelPaint = android.graphics.Paint().apply {
                        color = if (isSelected) android.graphics.Color.WHITE else android.graphics.Color.argb(220, 200, 220, 255)
                        textSize = 20f
                        isAntiAlias = true
                        typeface = if (isSelected) android.graphics.Typeface.DEFAULT_BOLD else android.graphics.Typeface.DEFAULT
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    val textY = if (pos.y > center.y) pos.y + nodeRadius + 18f else pos.y - nodeRadius - 6f
                    drawText("${node.shortName} (${node.mastery}%)", pos.x, textY, labelPaint)
                }
            }
        }
    }
}
