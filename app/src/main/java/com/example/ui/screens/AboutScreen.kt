package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CardNavyBorder
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.EmeraldGreen

@Composable
fun AboutScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "About the Project",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Automata Machine Converter & Interactive Simulator",
            fontSize = 13.sp,
            color = CyanAccent,
            modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            border = BorderStroke(1.dp, CardNavyBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ElectricPurple.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, tint = ElectricPurple)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("College Practical Project", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Theory of Computation & Formal Languages", fontSize = 12.sp, color = Color(0xFF94A3B8))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Built specifically as an interactive visual aid for college demonstrations. " +
                            "Instead of calculating transitions manually on a whiteboard, this app allows students " +
                            "and professors to see states dynamically plotted, verify equivalent Mealy outputs, " +
                            "and simulate binary input strings with real-time feedback.",
                    color = Color(0xFFCBD5E1),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Key Features Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            border = BorderStroke(1.dp, CardNavyBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Core Capabilities:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                FeatureBullet("Automated State Diagram: Renders nodes, curves, and self-loops on an interactive canvas.")
                FeatureBullet("One-Click Conversion: Maps Moore outputs onto transition arrows using destination states.")
                FeatureBullet("Side-by-Side Comparison: Dual canvas rendering of both Moore and Mealy representations.")
                FeatureBullet("Step-by-Step Player: Explains each transition in plain, conversational English.")
                FeatureBullet("Live String Simulator: Traces binary inputs with real-time state glow pulses.")
            }
        }
    }
}

@Composable
private fun FeatureBullet(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text("•", color = CyanAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = Color(0xFF94A3B8), fontSize = 12.5.sp, lineHeight = 16.sp)
    }
}
