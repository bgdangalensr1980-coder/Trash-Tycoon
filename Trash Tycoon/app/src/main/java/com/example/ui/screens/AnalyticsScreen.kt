package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.model.ResourceType
import com.example.ui.theme.*
import kotlin.math.max

@Composable
fun AnalyticsScreen(
    engine: GameSimulationEngine,
    modifier: Modifier = Modifier
) {
    val stats by engine.efficiencyStats.collectAsState()
    val inventory by engine.inventory.collectAsState()
    val totalTons by engine.totalTonsRecycled.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SleekBackground)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Аналитика Эффективности (OEE)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekTextDark
                )
                Text(
                    text = "Пропускная способность, чистота фракций и энергозатраты",
                    fontSize = 12.sp,
                    color = SleekTextMuted
                )
            }
        }

        // Key KPI Dashboard Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SleekCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ОБЩАЯ ЭФФЕКТИВНОСТЬ ОБОРУДОВАНИЯ (OEE)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = SleekTeal
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${stats.overallEquipmentEffectiveness}%",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                color = SleekTeal
                            )
                            Text(
                                text = "Статус: Оптимальный режим",
                                fontSize = 11.sp,
                                color = EcoEmeraldDark
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${stats.currentThroughputTonsPerMin} т/мин",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = SleekTextDark
                            )
                            Text(
                                text = "Текущий темп",
                                fontSize = 11.sp,
                                color = SleekTextMuted
                            )
                        }
                    }

                    HorizontalDivider(color = SleekCardBorder)

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${stats.averagePurityPercent}%",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                fontFamily = FontFamily.Monospace,
                                color = SleekTextDark
                            )
                            Text(text = "Чистота партий", fontSize = 10.sp, color = SleekTextMuted)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${stats.energyConsumptionKw.toInt()} кВт",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                fontFamily = FontFamily.Monospace,
                                color = SleekTeal
                            )
                            Text(text = "Энергозатраты", fontSize = 10.sp, color = SleekTextMuted)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${totalTons.toInt()} т",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                fontFamily = FontFamily.Monospace,
                                color = EcoEmeraldDark
                            )
                            Text(text = "Всего за всё время", fontSize = 10.sp, color = SleekTextMuted)
                        }
                    }
                }
            }
        }

        // Live Throughput Trend Chart
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SleekCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ДИНАМИКА ПЕРЕРАБОТКИ (ТОНН / МИНУТУ)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = SleekTextMuted
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SleekDarkCard)
                            .padding(8.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val hist = stats.throughputHistory
                            if (hist.size >= 2) {
                                val maxV = max(hist.maxOrNull() ?: 10f, 1f)
                                val stepX = size.width / (hist.size - 1)
                                val path = Path()
                                val fillPath = Path()

                                hist.forEachIndexed { index, v ->
                                    val px = index * stepX
                                    val py = size.height - (v / maxV * (size.height - 10f))
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

                                drawPath(
                                    path = fillPath,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(SleekTealLight.copy(alpha = 0.35f), Color.Transparent)
                                    )
                                )
                                drawPath(
                                    path = path,
                                    color = EcoEmeraldLight,
                                    style = Stroke(width = 3f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottleneck Analysis Insight
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SleekCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SleekTealMuted),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(EcoEmerald.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Анализ",
                            tint = EcoEmeraldDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Диагностика производственных цепочек",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekTextDark
                        )
                        Text(
                            text = "Линии работают сбалансировано. Скорость конвейеров синхронизирована с гидравлическими прессами.",
                            fontSize = 11.sp,
                            color = SleekTextMuted
                        )
                    }
                }
            }
        }
    }
}
