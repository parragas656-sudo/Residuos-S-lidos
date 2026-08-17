package com.ecuador.recicla

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EcuadorReciclaAppTheme {
                MainAppScreen()
            }
        }
    }
}

enum class Screen {
    SCANNER,
    CALCULATOR,
    HEATMAP
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen() {
    var currentScreen by remember { mutableStateOf(Screen.CALCULATOR) }
    
    // Global accumulated stats to mirror the web app's tracking
    var totalCo2Saved by remember { mutableStateOf(0.0) }
    var totalWeightGrams by remember { mutableStateOf(0.0) }
    var totalEarningsUsd by remember { mutableStateOf(0.0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            text = "EcuRecicla IA", 
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF10B981)
                        )
                        Text(
                            text = "Guardián de la Pachamama", 
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0F172A),
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentScreen == Screen.SCANNER,
                    onClick = { currentScreen = Screen.SCANNER },
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Escanear") },
                    label = { Text("Escanear", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF10B981),
                        selectedTextColor = Color(0xFF10B981),
                        unselectedIconColor = Color.LightGray,
                        unselectedTextColor = Color.LightGray,
                        indicatorColor = Color(0xFF1E293B)
                    )
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.CALCULATOR,
                    onClick = { currentScreen = Screen.CALCULATOR },
                    icon = { Icon(Icons.Default.Info, contentDescription = "Calculadora") },
                    label = { Text("Calculadora", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF06B6D4),
                        selectedTextColor = Color(0xFF06B6D4),
                        unselectedIconColor = Color.LightGray,
                        unselectedTextColor = Color.LightGray,
                        indicatorColor = Color(0xFF1E293B)
                    )
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.HEATMAP,
                    onClick = { currentScreen = Screen.HEATMAP },
                    icon = { Icon(Icons.Default.LocationOn, contentDescription = "Mapa") },
                    label = { Text("Mapa Ecuador", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFFBBF24),
                        selectedTextColor = Color(0xFFFBBF24),
                        unselectedIconColor = Color.LightGray,
                        unselectedTextColor = Color.LightGray,
                        indicatorColor = Color(0xFF1E293B)
                    )
                )
            }
        },
        containerColor = Color(0xFF020617)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentScreen) {
                Screen.SCANNER -> CameraScanScreen(
                    onAddImpact = { co2, weight, cash ->
                        totalCo2Saved += co2
                        totalWeightGrams += weight
                        totalEarningsUsd += cash
                    }
                )
                Screen.CALCULATOR -> CalculatorScreen(
                    onAddImpact = { co2, weight, cash ->
                        totalCo2Saved += co2
                        totalWeightGrams += weight
                        totalEarningsUsd += cash
                    }
                )
                Screen.HEATMAP -> EcuadorMapScreen()
            }
        }
    }
}

@Composable
fun EcuadorReciclaAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF10B981),
            secondary = Color(0xFF06B6D4),
            background = Color(0xFF020617),
            surface = Color(0xFF0F172A),
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = Color.White,
            onSurface = Color.White
        ),
        content = content
    )
}
