package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.practiceQuestions
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CardNavyBorder
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.EmeraldGreen
import com.example.viewmodel.AutomataViewModel

@Composable
fun PracticeScreen(
    viewModel: AutomataViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    val currentQ = practiceQuestions[uiState.currentQuizIndex % practiceQuestions.size]

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
                    text = "Try Yourself",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Quick interactive practice quiz for conversion mastery.",
                    fontSize = 12.5.sp,
                    color = CyanAccent
                )
            }

            Surface(
                color = ElectricPurple.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, ElectricPurple)
            ) {
                Text(
                    text = "Q ${uiState.currentQuizIndex + 1} / ${practiceQuestions.size}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quiz Question Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            border = BorderStroke(1.dp, CardNavyBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Given this Moore Transition:",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Diagram visual box
                Surface(
                    color = Color(0xFF0F172A),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF1E293B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${currentQ.fromState} ── ${currentQ.inputSymbol} ──→ ${currentQ.toState}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("Output of ${currentQ.fromState} = ${currentQ.fromStateOutput}", color = Color(0xFF94A3B8), fontSize = 12.sp)
                            Text("Output of ${currentQ.toState} = ${currentQ.toStateOutput}", color = CyanAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "What will the equivalent Mealy transition be?",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Multiple choice options: A, B, C, D
                currentQ.options.forEachIndexed { index, optionText ->
                    val optionLabels = listOf("A", "B", "C", "D")
                    val isSelected = uiState.quizSelectedOption == index
                    val isCorrect = index == currentQ.correctIndex

                    val borderColor = when {
                        !uiState.quizIsAnswered -> if (isSelected) CyanAccent else Color(0xFF334155)
                        isCorrect -> EmeraldGreen
                        isSelected -> Color(0xFFEF4444)
                        else -> Color(0xFF1E293B)
                    }

                    val bgColor = when {
                        !uiState.quizIsAnswered -> if (isSelected) CyanAccent.copy(alpha = 0.15f) else Color(0xFF161F33)
                        isCorrect -> EmeraldGreen.copy(alpha = 0.18f)
                        isSelected -> Color(0xFFEF4444).copy(alpha = 0.18f)
                        else -> Color(0xFF161F33)
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(enabled = !uiState.quizIsAnswered) {
                                viewModel.selectQuizOption(index)
                            }
                            .testTag("quiz_opt_$index"),
                        color = bgColor,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.5.dp, borderColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1E293B)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    optionLabels.getOrElse(index) { "$index" },
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "${currentQ.fromState} ── $optionText ──→ ${currentQ.toState}",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )

                            if (uiState.quizIsAnswered) {
                                if (isCorrect) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Correct", tint = EmeraldGreen)
                                } else if (isSelected) {
                                    Icon(Icons.Default.Close, contentDescription = "Wrong", tint = Color(0xFFEF4444))
                                }
                            }
                        }
                    }
                }

                // Feedback section
                if (uiState.quizIsAnswered) {
                    val wasCorrect = uiState.quizSelectedOption == currentQ.correctIndex

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        color = if (wasCorrect) EmeraldGreen.copy(alpha = 0.15f) else Color(0xFFF97316).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (wasCorrect) EmeraldGreen else Color(0xFFF97316)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (wasCorrect) "✓ Correct!" else "Not quite 🙂",
                                color = if (wasCorrect) EmeraldGreen else Color(0xFFF97316),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = currentQ.explanation,
                                color = Color(0xFFCBD5E1),
                                fontSize = 13.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.nextQuizQuestion(practiceQuestions.size) },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("quiz_next_btn")
                    ) {
                        Text("Next Question", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null)
                    }
                }
            }
        }
    }
}
