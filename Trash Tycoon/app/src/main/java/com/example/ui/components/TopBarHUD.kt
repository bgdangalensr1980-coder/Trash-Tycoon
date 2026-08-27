package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.GameSimulationEngine
import com.example.ui.theme.*

@Composable
fun TopBarHUD(
    engine: GameSimulationEngine,
    modifier: Modifier = Modifier
) {
    val money by engine.money.collectAsState()
    val ecoRating by engine.ecoRating.collectAsState()
    val gameHours by engine.gameHours.collectAsState()
    val alertMessages by engine.alertMessages.collectAsState()
    val totalTons by engine.totalTonsRecycled.collectAsState()

    val hoursInt = gameHours.toInt()
    val minsInt = ((gameHours - hoursInt) * 60).toInt()
    val timeFormatted = String.format("%02d:%02d", hoursInt, minsInt)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)),
        color = SleekTeal,
        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 14.dp)
        ) {
            // Main row with branding and financial counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand / Hub Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(EcoEmeraldLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Recycling,
                            contentDescription = "Recycling Icon",
                            tint = SleekTealDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "ScrapMaster Pro",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekTextWhite,
                            lineHeight = 18.sp
                        )
                        Text(
                            text = "INDUSTRIAL HUB #42 • $timeFormatted",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SleekTextWhite.copy(alpha = 0.8f),
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                // Balance display
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.testTag("hud_money_card")
                ) {
                    Text(
                        text = "$${String.format("%,.0f", money)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = SleekTextWhite
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Trending",
                            tint = Color(0xFFA7F3D0),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "${totalTons.toInt()}т переработано",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFA7F3D0)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Eco Progress & Stats Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Eco bar
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Black.copy(alpha = 0.25f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction = (ecoRating / 100f).coerceIn(0.05f, 1f))
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (ecoRating > 60f) EcoEmeraldLight
                                else if (ecoRating > 30f) SafetyAmberLight
                                else DangerRed
                            )
                    )
                }

                // Eco label badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.testTag("hud_eco_card")
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = "Eco",
                        tint = SafetyAmberLight,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${ecoRating.toInt()}% ECO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextWhite
                    )
                }
            }

            // Alert banner
            AnimatedVisibility(
                visible = alertMessages.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val topAlert = alertMessages.firstOrNull()
                if (topAlert != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.35f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Уведомление",
                                tint = SafetyAmberLight,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = topAlert,
                                color = SleekTextWhite,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
