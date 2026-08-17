package com.ecuador.recicla

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

enum class MaterialCategory(val spanish: String, val basePricePerKg: Double, val defaultCo2Factor: Double) {
    PLASTIC("Plástico ♻️", 0.38, 1.8),
    PAPER("Cartón y Papel 📦", 0.12, 0.9),
    METAL("Aluminio / Metales 🥫", 1.10, 3.2),
    GLASS("Vidrio / Botellas 🍾", 0.03, 0.4),
    ORGANIC("Orgánico 🌱", 0.00, 0.2)
}

data class SampleItem(val name: String, val averageWeightGrams: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    onAddImpact: (co2SavedGrams: Double, weightGrams: Double, cashEarnedUsd: Double) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(MaterialCategory.PLASTIC) }
    var quantity by remember { mutableStateOf(0) } // Starts at 0, as explicitly requested!
    var customItemName by remember { mutableStateOf("") }
    var customUnitWeight by remember { mutableStateOf("20") }
    
    // Sample items for convenience
    val samples = remember(selectedCategory) {
        when (selectedCategory) {
            MaterialCategory.PLASTIC -> listOf(
                SampleItem("Botella PET 500ml", 20),
                SampleItem("Galón de plástico rígido", 75),
                SampleItem("Tapa plástica pequeña", 3),
                SampleItem("Bolsa tipo supermercado", 8)
            )
            MaterialCategory.PAPER -> listOf(
                SampleItem("Caja de cartón promedio", 250),
                SampleItem("Periódico completo", 150),
                SampleItem("Revista / Catálogo", 120),
                SampleItem("Hojas sueltas (x10)", 45)
            )
            MaterialCategory.METAL -> listOf(
                SampleItem("Lata de aluminio de soda", 15),
                SampleItem("Lata de conservas", 40),
                SampleItem("Papel aluminio doméstico (rollo)", 180)
            )
            MaterialCategory.GLASS -> listOf(
                SampleItem("Botella retornable de cerveza", 350),
                SampleItem("Frasco de conserva de vidrio", 180),
                SampleItem("Vaso de vidrio roto", 120)
            )
            MaterialCategory.ORGANIC -> listOf(
                SampleItem("Cáscaras de frutas/verduras (lote)", 500),
                SampleItem("Restos de café molido", 100),
                SampleItem("Hojas del jardín y césped", 400)
            )
        }
    }

    var selectedSampleIndex by remember { mutableStateOf(0) }
    
    // Update fields when sample changes
    LaunchedEffect(selectedCategory, selectedSampleIndex) {
        if (selectedSampleIndex < samples.size) {
            val sample = samples[selectedSampleIndex]
            customItemName = sample.name
            customUnitWeight = sample.averageWeightGrams.toString()
        }
    }
    
    // Calculate live dynamic totals
    val unitW = customUnitWeight.toIntOrNull() ?: 0
    val totalWeightGrams = unitW * quantity
    val totalCo2Grams = totalWeightGrams * selectedCategory.defaultCo2Factor
    val totalEarningsUsd = (totalWeightGrams / 1000.0) * selectedCategory.basePricePerKg
    
    val co2OffsetKg = totalCo2Grams / 1000.0
    val equivalentKm = co2OffsetKg * 5.0 // Factor de movilidad: 1kg CO2 = 5km de conducción evitada
    val equivalentTreesRef = (co2OffsetKg * 0.15).toInt() // Referencia de árboles respirando
    
    var showSuccessDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020617))
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Applet Banner Header style
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🔋 Calculadora Ambiental & Reembolso",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Monitorea tu ahorro ambiental en tiempo real y estima el valor de ganancia en puntos de acopio de plástico y cartón en Ecuador.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Section 1: Selector de Categoría
        Column {
            Text("1. Selecciona el Tipo de Residuo:", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                var expanded by remember { mutableStateOf(false) }
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White,
                            containerColor = Color(0xFF0F172A)
                        ),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Text(selectedCategory.spanish, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.weight(1f))
                        Icon(Icons.Default.ArrowDropDown, "expand")
                    }
                    
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color(0xFF0F172A)).border(1.dp, Color(0xFF1E293B))
                    ) {
                        MaterialCategory.values().forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.spanish, color = Color.White, fontSize = 14.sp) },
                                onClick = {
                                    selectedCategory = cat
                                    selectedSampleIndex = 0
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Section 2: Detalle del residuo
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("2. Escoge el elemento o escribe peso unitario:", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            
            // Chips list of samples
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                samples.forEachIndexed { index, sample ->
                    InputChip(
                        selected = selectedSampleIndex == index,
                        onClick = { selectedSampleIndex = index },
                        label = { Text(sample.name, fontSize = 10.sp, color = if (selectedSampleIndex == index) Color.Black else Color.White) },
                        colors = InputChipDefaults.inputChipColors(
                            selectedContainerColor = Color(0xFF10B981),
                            containerColor = Color(0xFF1E293B)
                        ),
                        border = null
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = customItemName,
                    onValueChange = { customItemName = it },
                    label = { Text("Nombre del Objeto", fontSize = 11.sp) },
                    modifier = Modifier.weight(2f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF10B981)
                    )
                )

                OutlinedTextField(
                    value = customUnitWeight,
                    onValueChange = { customUnitWeight = it },
                    label = { Text("Peso (g)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF10B981)
                    )
                )
            }
        }

        // Section 3: Cantidad de Unidades (Starts at 0 as requested, fully clearable)
        Column {
            Text("3. Cantidad de Unidades:", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A), RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                IconButton(
                    onClick = { quantity = maxOf(0, quantity - 1) },
                    modifier = Modifier.size(44.dp)
                ) {
                    Text("-", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                
                // Allow users to empty out the value to type an exact number starting from 0
                OutlinedTextField(
                    value = if (quantity == 0) "" else quantity.toString(),
                    onValueChange = { input ->
                        if (input.isEmpty()) {
                            quantity = 0
                        } else {
                            input.toIntOrNull()?.let {
                                if (it in 0..1000) quantity = it
                            }
                        }
                    },
                    placeholder = { Text("0", textAlign = TextAlign.Center, color = Color.Gray, modifier = Modifier.fillMaxWidth()) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .width(100.dp)
                        .padding(horizontal = 8.dp)
                )

                IconButton(
                    onClick = { quantity += 1 },
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                }
            }
        }

        // Section 4: Visualización de Cálculos
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1329)),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                
                // Reducción calculada
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Reducción CO₂ Calculada:", color = Color.Gray, fontSize = 12.sp)
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = String.format(Locale.US, "%.1f g", totalCo2Grams),
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Peso total: $totalWeightGrams g",
                            color = Color.LightGray,
                            fontSize = 10.sp
                        )
                    }
                }
                
                Divider(color = Color(0xFF1E293B))

                // GANANCIA ESTIMADA ECUADOR (PLÁSTICOS Y CARTÓN)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Ganancia Estimada (Ecuador):", color = Color.Gray, fontSize = 12.sp)
                        Text(
                            text = String.format(Locale.US, "Precio acopio: $%.2f / Kg", selectedCategory.basePricePerKg),
                            color = Color.DarkGray,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        if (selectedCategory.basePricePerKg > 0) {
                            Text(
                                text = String.format(Locale.US, "$%.3f USD", totalEarningsUsd),
                                color = Color(0xFF06B6D4),
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "por recolectores o centros",
                                color = Color.Gray,
                                fontSize = 10.sp
                            )
                        } else {
                            Text(
                                text = "Abono Orgánico 🌱",
                                color = Color(0xFFA3E635),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "No comercializable",
                                color = Color.Gray,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Divider(color = Color(0xFF1E293B))

                // Fórmulas equivalentes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🚗 Equiv. Auto", fontSize = 9.sp, color = Color.Gray)
                        Text(String.format(Locale.US, "%.2f km", equivalentKm), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🌿 Árboles / Día", fontSize = 9.sp, color = Color.Gray)
                        Text("$equivalentTreesRef Árboles", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⏳ Degradación", fontSize = 9.sp, color = Color.Gray)
                        val degStr = when (selectedCategory) {
                            MaterialCategory.PLASTIC -> "450 años"
                            MaterialCategory.GLASS -> "4000 años"
                            MaterialCategory.METAL -> "150 años"
                            MaterialCategory.PAPER -> "1 año"
                            MaterialCategory.ORGANIC -> "4 semanas"
                        }
                        Text(degStr, color = Color(0xFFFCA5A5), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Button to register the impact
        Button(
            onClick = {
                if (quantity > 0) {
                    onAddImpact(totalCo2Grams, totalWeightGrams.toDouble(), totalEarningsUsd)
                    showSuccessDialog = true
                }
            },
            enabled = quantity > 0,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Registrar Aporte al Historial", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("¡Aporte Registrado! 🎉", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Text(
                    text = "Registraste $quantity unidad(es) de \"$customItemName\" (~$totalWeightGrams g).\n\n" +
                            "Evitaste liberar ${String.format(Locale.US, "%.1f", totalCo2Grams)}g de CO₂ en la atmósfera ecuatoriana.\n" +
                            if (selectedCategory.basePricePerKg > 0) {
                                "Estímulo de reembolso estimado: \$${String.format(Locale.US, "%.3f", totalEarningsUsd)} USD en tu centro de reciclaje local."
                            } else {
                                "¡Incrementaste el humus de la tierra mediante abono!"
                            },
                    color = Color.LightGray
                )
            },
            confirmButton = {
                TextButton(onClick = { 
                    showSuccessDialog = false 
                    quantity = 0 // Reset after registry
                }) {
                    Text("Entendido", color = Color(0xFF10B981))
                }
            },
            containerColor = Color(0xFF0F172A)
        )
    }
}
