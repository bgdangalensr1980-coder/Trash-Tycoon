package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.GameSimulationEngine
import com.example.model.MarketCommodity
import com.example.model.ResourceType
import com.example.ui.theme.*
import kotlin.math.max
import kotlin.math.min

@Composable
fun MarketScreen(
    engine: GameSimulationEngine,
    modifier: Modifier = Modifier
) {
    val commodities by engine.marketCommodities.collectAsState()
    val inventory by engine.inventory.collectAsState()
    val money by engine.money.collectAsState()

    var selectedType by remember { mutableStateOf(ResourceType.RECYCLED_STEEL) }
    val activeCommodity = commodities.find { it.type == selectedType } ?: commodities.first()
    val availableStock = inventory[selectedType] ?: 0.0

    var sellAmountTons by remember { mutableStateOf(0.0) }

    LaunchedEffect(availableStock, selectedType) {
        sellAmountTons = min(sellAmountTons, availableStock)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SleekBackground)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Глобальная Биржа Вторсырья",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextDark
                    )
                    Text(
                        text = "Динамические спотовые котировки и биржевые фьючерсы",
                        fontSize = 12.sp,
                        color = SleekTextMuted
                    )
                }
            }
        }

        // Active Commodity Detailed Chart & Trading Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SleekCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(Color(activeCommodity.type.colorHex))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = activeCommodity.type.displayName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekTextDark
                                )
                                Text(
                                    text = activeCommodity.supplyDemandStatus,
                                    fontSize = 11.sp,
                                    color = SleekTextMuted
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "$${String.format("%,.1f", activeCommodity.currentPricePerUnit)} / ${activeCommodity.type.unit}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                color = SleekTextDark
                            )
                            Text(
                                text = if (activeCommodity.priceChange24h >= 0) "+${activeCommodity.priceChange24h}% ▲" else "${activeCommodity.priceChange24h}% ▼",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeCommodity.priceChange24h >= 0) EcoEmerald else DangerRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sleek Dark Price Line Graph Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(SleekDarkCard)
                            .padding(10.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val hist = activeCommodity.priceHistory
                            if (hist.size >= 2) {
                                val minP = (hist.minOrNull() ?: 0.0) * 0.95
                                val maxP = (hist.maxOrNull() ?: 100.0) * 1.05
                                val range = max(maxP - minP, 1.0)

                                val stepX = size.width / (hist.size - 1)
                                val path = Path()
                                val fillPath = Path()

                                hist.forEachIndexed { index, price ->
                                    val px = index * stepX
                                    val py = size.height - ((price - minP) / range * size.height).toFloat()
                                    if (index == 0) {
                                        path.moveTo(px, py)
                                        fillPath.moveTo(px, size.height)
                                        fillPath.lineTo(px, py)
                                    } else {
                                        path.lineTo(px, py)
                                        fillPath.lineTo(px, py)
                                    }
                                }
                                fillPath.lineTo(size.width, size.height)
                                fillPath.close()

                                val graphColor = if (activeCommodity.priceChange24h >= 0) EcoEmeraldLight else DangerRed

                                drawPath(
                                    path = fillPath,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(graphColor.copy(alpha = 0.35f), Color.Transparent)
                                    )
                                )

                                drawPath(
                                    path = path,
                                    color = graphColor,
                                    style = Stroke(width = 3f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Inventory & Selling Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "На складе завода:",
                            fontSize = 12.sp,
                            color = SleekTextMuted
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SleekTealMuted
                        ) {
                            Text(
                                text = "${String.format("%,.1f", availableStock)} ${activeCommodity.type.unit}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekTealDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick percentage buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(0.25 to "25%", 0.50 to "50%", 1.0 to "100%").forEach { (fraction, label) ->
                            OutlinedButton(
                                onClick = {
                                    sellAmountTons = ((availableStock * fraction * 10).toInt() / 10.0)
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = SleekTeal
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Sell Slider
                    if (availableStock > 0.0) {
                        Slider(
                            value = sellAmountTons.toFloat(),
                            onValueChange = { sellAmountTons = (it * 10).toInt() / 10.0 },
                            valueRange = 0f..availableStock.toFloat(),
                            colors = SliderDefaults.colors(
                                thumbColor = SleekTeal,
                                activeTrackColor = SleekTeal
                            ),
                            modifier = Modifier.testTag("slider_sell_amount")
                        )
                    }

                    val totalPayout = sellAmountTons * activeCommodity.currentPricePerUnit

                    Button(
                        onClick = {
                            if (sellAmountTons > 0.0) {
                                engine.sellResource(activeCommodity.type, sellAmountTons)
                                sellAmountTons = 0.0
                            }
                        },
                        enabled = sellAmountTons > 0.0,
                        colors = ButtonDefaults.buttonColors(containerColor = SleekTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("execute_sell_btn")
                    ) {
                        Text(
                            text = "Продать ${String.format("%.1f", sellAmountTons)} ${activeCommodity.type.unit} за +$${String.format("%,.0f", totalPayout)}",
                            color = SleekTextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // List of all market commodities
        item {
            Text(
                text = "ВСЕ ТОРГОВЫЕ ПОЗИЦИИ",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SleekTextMuted,
                letterSpacing = 0.5.sp
            )
        }

        items(commodities) { item ->
            val isSelected = item.type == selectedType
            val inStock = inventory[item.type] ?: 0.0

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) SleekTealMuted else SleekCard
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) SleekTeal else SleekCardBorder
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedType = item.type }
                    .testTag("commodity_item_${item.type.name}")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(item.type.colorHex))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = item.type.displayName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekTextDark
                            )
                            Text(
                                text = "На складе: ${String.format("%.1f", inStock)} ${item.type.unit}",
                                fontSize = 11.sp,
                                color = SleekTextMuted
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$${String.format("%,.1f", item.currentPricePerUnit)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = SleekTextDark
                        )
                        Text(
                            text = if (item.priceChange24h >= 0) "+${item.priceChange24h}%" else "${item.priceChange24h}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (item.priceChange24h >= 0) EcoEmerald else DangerRed
                        )
                    }
                }
            }
        }
    }
}
