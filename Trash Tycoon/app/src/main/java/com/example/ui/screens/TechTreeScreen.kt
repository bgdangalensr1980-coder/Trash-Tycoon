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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.GameSimulationEngine
import com.example.model.TechUpgrade
import com.example.ui.theme.*

@Composable
fun TechTreeScreen(
    engine: GameSimulationEngine,
    modifier: Modifier = Modifier
) {
    val techList by engine.techTree.collectAsState()
    val money by engine.money.collectAsState()

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
                    text = "Научно-Исследовательский Центр (НИОКР)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekTextDark
                )
                Text(
                    text = "Прокачка технологий переработки, лазерной оптики и логистики",
                    fontSize = 12.sp,
                    color = SleekTextMuted
                )
            }
        }

        items(techList) { tech ->
            TechUpgradeCard(
                tech = tech,
                canAfford = money >= tech.cost,
                onUnlock = { engine.unlockTech(tech.id) }
            )
        }
    }
}

@Composable
private fun TechUpgradeCard(
    tech: TechUpgrade,
    canAfford: Boolean,
    onUnlock: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (tech.isUnlocked) SleekTealMuted else SleekCard
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (tech.isUnlocked) SleekTeal.copy(alpha = 0.5f) else SleekCardBorder
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (tech.isUnlocked) SleekCard else SleekBackground,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = tech.icon, fontSize = 24.sp)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = tech.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextDark
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SleekTealMuted
                    ) {
                        Text(
                            text = tech.category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekTealDark,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = tech.description,
                    fontSize = 11.sp,
                    color = SleekTextMuted
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (tech.isUnlocked) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EcoEmerald.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "✓ Изучено",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoEmeraldDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    )
                }
            } else {
                Button(
                    onClick = onUnlock,
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(containerColor = SleekTeal),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("unlock_tech_${tech.id}")
                ) {
                    Text(
                        text = "$${tech.cost.toInt()}",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = SleekTextWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
