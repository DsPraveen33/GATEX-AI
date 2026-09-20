package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * High-performance, structured message renderer tailored for GATE CSE technical content.
 * Parses:
 * - LaTeX math equations ($$...$$ and inline $...$)
 * - Code blocks (```lang ... ```) with line numbers & copy
 * - Markdown tables (| col1 | col2 |) with responsive horizontal scrolling
 * - Callout boxes (GATE Traps, Key Concepts, Theorems, Step-by-Step proofs)
 * - Complexity notations (O(N), Θ(N), Ω(N))
 * - Formatted bullet & numbered lists
 */
@Composable
fun TechnicalMessageRenderer(
    content: String,
    modifier: Modifier = Modifier,
    onCopyCode: (String) -> Unit = {}
) {
    val sections = remember(content) { parseContentBlocks(content) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        sections.forEach { block ->
            when (block) {
                is ContentBlock.Header -> {
                    TechnicalHeader(
                        title = block.text,
                        level = block.level
                    )
                }

                is ContentBlock.Code -> {
                    EnhancedCodeBlock(
                        language = block.language,
                        code = block.code,
                        onCopy = { onCopyCode(block.code) }
                    )
                }

                is ContentBlock.MathFormula -> {
                    LatexMathCard(
                        formula = block.formula,
                        label = block.label
                    )
                }

                is ContentBlock.Table -> {
                    MarkdownTableCard(
                        headers = block.headers,
                        rows = block.rows
                    )
                }

                is ContentBlock.Callout -> {
                    CalloutBox(
                        type = block.type,
                        title = block.title,
                        message = block.text
                    )
                }

                is ContentBlock.BulletItem -> {
                    BulletPointRow(
                        text = block.text,
                        level = block.indent
                    )
                }

                is ContentBlock.NumberedStep -> {
                    NumberedStepRow(
                        stepNumber = block.number,
                        text = block.text
                    )
                }

                is ContentBlock.Paragraph -> {
                    TechnicalParagraph(text = block.text)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// BLOCK MODELS & PARSING ENGINE
// -------------------------------------------------------------

sealed class ContentBlock {
    data class Header(val text: String, val level: Int) : ContentBlock()
    data class Code(val language: String, val code: String) : ContentBlock()
    data class MathFormula(val formula: String, val label: String? = null) : ContentBlock()
    data class Table(val headers: List<String>, val rows: List<List<String>>) : ContentBlock()
    data class Callout(val type: CalloutType, val title: String, val text: String) : ContentBlock()
    data class BulletItem(val text: String, val indent: Int = 0) : ContentBlock()
    data class NumberedStep(val number: String, val text: String) : ContentBlock()
    data class Paragraph(val text: String) : ContentBlock()
}

enum class CalloutType {
    TRAP,       // ⚠️ GATE Pitfall / Edge Case
    TIP,        // 💡 High-Yield Concept / Key Takeaway
    THEOREM,    // 📜 Theorem / Lemma / Property
    COMPLEXITY  // ⏱️ Asymptotic Analysis
}

private fun parseContentBlocks(rawText: String): List<ContentBlock> {
    val blocks = mutableListOf<ContentBlock>()
    val lines = rawText.split("\n")
    var i = 0

    while (i < lines.size) {
        val line = lines[i]
        val trimmed = line.trim()

        // 1. Skip blank lines
        if (trimmed.isEmpty()) {
            i++
            continue
        }

        // 2. Code Blocks (```lang ... ```)
        if (trimmed.startsWith("```")) {
            val language = trimmed.removePrefix("```").trim().ifEmpty { "CODE" }
            val codeLines = mutableListOf<String>()
            i++
            while (i < lines.size && !lines[i].trim().startsWith("```")) {
                codeLines.add(lines[i])
                i++
            }
            if (i < lines.size && lines[i].trim().startsWith("```")) {
                i++ // consume closing backticks
            }
            blocks.add(ContentBlock.Code(language = language, code = codeLines.joinToString("\n")))
            continue
        }

        // 3. LaTeX Block Formulas ($$...$$ or \[ ... \])
        if (trimmed.startsWith("$$") || trimmed.startsWith("\\[")) {
            if (trimmed.startsWith("$$") && trimmed.endsWith("$$") && trimmed.length > 4) {
                val formula = trimmed.removeSurrounding("$$").trim()
                blocks.add(ContentBlock.MathFormula(formula))
                i++
                continue
            } else if (trimmed.startsWith("\\[") && trimmed.endsWith("\\]") && trimmed.length > 4) {
                val formula = trimmed.removePrefix("\\[").removeSuffix("\\]").trim()
                blocks.add(ContentBlock.MathFormula(formula))
                i++
                continue
            } else {
                // Multi-line formula
                val formulaLines = mutableListOf<String>()
                val startDelimiter = if (trimmed.startsWith("$$")) "$$" else "\\["
                val endDelimiter = if (startDelimiter == "$$") "$$" else "\\]"
                val firstLineContent = trimmed.removePrefix(startDelimiter).trim()
                if (firstLineContent.isNotEmpty()) formulaLines.add(firstLineContent)
                i++
                while (i < lines.size && !lines[i].trim().endsWith(endDelimiter) && !lines[i].trim().contains(endDelimiter)) {
                    formulaLines.add(lines[i])
                    i++
                }
                if (i < lines.size) {
                    val lastLine = lines[i].trim().removeSuffix(endDelimiter).trim()
                    if (lastLine.isNotEmpty()) formulaLines.add(lastLine)
                    i++
                }
                blocks.add(ContentBlock.MathFormula(formula = formulaLines.joinToString("\n")))
                continue
            }
        }

        // 4. Standalone Formula: or Theorem: headers
        if (trimmed.startsWith("Formula:", ignoreCase = true) ||
            trimmed.startsWith("Theorem:", ignoreCase = true) ||
            trimmed.startsWith("Equation:", ignoreCase = true) ||
            trimmed.startsWith("Property:", ignoreCase = true)
        ) {
            val label = trimmed.substringBefore(":").trim()
            val formula = trimmed.substringAfter(":").trim()
            blocks.add(ContentBlock.MathFormula(formula = formula, label = label))
            i++
            continue
        }

        // 5. Markdown Tables (| header1 | header2 |)
        if (trimmed.startsWith("|") && trimmed.endsWith("|") && trimmed.contains("-") && i + 1 < lines.size) {
            // Check if current is header or separator
            // Usually header is line i, separator is line i+1
        }
        if (trimmed.startsWith("|") && trimmed.endsWith("|")) {
            val tableLines = mutableListOf<String>()
            while (i < lines.size && lines[i].trim().startsWith("|") && lines[i].trim().endsWith("|")) {
                tableLines.add(lines[i].trim())
                i++
            }
            if (tableLines.size >= 2) {
                val headerLine = tableLines[0]
                val headers = headerLine.split("|").map { it.trim() }.filter { it.isNotEmpty() }
                val rowLines = tableLines.drop(1).filter { lineContent ->
                    // filter out separator line |---|---|
                    !lineContent.replace("|", "").replace("-", "").replace(":", "").trim().isEmpty()
                }
                val rows = rowLines.map { rowLine ->
                    rowLine.split("|").map { it.trim() }.filter { it.isNotEmpty() }
                }
                if (headers.isNotEmpty()) {
                    blocks.add(ContentBlock.Table(headers = headers, rows = rows))
                    continue
                }
            }
        }

        // 6. Section Headers (#, ##, ###, ####)
        if (trimmed.startsWith("#")) {
            val level = trimmed.takeWhile { it == '#' }.length
            val title = trimmed.removePrefix("#".repeat(level)).trim()
            blocks.add(ContentBlock.Header(text = title, level = level.coerceIn(1, 4)))
            i++
            continue
        }

        // 7. GATE Traps / Edge Cases / Warnings Callout
        if (trimmed.contains("⚠️") ||
            trimmed.startsWith("Trap:", ignoreCase = true) ||
            trimmed.startsWith("GATE Trap", ignoreCase = true) ||
            trimmed.startsWith("Common Pitfall", ignoreCase = true) ||
            trimmed.startsWith("Watch Out", ignoreCase = true) ||
            trimmed.startsWith("Caution:", ignoreCase = true)
        ) {
            val title = if (trimmed.contains("Trap", ignoreCase = true)) "GATE Exam Pitfall" else "Exam Caution"
            blocks.add(ContentBlock.Callout(type = CalloutType.TRAP, title = title, text = trimmed.replace("⚠️", "").trim()))
            i++
            continue
        }

        // 8. Key Concepts / High-Yield Takeaways
        if (trimmed.contains("💡") ||
            trimmed.startsWith("Key Takeaway:", ignoreCase = true) ||
            trimmed.startsWith("Summary:", ignoreCase = true) ||
            trimmed.startsWith("GATE Tip:", ignoreCase = true) ||
            trimmed.startsWith("Pro Tip:", ignoreCase = true)
        ) {
            val title = if (trimmed.contains("Tip", ignoreCase = true)) "GATE Pro-Tip" else "High-Yield Summary"
            blocks.add(ContentBlock.Callout(type = CalloutType.TIP, title = title, text = trimmed.replace("💡", "").trim()))
            i++
            continue
        }

        // 9. Time & Space Complexity Callout
        if (trimmed.startsWith("Complexity:", ignoreCase = true) ||
            trimmed.startsWith("Time Complexity:", ignoreCase = true) ||
            trimmed.startsWith("Space Complexity:", ignoreCase = true)
        ) {
            blocks.add(ContentBlock.Callout(type = CalloutType.COMPLEXITY, title = "Asymptotic Complexity", text = trimmed))
            i++
            continue
        }

        // 10. Numbered Steps (1. , 2. , 1) , Step 1:)
        val stepMatch = Regex("""^(?:Step\s*(\d+)[:\.]?|(\d+)[\.\)])\s*(.*)""", RegexOption.IGNORE_CASE).find(trimmed)
        if (stepMatch != null) {
            val stepNum = stepMatch.groupValues[1].ifEmpty { stepMatch.groupValues[2] }
            val text = stepMatch.groupValues[3]
            blocks.add(ContentBlock.NumberedStep(number = stepNum, text = text))
            i++
            continue
        }

        // 11. Bullet List Items (- , * , •)
        if (trimmed.startsWith("- ") || trimmed.startsWith("* ") || trimmed.startsWith("• ")) {
            val indent = line.takeWhile { it.isWhitespace() }.length / 2
            val text = trimmed.substring(2).trim()
            blocks.add(ContentBlock.BulletItem(text = text, indent = indent.coerceIn(0, 3)))
            i++
            continue
        }

        // 12. Standard Technical Paragraph
        blocks.add(ContentBlock.Paragraph(text = trimmed))
        i++
    }

    return blocks
}

// -------------------------------------------------------------
// UI COMPONENTS FOR STRUCTURED BLOCKS
// -------------------------------------------------------------

@Composable
fun TechnicalHeader(title: String, level: Int) {
    val fontSize = when (level) {
        1 -> 16.sp
        2 -> 14.5.sp
        else -> 13.5.sp
    }
    val fontWeight = FontWeight.Bold

    Column(modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp, if (level <= 2) 18.dp else 14.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        ),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = renderRichMathAnnotated(title),
                fontSize = fontSize,
                fontWeight = fontWeight,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.2.sp
            )
        }
        if (level <= 2) {
            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                thickness = 1.dp
            )
        }
    }
}

@Composable
fun LatexMathCard(formula: String, label: String? = null) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val formattedMath = remember(formula) { formatLatexToReadableMath(formula) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                RoundedCornerShape(12.dp)
            )
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "LaTeX / FORMULA",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    if (label != null) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(formula))
                        Toast.makeText(context, "Formula copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "Copy Formula",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Main Formula Visual Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        RoundedCornerShape(8.dp)
                    )
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = formattedMath,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun MarkdownTableCard(headers: List<String>, rows: List<List<String>>) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant,
                RoundedCornerShape(12.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(8.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                headers.forEach { header ->
                    Box(
                        modifier = Modifier
                            .widthIn(min = 100.dp, max = 220.dp)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = renderRichMathAnnotated(header),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Table Body Rows
            rows.forEachIndexed { index, row ->
                val isEven = index % 2 == 0
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (isEven) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            else Color.Transparent
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    row.forEachIndexed { colIndex, cellText ->
                        Box(
                            modifier = Modifier
                                .widthIn(min = 100.dp, max = 220.dp)
                                .padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = renderRichMathAnnotated(cellText),
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedCodeBlock(language: String, code: String, onCopy: () -> Unit) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF181824)),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF313244), RoundedCornerShape(12.dp))
    ) {
        Column {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF212234))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF38BA8))
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF9E2AF))
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFA6E3A1))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = language.uppercase(),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF89B4FA)
                    )
                }

                TextButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(code))
                        onCopy()
                        copied = true
                        Toast.makeText(context, "Code copied", Toast.LENGTH_SHORT).show()
                    },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = "Copy code",
                        tint = if (copied) Color(0xFFA6E3A1) else Color(0xFFBAC2DE),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (copied) "Copied" else "Copy",
                        fontSize = 11.sp,
                        color = if (copied) Color(0xFFA6E3A1) else Color(0xFFBAC2DE),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Code Content with Line Numbers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(12.dp)
            ) {
                val lines = code.split("\n")
                // Line Numbers Column
                Column(
                    modifier = Modifier.padding(end = 12.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    lines.indices.forEach { index ->
                        Text(
                            text = "${index + 1}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.5.sp,
                            lineHeight = 18.sp,
                            color = Color(0xFF585B70)
                        )
                    }
                }

                // Code Lines Column
                Column {
                    lines.forEach { lineText ->
                        Text(
                            text = lineText,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.5.sp,
                            lineHeight = 18.sp,
                            color = Color(0xFFCDD6F4)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalloutBox(type: CalloutType, title: String, message: String) {
    val (containerColor, borderColor, iconColor, icon) = when (type) {
        CalloutType.TRAP -> Quad(
            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f),
            MaterialTheme.colorScheme.error.copy(alpha = 0.45f),
            MaterialTheme.colorScheme.error,
            Icons.Default.WarningAmber
        )
        CalloutType.TIP -> Quad(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
            MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
            MaterialTheme.colorScheme.primary,
            Icons.Default.Lightbulb
        )
        CalloutType.THEOREM -> Quad(
            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.35f),
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.45f),
            MaterialTheme.colorScheme.tertiary,
            Icons.Default.Functions
        )
        CalloutType.COMPLEXITY -> Quad(
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.45f),
            MaterialTheme.colorScheme.secondary,
            Icons.Default.Timer
        )
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = containerColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = iconColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = renderRichMathAnnotated(message),
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun BulletPointRow(text: String, level: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (level * 12 + 4).dp, top = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = if (level == 0) "•" else "◦",
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 1.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = renderRichMathAnnotated(text),
            fontSize = 13.sp,
            lineHeight = 19.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun NumberedStepRow(stepNumber: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 3.dp, bottom = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(22.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = stepNumber,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = renderRichMathAnnotated(text),
            fontSize = 13.sp,
            lineHeight = 19.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TechnicalParagraph(text: String) {
    Text(
        text = renderRichMathAnnotated(text),
        fontSize = 13.sp,
        lineHeight = 19.5.sp,
        color = MaterialTheme.colorScheme.onSurface
    )
}

// -------------------------------------------------------------
// LATEX & INLINE MARKDOWN PARSING UTILITIES
// -------------------------------------------------------------

/**
 * Converts raw LaTeX math formula strings into clean Unicode mathematical notation.
 * e.g. \sum_{i=1}^{n} i = \frac{n(n+1)}{2} -> ∑(i=1 to n) i = (n(n+1)) / 2
 */
fun formatLatexToReadableMath(raw: String): String {
    var result = raw.trim()

    // Clean surrounding math markers
    result = result.removePrefix("$$").removeSuffix("$$")
    result = result.removePrefix("\\[").removeSuffix("\\]")
    result = result.removePrefix("$").removeSuffix("$")
    result = result.trim()

    val replacements = listOf(
        "\\times" to " × ",
        "\\cdot" to " · ",
        "\\div" to " ÷ ",
        "\\pm" to " ± ",
        "\\le" to " ≤ ",
        "\\leq" to " ≤ ",
        "\\ge" to " ≥ ",
        "\\geq" to " ≥ ",
        "\\neq" to " ≠ ",
        "\\approx" to " ≈ ",
        "\\equiv" to " ≡ ",
        "\\sum" to "∑",
        "\\prod" to "∏",
        "\\int" to "∫",
        "\\infty" to "∞",
        "\\alpha" to "α",
        "\\beta" to "β",
        "\\gamma" to "γ",
        "\\delta" to "δ",
        "\\Delta" to "Δ",
        "\\epsilon" to "ε",
        "\\theta" to "θ",
        "\\lambda" to "λ",
        "\\mu" to "μ",
        "\\pi" to "π",
        "\\sigma" to "σ",
        "\\omega" to "ω",
        "\\Omega" to "Ω",
        "\\Theta" to "Θ",
        "\\in" to " ∈ ",
        "\\notin" to " ∉ ",
        "\\subset" to " ⊂ ",
        "\\subseteq" to " ⊆ ",
        "\\cup" to " ∪ ",
        "\\cap" to " ∩ ",
        "\\forall" to "∀",
        "\\exists" to "∃",
        "\\rightarrow" to " → ",
        "\\to" to " → ",
        "\\leftarrow" to " ← ",
        "\\Rightarrow" to " ⇒ ",
        "\\implies" to " ⇒ ",
        "\\iff" to " ⇔ ",
        "\\log" to "log",
        "\\ln" to "ln",
        "\\lim" to "lim",
        "\\mod" to " mod ",
        "\\{" to "{",
        "\\}" to "}"
    )

    for ((tex, unicode) in replacements) {
        result = result.replace(tex, unicode)
    }

    // Replace \frac{a}{b} with (a / b)
    val fracRegex = Regex("""\\frac\{([^{}]+)\}\{([^{}]+)\}""")
    while (fracRegex.containsMatchIn(result)) {
        result = fracRegex.replace(result) { match ->
            val num = match.groupValues[1]
            val den = match.groupValues[2]
            "($num / $den)"
        }
    }

    // Replace \sqrt{x} with √(x)
    val sqrtRegex = Regex("""\\sqrt\{([^{}]+)\}""")
    result = sqrtRegex.replace(result) { "√(${it.groupValues[1]})" }

    // Superscripts ^2, ^n, ^{exp}
    result = result.replace("^2", "²")
        .replace("^3", "³")
        .replace("^n", "ⁿ")
        .replace("^k", "ᵏ")
        .replace("^0", "⁰")
        .replace("^1", "¹")

    return result
}

/**
 * Parses bold **text**, inline `code`, inline math $math$, and complexity badges.
 */
fun renderRichMathAnnotated(text: String): AnnotatedString {
    val builder = AnnotatedString.Builder()
    // Pattern matches:
    // 1. **bold**
    // 2. `inline code`
    // 3. $inline math$
    // 4. [complexity badge] like O(n), O(1), Theta(n)
    val tokenRegex = Regex("""\*\*(.*?)\*\*|`([^`]+)`|\$([^$]+)\$|(O\([^)]+\)|Θ\([^)]+\)|Ω\([^)]+\))""")
    var lastIndex = 0

    val matches = tokenRegex.findAll(text)
    for (match in matches) {
        if (match.range.first > lastIndex) {
            builder.append(text.substring(lastIndex, match.range.first))
        }

        val bold = match.groups[1]?.value
        val code = match.groups[2]?.value
        val math = match.groups[3]?.value
        val complexity = match.groups[4]?.value

        when {
            bold != null -> {
                builder.pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                builder.append(bold)
                builder.pop()
            }
            code != null -> {
                builder.pushStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        background = Color(0x22808080)
                    )
                )
                builder.append(" $code ")
                builder.pop()
            }
            math != null -> {
                builder.pushStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Medium
                    )
                )
                builder.append(formatLatexToReadableMath(math))
                builder.pop()
            }
            complexity != null -> {
                builder.pushStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                builder.append(complexity)
                builder.pop()
            }
        }

        lastIndex = match.range.last + 1
    }

    if (lastIndex < text.length) {
        builder.append(text.substring(lastIndex))
    }

    return builder.toAnnotatedString()
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
