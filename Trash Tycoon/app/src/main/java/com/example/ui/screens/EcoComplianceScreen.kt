package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.GameSimulationEngine
import com.example.ui.theme.*

@Composable
fun EcoComplianceScreen(
    engine: GameSimulationEngine,
    modifier: Modifier = Modifier
) {
    val ecoRating by engine.ecoRating.collectAsState()
    val violations by engine.ecoViolations.collectAsState()
    val money by engine.money.collectAsState()

    val fineRiskPercent = ((100f - ecoRating) * 1.15f).coerceIn(5f, 95f)

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
                    text = "Экологический Надзор",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekTextDark
                )
                Text(
                    text = "Контроль токсичных выбросов, фильтрата и инспекции",
                    fontSize = 12.sp,
                    color = SleekTextMuted
                )
            }
        }

        // Eco Status Overview Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SleekCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ЭКО-РЕЙТИНГ ЗАВОДА",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = SleekTextMuted
                        )
                        Text(
                            text = "${ecoRating.toInt()}% / 100%",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            color = if (ecoRating > 75f) EcoEmerald else if (ecoRating > 45f) SafetyAmber else DangerRed
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = (ecoRating / 100f).coerceIn(0f, 1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = if (ecoRating > 75f) EcoEmerald else if (ecoRating > 45f) SafetyAmber else DangerRed,
                        trackColor = SleekBackground
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Риск внеплановой проверки:", fontSize = 12.sp, color = SleekTextDark)
                        Text(
                            text = "${fineRiskPercent.toInt()}% (Штрафы до $50k)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (fineRiskPercent > 50f) DangerRed else EcoEmerald
                        )
                    }
                }
            }
        }

        // Environmental Investments & Remediation
        item {
            Text(
                text = "ПРИРОДООХРАННЫЕ МЕРОПРИЯТИЯ",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = SleekTextMuted
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SleekCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    EcoActionRow(
                        title = "Мембранная гидроизоляция грунта",
                        description = "Устраняет просачивание токсичного фильтрата (+15% к Эко)",
                        cost = 6500.0,
                        onBuy = {
                            if (money >= 6500.0) {
                                engine.money.value -= 6500.0
                                engine.ecoRating.value = (engine.ecoRating.value + 15f).coerceAtMost(100f)
                                engine.postAlert("🌱 Уложена защитная мембрана (+15% Эко)!")
                            }
                        }
                    )

                    HorizontalDivider(color = SleekCardBorder)

                    EcoActionRow(
                        title = "Монтаж вихревых газоочистных скрубберов",
                        description = "Очищает 99% сернистых газов и запахов сжигания (+20% к Эко)",
                        cost = 12000.0,
                        onBuy = {
                            if (money >= 12000.0) {
                                engine.money.value -= 12000.0
                                engine.ecoRating.value = (engine.ecoRating.value + 20f).coerceAtMost(100f)
                                engine.postAlert("🌱 Газоочистные скрубберы запущены (+20% Эко)!")
                            }
                        }
                    )

                    HorizontalDivider(color = SleekCardBorder)

                    EcoActionRow(
                        title = "Лесопарковая буферная зона & Озеленение",
                        description = "Поглощает шум и пыль, сертификат ISO 14001 (+25% к Эко)",
                        cost = 18000.0,
                        onBuy = {
                            if (money >= 18000.0) {
                                engine.money.value -= 18000.0
                                engine.ecoRating.value = (engine.ecoRating.value + 25f).coerceAtMost(100f)
                                engine.postAlert("🌱 Высажена зеленая буферная зона (+25% Эко)!")
                            }
                        }
                    )
                }
            }
        }

        // Violations Log
        item {
            Text(
                text = "ИСТОРИЯ ПРОВЕРОК И ВЗЫСКАНИЙ",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = SleekTextMuted
            )
        }

        if (violations.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SleekCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✅ Нарушений не зафиксировано. Завод полностью соответствует нормативам!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = EcoEmeraldDark,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(violations) { v ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = SleekCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DangerRed.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = v.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DangerRed)
                            Text(text = v.cause, fontSize = 11.sp, color = SleekTextMuted)
                            Text(text = "Время: ${v.timestampFormatted}", fontSize = 10.sp, color = SleekTextLight)
                        }
                        Text(
                            text = "-$${v.fineAmount.toInt()}",
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            color = DangerRed
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EcoActionRow(
    title: String,
    description: String,
    cost: Double,
    onBuy: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SleekTextDark)
            Text(text = description, fontSize = 11.sp, color = SleekTextMuted)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Button(
            onClick = onBuy,
            colors = ButtonDefaults.buttonColors(containerColor = SleekTeal),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.testTag("buy_eco_${cost.toInt()}")
        ) {
            Text(text = "$${cost.toInt()}", fontSize = 12.sp, color = SleekTextWhite, fontWeight = FontWeight.Bold)
        }
    }
}
