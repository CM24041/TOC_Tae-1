package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AutomatonCanvas
import com.example.ui.components.MachineDisplayMode
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CardNavyBorder
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.EmeraldGreen
import com.example.viewmodel.AutomataViewModel

@Composable
fun AutomatonScreen(
    viewModel: AutomataViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Moore, 1: Mealy

    val currentMode = if (selectedTab == 0) MachineDisplayMode.MOORE else MachineDisplayMode.MEALY

    // Active simulated state for pulse effect
    val activeState = if (uiState.activeSimulationIndex >= 0 && uiState.simulationResultMoore != null) {
        val seq = if (selectedTab == 0) uiState.simulationResultMoore?.stateSequence else uiState.simulationResultMealy?.stateSequence
        seq?.getOrNull(uiState.activeSimulationIndex)
    } else null

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
                    text = "Interactive Automaton",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Tap any state node to inspect its outputs and connections.",
                    fontSize = 12.5.sp,
                    color = CyanAccent
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Toggle Tab: Moore Machine vs Mealy Machine
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF131B2E),
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = if (selectedTab == 0) ElectricPurple else CyanAccent
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        "Moore Diagram",
                        color = if (selectedTab == 0) Color.White else Color(0xFF94A3B8),
                        fontWeight = FontWeight.Bold
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        "Mealy Diagram",
                        color = if (selectedTab == 1) CyanAccent else Color(0xFF94A3B8),
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Large Automaton Canvas (Full visual hero area)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            border = BorderStroke(1.dp, CardNavyBorder)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AutomatonCanvas(
                    modifier = Modifier.fillMaxSize(),
                    states = uiState.states,
                    transitions = uiState.transitions,
                    mealyTransitions = uiState.mealyTransitions,
                    mode = currentMode,
                    initialState = uiState.initialState,
                    selectedStateName = uiState.selectedStateName,
                    activePulseState = activeState,
                    onStateClicked = { viewModel.selectState(it) }
                )

                // Drag gesture hint
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Text(
                        "Drag nodes to rearrange • Tap to inspect",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Selected State Information Inspector Card
        uiState.selectedStateName?.let { stateName ->
            val node = uiState.states.find { it.name == stateName }
            val incoming = uiState.transitions.filter { it.toState == stateName }
            val outgoing = uiState.transitions.filter { it.fromState == stateName }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                border = BorderStroke(1.dp, CyanAccent)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(CyanAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(stateName, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "State Details: $stateName",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            color = ElectricPurple.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, ElectricPurple)
                        ) {
                            Text(
                                text = "Moore Output: ${node?.output ?: "0"}",
                                color = CyanAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Outgoing transitions
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Outgoing:", color = Color(0xFF94A3B8), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            if (outgoing.isEmpty()) {
                                Text("None", color = Color(0xFF64748B), fontSize = 11.5.sp)
                            } else {
                                outgoing.forEach { t ->
                                    Text("input ${t.inputSymbol} → ${t.toState}", color = Color.White, fontSize = 12.sp)
                                }
                            }
                        }

                        // Incoming transitions
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Incoming:", color = Color(0xFF94A3B8), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            if (incoming.isEmpty()) {
                                Text("None", color = Color(0xFF64748B), fontSize = 11.5.sp)
                            } else {
                                incoming.forEach { t ->
                                    Text("from ${t.fromState} (${t.inputSymbol})", color = Color.White, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Live String Simulator Tool
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            border = BorderStroke(1.dp, CardNavyBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Live String Simulator",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Input a binary string and test both machines step-by-step.",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = uiState.simulationInput,
                        onValueChange = { viewModel.setSimulationInput(it) },
                        placeholder = { Text("e.g. 0101") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { viewModel.runSimulation() },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(54.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF0F172A))
                        Text("Simulate", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { viewModel.stepSimulation() },
                        enabled = uiState.simulationResultMoore != null
                    ) {
                        Icon(Icons.Default.FastForward, contentDescription = "Step", tint = CyanAccent)
                    }
                }

                // Results view
                if (uiState.simulationResultMoore != null) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = Color(0xFF131B2E),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFF1E293B)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Input Length: ${uiState.simulationInput.length}", color = Color(0xFF94A3B8), fontSize = 12.sp)
                                Text("Initial: ${uiState.initialState}", color = EmeraldGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                "Moore Output: ${uiState.simulationResultMoore?.outputString}",
                                color = ElectricPurple,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Mealy Output: ${uiState.simulationResultMealy?.outputString}",
                                color = CyanAccent,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Note: For input length n, Moore produces (n+1) outputs (initial state output included), whereas Mealy produces exactly n outputs.",
                                color = Color(0xFF64748B),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
