package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AutomatonCanvas
import com.example.ui.components.MachineDisplayMode
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CardNavyBorder
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonBlue
import com.example.viewmodel.AutomataViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ConvertScreen(
    viewModel: AutomataViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var hasConvertedAnimPlayed by remember { mutableStateOf(false) }

    val currentStep = uiState.mealyTransitions.getOrNull(uiState.conversionStepIndex)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Convert Moore → Mealy",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Output shifts from state nodes directly onto transition arrows.",
                    fontSize = 12.5.sp,
                    color = CyanAccent
                )
            }

            // Copy Table action
            IconButton(
                onClick = {
                    val sb = StringBuilder()
                    sb.appendLine("=== MOORE MACHINE ===")
                    sb.appendLine("State\tOutput")
                    uiState.states.forEach { sb.appendLine("${it.name}\t${it.output}") }
                    sb.appendLine("\nTransitions:")
                    uiState.transitions.forEach { sb.appendLine("${it.fromState} -- ${it.inputSymbol} --> ${it.toState}") }
                    sb.appendLine("\n=== MEALY MACHINE ===")
                    sb.appendLine("From\tInput/Output\tTo")
                    uiState.mealyTransitions.forEach { sb.appendLine("${it.fromState}\t${it.inputSymbol}/${it.outputSymbol}\t${it.toState}") }

                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Automata Table", sb.toString())
                    clipboard.setPrimaryClip(clip)
                    viewModel.showToast("Copied Moore & Mealy tables to clipboard!")
                },
                modifier = Modifier.testTag("copy_table_btn")
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Table", tint = CyanAccent)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Large ⚡ CONVERT Button with glowing gradient
        Button(
            onClick = {
                viewModel.startConversionAnimation()
                hasConvertedAnimPlayed = true
                viewModel.showToast("✓ Conversion complete: Moore is now Mealy!")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("main_convert_btn"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ElectricPurple
            )
        ) {
            Icon(Icons.Default.Bolt, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "⚡ CONVERT MACHINE",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Conversion Core Rule Box
        Surface(
            color = Color(0xFF131B2E),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CyanAccent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("★", color = CyanAccent, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        "The Golden Rule:",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Destination State Output → Transition Output",
                        color = CyanAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BEFORE (Moore) vs AFTER (Mealy) Side-by-Side Canvas Visualizer
        Text(
            text = "Before vs After Visual Comparison",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // BEFORE - MOORE
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                border = BorderStroke(1.dp, CardNavyBorder)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AutomatonCanvas(
                        modifier = Modifier.fillMaxSize(),
                        states = uiState.states,
                        transitions = uiState.transitions,
                        mode = MachineDisplayMode.MOORE,
                        initialState = uiState.initialState,
                        highlightedTransitionIndex = uiState.conversionStepIndex
                    )

                    Surface(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .padding(6.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Text(
                            "BEFORE: Moore (q / out)",
                            color = ElectricPurple,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // AFTER - MEALY
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                border = BorderStroke(1.dp, CardNavyBorder)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AutomatonCanvas(
                        modifier = Modifier.fillMaxSize(),
                        states = uiState.states,
                        mealyTransitions = uiState.mealyTransitions,
                        mode = MachineDisplayMode.MEALY,
                        initialState = uiState.initialState,
                        highlightedTransitionIndex = uiState.conversionStepIndex
                    )

                    Surface(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .padding(6.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Text(
                            "AFTER: Mealy (in / out)",
                            color = CyanAccent,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step-By-Step Interactive Player
        if (uiState.mealyTransitions.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                border = BorderStroke(1.dp, CardNavyBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Step-By-Step Conversion Player",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "Step ${uiState.conversionStepIndex + 1} of ${uiState.mealyTransitions.size}",
                            fontSize = 12.sp,
                            color = CyanAccent,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    currentStep?.let { step ->
                        // Step animation explanation card
                        Surface(
                            color = Color(0xFF161F33),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, ElectricPurple.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${step.fromState} ── ${step.inputSymbol} ──→ ${step.toState}",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Surface(
                                        color = CyanAccent.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "Mealy: ${step.inputSymbol}/${step.outputSymbol}",
                                            color = CyanAccent,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = step.explanation,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.5.sp,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Player controls: Prev, Next
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.previousConversionStep() },
                            enabled = uiState.conversionStepIndex > 0,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Previous")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Previous")
                        }

                        Button(
                            onClick = { viewModel.nextConversionStep() },
                            enabled = uiState.conversionStepIndex < uiState.mealyTransitions.size - 1,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
                        ) {
                            Text("Next Step", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = "Next", tint = Color(0xFF0F172A))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Side-by-Side Transition Table
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            border = BorderStroke(1.dp, CardNavyBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Before vs After Mapping Table",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E293B), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text("Moore Transition", color = Color(0xFFCBD5E1), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.1f))
                    Text("Dest. Out", color = Color(0xFFCBD5E1), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
                    Text("Mealy Transition", color = CyanAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
                }

                Spacer(modifier = Modifier.height(4.dp))

                uiState.mealyTransitions.forEachIndexed { i, m ->
                    val isCurrent = i == uiState.conversionStepIndex
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isCurrent) ElectricPurple.copy(alpha = 0.2f) else Color.Transparent,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${m.fromState} ── ${m.inputSymbol} ──→ ${m.toState}",
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1.1f)
                        )
                        Text(
                            text = "[ ${m.outputSymbol} ]",
                            color = CyanAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(0.7f),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "${m.fromState} ── ${m.inputSymbol}/${m.outputSymbol} ──→ ${m.toState}",
                            color = EmeraldGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1.1f),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}
