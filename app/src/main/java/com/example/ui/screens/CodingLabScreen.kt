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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.ScreenDestination
import com.example.ui.theme.*

@Composable
fun CodingLabScreen(viewModel: MainViewModel) {
    val presets = listOf(
        "Pointers & Post-Increment" to """
            #include <stdio.h>
            int main() {
                int arr[] = {10, 20, 30, 40};
                int *p = arr;
                printf("%d ", *p++);
                printf("%d ", (*p)++);
                printf("%d ", *p);
                return 0;
            }
        """.trimIndent(),
        "Recursion with Static Variable" to """
            #include <stdio.h>
            int fun(int n) {
                static int x = 1;
                if (n <= 1) return 1;
                x += 1;
                return fun(n - 1) + x;
            }
            int main() {
                printf("%d", fun(4));
                return 0;
            }
        """.trimIndent(),
        "Binary Tree Traversal" to """
            // Inorder: Left -> Root -> Right
            // Preorder: Root -> Left -> Right
            // Postorder: Left -> Right -> Root
            // GATE Note: Unique tree requires Inorder + (Preorder OR Postorder)!
        """.trimIndent()
    )

    var selectedPresetIndex by remember { mutableIntStateOf(0) }
    var codeText by remember { mutableStateOf(presets[0].second) }
    var executionOutput by remember { mutableStateOf<String?>(null) }
    var complexitySummary by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeep)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            Text(
                text = "C Programming & Stack Tracing Sandbox",
                fontWeight = FontWeight.Black,
                color = Color.White,
                fontSize = 20.sp
            )
            Text(
                text = "Simulate Pointer Arithmetic, Recursion Call Stacks & Asymptotic Complexities",
                color = TextSecondaryDark,
                fontSize = 12.sp
            )
        }

        // Preset Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(presets.indices.toList()) { idx ->
                    FilterChip(
                        selected = selectedPresetIndex == idx,
                        onClick = {
                            selectedPresetIndex = idx
                            codeText = presets[idx].second
                            executionOutput = null
                            complexitySummary = null
                        },
                        label = { Text(presets[idx].first, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricCyan,
                            selectedLabelColor = NavyDeep
                        )
                    )
                }
            }
        }

        // Code Editor Box
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "C Language Code (GATE Syntax)", color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(text = "ANSI C99", color = TextSecondaryDark, fontSize = 10.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = codeText,
                        onValueChange = { codeText = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = NeonMint,
                            unfocusedTextColor = NeonMint,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = NavySurface,
                            unfocusedContainerColor = NavySurface
                        ),
                        textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                        minLines = 7
                    )
                }
            }
        }

        // Action Controls
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (selectedPresetIndex == 0) {
                            executionOutput = "Output: 10 20 21\n\nExecution Step Trace:\n1. printf('%d', *p++) -> Prints 10, then advances p to point to arr[1] (20).\n2. printf('%d', (*p)++) -> Value at p (20) printed, then element in arr modified from 20 to 21!\n3. printf('%d', *p) -> Dereferences modified arr[1] = 21."
                            complexitySummary = "Time Complexity: O(1) • Space Complexity: O(1)"
                        } else if (selectedPresetIndex == 1) {
                            executionOutput = "Output: 13\n\nRecursion Stack Trace:\nfun(4) calls fun(3), fun(2), fun(1).\nStatic variable x retains state across all recursive frames (x increments on each call)!"
                            complexitySummary = "Time Complexity: O(n) • Stack Space: O(n) Aux Space"
                        } else {
                            executionOutput = "Binary Tree Traversal Property Verified."
                            complexitySummary = "Time Complexity: O(n) • Space Complexity: O(h)"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = NavyDeep),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Trace Execution", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        viewModel.sendTutorMessage("Please explain this C code step-by-step with recursion/pointer stack diagram:\n$codeText")
                        viewModel.navigateTo(ScreenDestination.AITutor)
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricCyan),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Ask AI Tutor", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Trace Results
        if (executionOutput != null) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NeonMint.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "Simulation & Stack Trace Result:", fontWeight = FontWeight.Bold, color = NeonMint, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = executionOutput ?: "",
                            color = TextPrimaryDark,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        if (complexitySummary != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ElectricBlue.copy(alpha = 0.2f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = complexitySummary ?: "",
                                    color = ElectricCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
