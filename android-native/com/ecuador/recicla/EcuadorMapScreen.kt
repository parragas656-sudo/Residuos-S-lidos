package com.ecuador.recicla

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

data class MapLocation(
    val id: String,
    val name: String,
    val material: String,
    val weightGrams: Int,
    val co2SavedGrams: Double,
    val category: MaterialCategory,
    val xRatio: Float, // Ratio across map width (0f to 1f)
    val yRatio: Float  // Ratio across map height (0f to 1f)
)

@Composable
fun EcuadorMapScreen() {
    // Initial hotspot locations mapping to major cities in Ecuador
    val locations = remember {
        listOf(
            MapLocation("1", "Guayaquil (Puerto)", "Botellas PET de agua", 240, 432.0, MaterialCategory.PLASTIC, 0.28f, 0.68f),
            MapLocation("2", "Quito (Centro Histórico)", "Cajas de cartón corrugado", 1500, 1350.0, MaterialCategory.PAPER, 0.44f, 0.32f),
            MapLocation("3", "Cuenca (El Barranco)", "Botes de aluminio de refrescos", 450, 1440.0, MaterialCategory.METAL, 0.46f, 0.78f),
            MapLocation("4", "Esmeraldas (Playa Las Palmas)", "Envases plásticos de playa", 800, 1440.0, MaterialCategory.PLASTIC, 0.29f, 0.16f),
            MapLocation("5", "Manta (Puerto Pesquero)", "Sacos de papel de harinas", 320, 288.0, MaterialCategory.PAPER, 0.17f, 0.45f)
        )
    }

    var selectedLocation by remember { mutableStateOf<MapLocation?>(null) }
    var currentMapMode by remember { mutableStateOf("heatmap") } // "heatmap" or "pins"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020617))
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // Map header filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "🗺️ Mapa de Calor de Residuos",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 15.sp
                    )
                    Text(
                        "Ubicación geográfica de reportes en Ecuador",
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                }
                
                // Toggle mode button
                Row(
                    modifier = Modifier
                        .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                        .padding(4.dp)
                ) {
                    Text(
                        text = "Calor",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (currentMapMode == "heatmap") Color(0xFF10B981) else Color.Gray,
                        modifier = Modifier
                            .clickable { currentMapMode = "heatmap" }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    Text(
                        text = "Marcador",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (currentMapMode == "pins") Color(0xFF06B6D4) else Color.Gray,
                        modifier = Modifier
                            .clickable { currentMapMode = "pins" }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Map drawing canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFF080F21), RoundedCornerShape(20.dp))
                    .border(2.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp))
                    .clickable { selectedLocation = null } // Clear selection
            ) {
                // Background geography canvas drawing
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Draw Oceans ocean-contour gradient simulator
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF38BDF8).copy(alpha = 0.08f), Color.Transparent),
                            center = Offset(w * 0.2f, h * 0.6f),
                            radius = w * 0.8f
                        )
                    )

                    // Draw 1. COSTA REGION PATH
                    val costaPath = Path().apply {
                        moveTo(w * 0.28f, h * 0.05f)
                        quadraticTo(w * 0.22f, h * 0.15f, w * 0.18f, h * 0.28f)
                        quadraticTo(w * 0.12f, h * 0.38f, w * 0.14f, h * 0.52f)
                        quadraticTo(w * 0.05f, h * 0.65f, w * 0.11f, h * 0.72f)
                        quadraticTo(w * 0.19f, h * 0.75f, w * 0.25f, h * 0.70f)
                        quadraticTo(w * 0.28f, h * 0.55f, w * 0.32f, h * 0.38f)
                        quadraticTo(w * 0.34f, h * 0.15f, w * 0.34f, h * 0.05f)
                        close()
                    }
                    drawPath(
                        path = costaPath,
                        brush = Brush.verticalGradient(listOf(Color(0xFF16A34A), Color(0xFFDE7706)))
                    )

                    // Draw 2. SIERRA REGION PATH
                    val sierraPath = Path().apply {
                        moveTo(w * 0.34f, h * 0.05f)
                        lineTo(w * 0.32f, h * 0.38f)
                        lineTo(w * 0.25f, h * 0.70f)
                        quadraticTo(w * 0.30f, h * 0.85f, w * 0.38f, h * 0.92f)
                        lineTo(w * 0.44f, h * 0.88f)
                        quadraticTo(w * 0.46f, h * 0.58f, w * 0.46f, h * 0.32f)
                        lineTo(w * 0.42f, h * 0.05f)
                        close()
                    }
                    drawPath(
                        path = sierraPath,
                        brush = Brush.verticalGradient(listOf(Color(0xFF78350F), Color(0xFFB45309)))
                    )

                    // Draw 3. ORIENTE REGION PATH
                    val orientePath = Path().apply {
                        moveTo(w * 0.42f, h * 0.05f)
                        lineTo(w * 0.46f, h * 0.32f)
                        lineTo(w * 0.44f, h * 0.88f)
                        quadraticTo(w * 0.62f, h * 0.80f, w * 0.85f, h * 0.68f)
                        quadraticTo(w * 0.95f, h * 0.50f, w * 0.92f, h * 0.25f)
                        quadraticTo(w * 0.65f, h * 0.20f, w * 0.52f, h * 0.10f)
                        close()
                    }
                    drawPath(
                        path = orientePath,
                        brush = Brush.verticalGradient(listOf(Color(0xFF14532D), Color(0xFF166534)))
                    )

                    // Draw Border boundaries
                    drawPath(path = costaPath, color = Color(0xFF10B981), style = Stroke(width = 1.dp.toPx()))
                    drawPath(path = sierraPath, color = Color(0xFFF59E0B), style = Stroke(width = 1.dp.toPx()))
                    drawPath(path = orientePath, color = Color(0xFF14B8A6), style = Stroke(width = 1.dp.toPx()))

                    // Volcanic Andes Central Spine Line (La Avenida de los Volcanes)
                    val volcanoLinePath = Path().apply {
                        moveTo(w * 0.39f, h * 0.06f)
                        quadraticTo(w * 0.37f, h * 0.35f, w * 0.39f, h * 0.55f)
                        quadraticTo(w * 0.40f, h * 0.72f, w * 0.37f, h * 0.90f)
                    }
                    drawPath(
                        path = volcanoLinePath,
                        color = Color(0xFFEF4444).copy(alpha = 0.6f),
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Draw geographic grid longitude lines
                    val dashThickness = 0.5.dp.toPx()
                    for (xGrid in listOf(0.1f, 0.3f, 0.5f, 0.7f, 0.9f)) {
                        drawLine(
                            color = Color.White.copy(alpha = 0.05f),
                            start = Offset(w * xGrid, 0f),
                            end = Offset(w * xGrid, h),
                            strokeWidth = dashThickness
                        )
                    }
                    for (yGrid in listOf(0.2f, 0.4f, 0.6f, 0.8f)) {
                        drawLine(
                            color = Color.White.copy(alpha = 0.05f),
                            start = Offset(0f, h * yGrid),
                            end = Offset(w, h * yGrid),
                            strokeWidth = dashThickness
                        )
                    }
                    
                    // Draw volcanos
                    drawCircle(Color.White, radius = 4f, center = Offset(w * 0.38f, h * 0.34f)) // Cotopaxi
                    drawCircle(Color.White, radius = 5f, center = Offset(w * 0.37f, h * 0.50f)) // Chimborazo
                }

                // Interactive Render points Layer
                locations.forEach { loc ->
                    val xPos = loc.xRatio
                    val yPos = loc.yRatio
                    
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        // Correct coordinates calculation relative to size
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset(x = 220.dp * xPos, y = 290.dp * yPos) // Approximate view binding size
                        ) {
                            if (currentMapMode == "heatmap") {
                                // Pulsing Heat radiation radial circle
                                Box(
                                    modifier = Modifier
                                        .size(if (selectedLocation?.id == loc.id) 48.dp else 36.dp)
                                        .background(
                                            Brush.radialGradient(
                                                colors = listOf(
                                                    Color(0xFFFF1E56).copy(alpha = 0.85f),
                                                    Color(0xFFFFB000).copy(alpha = 0.4f),
                                                    Color.Transparent
                                                )
                                            ),
                                            shape = CircleShape
                                        )
                                        .align(Alignment.Center)
                                )
                                // Active core dot
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            if (selectedLocation?.id == loc.id) Color(0xFFFBBF24) else Color.White,
                                            shape = CircleShape
                                        )
                                        .align(Alignment.Center)
                                        .clickable { selectedLocation = loc }
                                )
                            } else {
                                // Pins Marker style
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .border(2.dp, Color.White, CircleShape)
                                        .background(
                                            if (selectedLocation?.id == loc.id) Color(0xFF10B981) else Color(0xFF06B6D4),
                                            shape = CircleShape
                                        )
                                        .clickable { selectedLocation = loc }
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }
                }

                // Floating popover with real data and pricing!
                AnimatedVisibility(
                    visible = selectedLocation != null,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    selectedLocation?.let { loc ->
                        val localEarnings = (loc.weightGrams / 1000.0) * loc.category.basePricePerKg
                        
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.95f)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = loc.category.spanish,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10B981)
                                    )
                                    IconButton(
                                        onClick = { selectedLocation = null },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(Icons.Default.Close, "close", tint = Color.LightGray, modifier = Modifier.size(14.dp))
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Waste category emoji avatar bubble
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val emoji = when (loc.category) {
                                            MaterialCategory.PLASTIC -> "🥤"
                                            MaterialCategory.PAPER -> "📦"
                                            MaterialCategory.METAL -> "🥫"
                                            MaterialCategory.GLASS -> "🍾"
                                            MaterialCategory.ORGANIC -> "🌱"
                                        }
                                        Text(emoji, fontSize = 20.sp)
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(loc.material, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                                        Text("Ubicación: ${loc.name}", fontSize = 9.sp, color = Color.Gray)
                                    }
                                }

                                Divider(color = Color.White.copy(alpha = 0.05f))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Peso total registrado:", fontSize = 8.sp, color = Color.Gray)
                                        Text("${loc.weightGrams} g (${String.format(Locale.US, "%.2f", loc.weightGrams / 1000.0)} Kg)", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 10.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Reembolso Estimado:", fontSize = 8.sp, color = Color.Gray)
                                        if (loc.category.basePricePerKg > 0) {
                                            Text(
                                                text = String.format(Locale.US, "$%.3f USD", localEarnings),
                                                color = Color(0xFF06B6D4),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        } else {
                                            Text("Compost 🌱", color = Color.Lime, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier2 = Modifier.padding(top = 2.dp)
                                ) {
                                    Text(
                                        text = "♻️ Ahorro CO₂: ${loc.co2SavedGrams.toInt()}g",
                                        color = Color(0xFF10B981),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = "Movilidad: ~${String.format(Locale.US, "%.1f", (loc.co2SavedGrams / 1000.0) * 5.0)} Km",
                                        color = Color.LightGray,
                                        fontSize = 9.sp
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

// Minimal compatibility spacer attribute for helper layout padding
private val Modifier.modifier2: Modifier get() = this
