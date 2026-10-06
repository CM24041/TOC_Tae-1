package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppSidebar
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.AutomatonScreen
import com.example.ui.screens.BuildMachineScreen
import com.example.ui.screens.ConvertScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PracticeScreen
import com.example.ui.screens.TheoryScreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DeepNavyBg
import com.example.ui.theme.MooreMealyTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.AutomataViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: AutomataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsState()

            MooreMealyTheme(darkTheme = uiState.isDarkMode) {
                // BackHandler returns to Home if on another screen
                BackHandler(enabled = uiState.currentScreen != AppScreen.HOME) {
                    viewModel.navigateTo(AppScreen.HOME)
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing),
                    containerColor = if (uiState.isDarkMode) DeepNavyBg else Color(0xFFF1F5F9)
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Main Layout: Left Sidebar + Right Screen Content
                        Row(modifier = Modifier.fillMaxSize()) {
                            AppSidebar(
                                currentScreen = uiState.currentScreen,
                                isExpanded = uiState.isSidebarExpanded,
                                isDarkMode = uiState.isDarkMode,
                                onNavigate = { viewModel.navigateTo(it) },
                                onToggleExpand = { viewModel.toggleSidebar() },
                                onToggleDarkMode = { viewModel.toggleDarkMode() }
                            )

                            // Main Screen Area
                            Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                                when (uiState.currentScreen) {
                                    AppScreen.HOME -> HomeScreen(viewModel = viewModel)
                                    AppScreen.BUILD_MACHINE -> BuildMachineScreen(viewModel = viewModel)
                                    AppScreen.CONVERT -> ConvertScreen(viewModel = viewModel)
                                    AppScreen.AUTOMATON -> AutomatonScreen(viewModel = viewModel)
                                    AppScreen.THEORY -> TheoryScreen()
                                    AppScreen.PRACTICE -> PracticeScreen(viewModel = viewModel)
                                    AppScreen.ABOUT -> AboutScreen()
                                }
                            }
                        }

                        // Floating Toast Message
                        uiState.toastMessage?.let { toast ->
                            LaunchedEffect(toast) {
                                kotlinx.coroutines.delay(2800)
                                viewModel.dismissToast()
                            }
                            AnimatedVisibility(
                                visible = true,
                                enter = slideInVertically { it } + fadeIn(),
                                exit = slideOutVertically { it } + fadeOut(),
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 16.dp)
                            ) {
                                Surface(
                                    color = Color(0xFF1E293B),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, CyanAccent),
                                    tonalElevation = 8.dp
                                ) {
                                    Text(
                                        text = toast,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
