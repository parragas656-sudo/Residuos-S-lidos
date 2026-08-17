package com.ecuador.recicla

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun CameraScanScreen(
    onAddImpact: (co2SavedGrams: Double, weightGrams: Double, cashEarnedUsd: Double) -> Unit
) {
    var isScanning by remember { mutableStateOf(false) }
    var scanResult by remember { mutableStateOf<MapLocation?>(null) }
    val scope = rememberCoroutineScope()

    // Database of funny random waste objects of Ecuador to scan
    val scanPool = remember {
        listOf(
            MapLocation("s1", "Punto de Escaneo", "Botella Plástica PET de Coca-Cola (3L)", 54, 97.2, MaterialCategory.PLASTIC, 0f, 0f),
            MapLocation("s2", "Punto de Escaneo", "Caja Grande de Cartón de Banano de Exportación", 420, 378.0, MaterialCategory.PAPER, 0f, 0f),
            MapLocation("s3", "Punto de Escaneo", "Lata de Conserva de Atún Real", 45, 144.0, MaterialCategory.METAL, 0f, 0f),
            MapLocation("s4", "Punto de Escaneo", "Frasco de Vidrio de Café Buendía", 210, 84.0, MaterialCategory.GLASS, 0f, 0f),
            MapLocation("s5", "Punto de Escaneo", "Envase soplado de Champú Vacío", 35, 63.0, MaterialCategory.PLASTIC, 0f, 0f),
            MapLocation("s6", "Punto de Escaneo", "Hojas de cuaderno escolares desechas", 70, 63.0, MaterialCategory.PAPER, 0f, 0f)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020617))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Screen Header details
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "📷 Escáner de Residuos con IA",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 15.sp
                )
                Text(
                    "Simulador de análisis inteligente de residuos por cámara",
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }

            // Camera viewport box frame
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFF0F172A), RoundedCornerShape(24.dp))
                    .border(2.dp, Color(0xFF1E293B), RoundedCornerShape(24.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isScanning) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = Color(0xFF10B981))
                        Text(
                            text = "Analizando material con IA...",
                            color = Color.LightGray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Identificando polímeros y fibras reciclables",
                            color = Color.DarkGray,
                            fontSize = 10.sp
                        )
                    }
                } else if (scanResult != null) {
                    // Resulting display layout
                    val res = scanResult!!
                    val localEarnings = (res.weightGrams / 1000.0) * res.category.basePricePerKg

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF020617)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .border(1.dp, Color(0xFF1D283A), RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "¡Análisis Exitoso! 🧬",
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = res.category.spanish,
                                    color = Color(0xFF06B6D4),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }

                            Divider(color = Color(0xFF1D283A))

                            Text(
                                text = res.material,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = Color.White
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Peso aproximado:", fontSize = 9.sp, color = Color.Gray)
                                    Text("${res.weightGrams} g", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Reembolso de mercado:", fontSize = 9.sp, color = Color.Gray)
                                    if (res.category.basePricePerKg > 0) {
                                        Text(
                                            text = String.format(Locale.US, "$%.3f USD", localEarnings),
                                            color = Color(0xFF06B6D4),
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    } else {
                                        Text("Abono 🌱", color = Color.Lime, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Factor CO₂:", fontSize = 9.sp, color = Color.Gray)
                                    Text("${res.category.defaultCo2Factor}x", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("CO₂ evitado estimado:", fontSize = 9.sp, color = Color.Gray)
                                    Text("${res.co2SavedGrams.toInt()} g", color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                }
                            }

                            Button(
                                onClick = {
                                    onAddImpact(res.co2SavedGrams, res.weightGrams.toDouble(), localEarnings)
                                    scanResult = null
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Aceptar e ingresar a mi huella", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                } else {
                    // Default camera lenses viewport outline
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .border(2.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📷", fontSize = 42.sp)
                        }
                        
                        Text(
                            text = "Apunta la cámara del celular al desecho",
                            textAlign = TextAlign.Center,
                            color = Color.LightGray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Text(
                            text = "Acepta plástico, cartón, papel, vidrios y metales.",
                            textAlign = TextAlign.Center,
                            color = Color.Gray,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Scanner action button panel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                if (!isScanning && scanResult == null) {
                    Button(
                        onClick = {
                            isScanning = true
                            scope.launch {
                                delay(2000) // Simulate AI scanning latency
                                isScanning = false
                                scanResult = scanPool.random()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06B6D4)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Iniciar Escaneo Clínico de IA", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                    }
                } else if (scanResult != null) {
                    OutlinedButton(
                        onClick = { scanResult = null },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Escanear otro objeto", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
