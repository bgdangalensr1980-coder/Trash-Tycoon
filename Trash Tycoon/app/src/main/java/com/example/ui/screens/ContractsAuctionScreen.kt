package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.GameSimulationEngine
import com.example.model.RecyclingContract
import com.example.ui.theme.*

@Composable
fun ContractsAuctionScreen(
    engine: GameSimulationEngine,
    modifier: Modifier = Modifier
) {
    var activeSubTab by remember { mutableStateOf(0) } // 0 = Contracts, 1 = Live Auction

    val contracts by engine.activeContracts.collectAsState()
    val liveAuction by engine.liveAuction.collectAsState()
    val money by engine.money.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SleekBackground)
            .padding(14.dp)
    ) {
        // Tab Header Pill Selector
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SleekCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (activeSubTab == 0) SleekTeal else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeSubTab = 0 }
                        .testTag("tab_contracts")
                ) {
                    Text(
                        text = "Муниципальные Контракты",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activeSubTab == 0) SleekTextWhite else SleekTextMuted,
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                        maxLines = 1
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (activeSubTab == 1) SleekTeal else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeSubTab = 1 }
                        .testTag("tab_auction")
                ) {
                    Text(
                        text = "Аукционы ГосТендеров",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activeSubTab == 1) SleekTextWhite else SleekTextMuted,
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (activeSubTab == 0) {
            // Contracts List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(contracts) { contract ->
                    ContractCard(contract = contract)
                }
            }
        } else {
            // Live Auction Arena
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    if (liveAuction != null) {
                        val auc = liveAuction!!
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SleekDarkCard),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (auc.isPlayerLeading) EcoEmeraldLight else SleekDarkCardBorder
                            ),
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "LIVE АУКЦИОН ГОСТЕНДЕРА",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.sp,
                                        color = EcoEmeraldLight
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (auc.secondsRemaining < 8) DangerRed.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.3f)
                                    ) {
                                        Text(
                                            text = "⏱️ ${auc.secondsRemaining} сек",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (auc.secondsRemaining < 8) DangerRed else SleekTextWhite,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = auc.title,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekTextWhite
                                )
                                Text(
                                    text = "Объём лота: ${auc.volumeTons.toInt()} т ${auc.wasteType.displayName} | Оценка: $${String.format("%,.0f", auc.baseEstimatedValue)}",
                                    fontSize = 12.sp,
                                    color = SleekTextLight
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Current High Bid Card
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = SleekDarkSurface,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "ТЕКУЩАЯ ЛИДИРУЮЩАЯ СТАВКА",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            letterSpacing = 0.5.sp,
                                            color = SleekTextLight
                                        )
                                        Text(
                                            text = "$${String.format("%,.0f", auc.currentHighBid)}",
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontFamily = FontFamily.Monospace,
                                            color = SafetyAmberLight
                                        )
                                        Text(
                                            text = "Лидер: ${auc.currentLeaderName}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (auc.isPlayerLeading) EcoEmeraldLight else CyberCyan
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Competitors list
                                Text(
                                    text = "УЧАСТНИКИ ТОРГОВ",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp,
                                    color = SleekTextLight
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                auc.competitorBidders.forEach { bidder ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "🏢 ${bidder.name}", fontSize = 12.sp, color = SleekTextWhite)
                                        Text(
                                            text = "Бюджет: до $${(bidder.maxBudget / 1000).toInt()}k",
                                            fontSize = 11.sp,
                                            color = SleekTextLight
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                if (!auc.isClosed) {
                                    // Bidding Buttons
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Button(
                                            onClick = { engine.placeAuctionBid(auc.currentHighBid + 2000.0) },
                                            colors = ButtonDefaults.buttonColors(containerColor = SleekTealLight),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("bid_2000_btn")
                                        ) {
                                            Text(
                                                text = "+$2,000",
                                                color = SleekTextWhite,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                        }

                                        Button(
                                            onClick = { engine.placeAuctionBid(auc.currentHighBid + 5000.0) },
                                            colors = ButtonDefaults.buttonColors(containerColor = EcoEmerald),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("bid_5000_btn")
                                        ) {
                                            Text(
                                                text = "+$5,000",
                                                color = SleekTextWhite,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                } else {
                                    // Auction Closed Result
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (auc.isWonByPlayer) EcoEmeraldDark else DangerRed.copy(alpha = 0.3f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = if (auc.isWonByPlayer) "🎉 ВЫ ПОБЕДИЛИ В АУКЦИОНЕ! Контракт добавлен на доску."
                                            else "Торги закрыты. Победил конкурент.",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SleekTextWhite,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Button(
                                        onClick = { engine.startNewAuction() },
                                        colors = ButtonDefaults.buttonColors(containerColor = SleekTealLight),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("request_new_auction_btn")
                                    ) {
                                        Text(
                                            text = "Запросить новый аукцион госзаказа",
                                            color = SleekTextWhite,
                                            fontWeight = FontWeight.Bold
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
}

@Composable
private fun ContractCard(contract: RecyclingContract) {
    val progress = (contract.deliveredTons / contract.requiredTons).coerceIn(0f, 1f)

    Card(
        colors = CardDefaults.cardColors(containerColor = SleekCard),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (contract.isCompleted) EcoEmerald else if (contract.isFailed) DangerRed else SleekCardBorder
        ),
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
                Column {
                    Text(
                        text = contract.clientName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextDark
                    )
                    Text(
                        text = "${contract.clientType} • ${contract.targetType.displayName}",
                        fontSize = 11.sp,
                        color = SleekTextMuted
                    )
                }

                Text(
                    text = "+$${String.format("%,.0f", contract.payoutTotal)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    color = SleekTeal
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (contract.isCompleted) EcoEmerald else SleekTeal,
                trackColor = SleekBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Поставлено: ${String.format("%.1f", contract.deliveredTons)} / ${contract.requiredTons.toInt()} т (${(progress * 100).toInt()}%)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = SleekTextDark
                )
                Text(
                    text = "Осталось: ${contract.remainingGameHours.toInt()} ч",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (contract.remainingGameHours < 8f) DangerRed else SleekTextMuted
                )
            }
        }
    }
}
