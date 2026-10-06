package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CardNavyBorder
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricPurple
import com.example.viewmodel.AppScreen

data class NavItem(
    val screen: AppScreen,
    val title: String,
    val icon: ImageVector,
    val testTag: String
)

@Composable
fun AppSidebar(
    modifier: Modifier = Modifier,
    currentScreen: AppScreen,
    isExpanded: Boolean,
    isDarkMode: Boolean,
    onNavigate: (AppScreen) -> Unit,
    onToggleExpand: () -> Unit,
    onToggleDarkMode: () -> Unit
) {
    val navItems = listOf(
        NavItem(AppScreen.HOME, "Home", Icons.Default.Home, "nav_home"),
        NavItem(AppScreen.BUILD_MACHINE, "Build Machine", Icons.Default.Build, "nav_build"),
        NavItem(AppScreen.CONVERT, "Convert", Icons.Default.SyncAlt, "nav_convert"),
        NavItem(AppScreen.AUTOMATON, "Automaton", Icons.Default.AutoAwesome, "nav_automaton"),
        NavItem(AppScreen.THEORY, "Theory", Icons.Default.Psychology, "nav_theory"),
        NavItem(AppScreen.PRACTICE, "Practice", Icons.Default.Science, "nav_practice"),
        NavItem(AppScreen.ABOUT, "About", Icons.Default.Info, "nav_about")
    )

    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(if (isExpanded) 230.dp else 72.dp)
            .testTag("app_sidebar"),
        color = CardNavy,
        tonalElevation = 6.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardNavyBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header / Brand
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = if (isExpanded) Arrangement.SpaceBetween else Arrangement.Center
                ) {
                    if (isExpanded) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(ElectricPurple),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "M",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    "Moore → Mealy",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Automata Tool",
                                    color = CyanAccent,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onToggleExpand,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("toggle_sidebar_btn")
                    ) {
                        Icon(
                            if (isExpanded) Icons.Default.MenuOpen else Icons.Default.Menu,
                            contentDescription = "Toggle Sidebar",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Navigation items
                navItems.forEach { item ->
                    val isSelected = currentScreen == item.screen
                    val bg = if (isSelected) ElectricPurple.copy(alpha = 0.25f) else Color.Transparent
                    val border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, ElectricPurple) else null
                    val contentColor = if (isSelected) CyanAccent else Color(0xFFCBD5E1)

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onNavigate(item.screen) }
                            .testTag(item.testTag),
                        color = bg,
                        shape = RoundedCornerShape(10.dp),
                        border = border
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = if (isExpanded) Arrangement.Start else Arrangement.Center
                        ) {
                            Icon(
                                item.icon,
                                contentDescription = item.title,
                                tint = contentColor,
                                modifier = Modifier.size(22.dp)
                            )
                            if (isExpanded) {
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    item.title,
                                    color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                    fontSize = 13.5.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // Bottom section: Dark Mode Toggle
            Column {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onToggleDarkMode() }
                        .testTag("toggle_theme_btn"),
                    color = Color(0xFF161F33),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = if (isExpanded) Arrangement.Start else Arrangement.Center
                    ) {
                        Icon(
                            if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = "Toggle Dark Mode",
                            tint = if (isDarkMode) Color(0xFFFACC15) else Color(0xFF38BDF8),
                            modifier = Modifier.size(20.dp)
                        )
                        if (isExpanded) {
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                if (isDarkMode) "Dark Mode" else "Light Mode",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
