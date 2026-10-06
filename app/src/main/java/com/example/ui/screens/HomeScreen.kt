package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AutomataPresets
import com.example.ui.components.AutomatonCanvas
import com.example.ui.components.MachineDisplayMode
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.NeonBlue
import com.example.viewmodel.AppScreen
import com.example.viewmodel.AutomataViewModel

@Composable
fun HomeScreen(
    viewModel: AutomataViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hero Header Badge
        Surface(
            color = ElectricPurple.copy(alpha = 0.18f),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, ElectricPurple.copy(alpha = 0.4f)),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = CyanAccent,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "AUTOMATA THEORY SIMULATOR",
                    color = CyanAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        // Main Title
        Text(
            text = "Moore → Mealy",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Text(
            text = "“Build it. See it. Convert it.”",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = CyanAccent,
            modifier = Modifier.padding(top = 4.dp, bottom = 6.dp)
        )

        Text(
            text = "Create a Moore machine, watch the automaton come alive, and convert it into a Mealy machine with step-by-step visual proof.",
            fontSize = 13.5.sp,
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(0.92f)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Animated Mini Automaton Preview Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .shadow(16.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AutomatonCanvas(
                    modifier = Modifier.fillMaxSize(),
                    states = AutomataPresets.collegeExample.states.take(2),
                    transitions = listOf(
                        AutomataPresets.collegeExample.transitions[0], // q0 --0--> q1
                        AutomataPresets.collegeExample.transitions[3]  // q1 --1--> q0
                    ),
                    mode = MachineDisplayMode.MOORE,
                    initialState = "q0"
                )

                // Overlay tag
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        "Live Interactive Canvas",
                        color = Color(0xFFCBD5E1),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons: "+ Create Machine" & "Try Example"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Button(
                onClick = { viewModel.navigateTo(AppScreen.BUILD_MACHINE) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("home_create_machine_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricPurple
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("+ Create Machine", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = {
                    viewModel.loadPreset(AutomataPresets.collegeExample)
                    viewModel.navigateTo(AppScreen.CONVERT)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("home_try_example_btn"),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, CyanAccent),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = CyanAccent
                )
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Try Example", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Three Feature Cards: Build, Visualize, Convert
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FeatureInfoCard(
                modifier = Modifier.weight(1f),
                title = "Build",
                description = "Create your own machine with states & transitions.",
                icon = Icons.Default.Build,
                accentColor = ElectricPurple,
                onClick = { viewModel.navigateTo(AppScreen.BUILD_MACHINE) }
            )

            FeatureInfoCard(
                modifier = Modifier.weight(1f),
                title = "Visualize",
                description = "See your automaton diagram automatically rearrange.",
                icon = Icons.Default.AutoAwesome,
                accentColor = CyanAccent,
                onClick = { viewModel.navigateTo(AppScreen.AUTOMATON) }
            )

            FeatureInfoCard(
                modifier = Modifier.weight(1f),
                title = "Convert",
                description = "Turn Moore into Mealy with animated migration.",
                icon = Icons.Default.SyncAlt,
                accentColor = NeonBlue,
                onClick = { viewModel.navigateTo(AppScreen.CONVERT) }
            )
        }
    }
}

@Composable
private fun FeatureInfoCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E)),
        border = BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                fontSize = 11.5.sp,
                color = Color(0xFF94A3B8),
                lineHeight = 15.sp
            )
        }
    }
}
