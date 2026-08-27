package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.GameSimulationEngine
import com.example.model.ConveyorNode
import com.example.model.MachineNodeType
import com.example.ui.theme.*

@Composable
fun ConveyorEditorScreen(
    engine: GameSimulationEngine,
    modifier: Modifier = Modifier
) {
    val conveyorNodes by engine.conveyorGrid.collectAsState()
    val money by engine.money.collectAsState()
    val scrollState = rememberScrollState()

    var selectedGridCoord by remember { mutableStateOf<Pair<Int, Int>?>(Pair(0, 1)) }
    var selectedShopMachine by remember { mutableStateOf(MachineNodeType.CONVEYOR_BELT) }

    val activeNode = conveyorNodes.find { it.gridX == selectedGridCoord?.first && it.gridY == selectedGridCoord?.second }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SleekBackground)
            .verticalScroll(scrollState)
            .padding(14.dp)
    ) {
        // Header info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Конвейерные Линии & Прессы",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekTextDark
                )
                Text(
                    text = "Маршрутизация, скорость и давление прессования",
                    fontSize = 12.sp,
                    color = SleekTextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 6x5 Factory Grid View
        Card(
            colors = CardDefaults.cardColors(containerColor = SleekCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "СХЕМА ЦЕХА ПЕРЕРАБОТКИ (СЕТКА УЗЛОВ)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = SleekTeal,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                for (gy in 0..4) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(vertical = 3.dp)
                    ) {
                        for (gx in 0..5) {
                            val node = conveyorNodes.find { it.gridX == gx && it.gridY == gy }
                            val isSelected = selectedGridCoord?.first == gx && selectedGridCoord?.second == gy

                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) SleekTealMuted
                                        else if (node != null) SleekDarkCard
                                        else SleekBackground
                                    )
                                    .border(
                                        1.5.dp,
                                        if (isSelected) SleekTeal
                                        else if (node != null) SleekDarkCardBorder
                                        else SleekCardBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        selectedGridCoord = Pair(gx, gy)
                                    }
                                    .testTag("grid_cell_${gx}_${gy}"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (node != null) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = when (node.type) {
                                                MachineNodeType.INTAKE_HOPPER -> "📥"
                                                MachineNodeType.CONVEYOR_BELT -> "➡️"
                                                MachineNodeType.HIGH_SPEED_BELT -> "⚡"
                                                MachineNodeType.PRIMARY_SHREDDER -> "⚙️"
                                                MachineNodeType.MAGNETIC_SEPARATOR -> "🧲"
                                                MachineNodeType.OPTICAL_SORTER -> "🔬"
                                                MachineNodeType.HYDRAULIC_PRESS -> "🗜️"
                                                MachineNodeType.BIO_DIGESTER -> "🌱"
                                                MachineNodeType.HAZMAT_SCRUBBER -> "🧪"
                                                MachineNodeType.PRECIOUS_SMELTER -> "🔥"
                                            },
                                            fontSize = 16.sp,
                                            modifier = if (node.type == MachineNodeType.CONVEYOR_BELT) Modifier.rotate(node.directionDeg.toFloat()) else Modifier
                                        )
                                        if (node.internalBufferTons > 0f) {
                                            Text(
                                                text = "${node.internalBufferTons.toInt()}т",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SafetyAmberLight
                                            )
                                        }
                                    }
                                } else {
                                    Text("+", fontSize = 14.sp, color = SleekTextLight)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Selected Node Configurator Panel
        if (activeNode != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SleekCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SleekTeal.copy(alpha = 0.4f)),
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
                                text = "Узел [${activeNode.gridX}, ${activeNode.gridY}]: ${activeNode.type.displayName}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekTextDark
                            )
                            Text(
                                text = activeNode.type.description,
                                fontSize = 11.sp,
                                color = SleekTextMuted
                            )
                        }

                        IconButton(
                            onClick = {
                                engine.removeConveyorNode(activeNode.gridX, activeNode.gridY)
                            },
                            modifier = Modifier.testTag("demolish_node_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Демонтаж", tint = DangerRed)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Direction Controls
                    Text("Направление передачи:", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = SleekTextDark)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        listOf(0 to "Вправо ➡️", 90 to "Вниз ⬇️", 180 to "Влево ⬅️", 270 to "Вверх ⬆️").forEach { (deg, label) ->
                            val isCurrent = activeNode.directionDeg == deg
                            OutlinedButton(
                                onClick = {
                                    engine.addOrUpdateConveyorNode(activeNode.gridX, activeNode.gridY, activeNode.type, deg)
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isCurrent) SleekTeal else Color.Transparent
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isCurrent) SleekTeal else SleekCardBorder),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isCurrent) SleekTextWhite else SleekTextDark
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Speed Multiplier Slider
                    Text(
                        text = "Скорость узла: ${String.format("%.1f", activeNode.speedMultiplier)}x",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = SleekTextDark
                    )
                    Slider(
                        value = activeNode.speedMultiplier,
                        onValueChange = { newSpeed ->
                            engine.updateNodeParameters(
                                activeNode.gridX,
                                activeNode.gridY,
                                newSpeed,
                                activeNode.pressureBar,
                                activeNode.filterSelectivity
                            )
                        },
                        valueRange = 0.5f..3.5f,
                        steps = 6,
                        colors = SliderDefaults.colors(
                            thumbColor = SleekTeal,
                            activeTrackColor = SleekTeal
                        ),
                        modifier = Modifier.testTag("slider_speed")
                    )

                    // Hydraulic Pressure Slider (for Press)
                    if (activeNode.type == MachineNodeType.HYDRAULIC_PRESS || activeNode.type == MachineNodeType.PRIMARY_SHREDDER) {
                        Text(
                            text = "Давление гидравлики: ${activeNode.pressureBar.toInt()} Бар",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = SleekTextDark
                        )
                        Slider(
                            value = activeNode.pressureBar,
                            onValueChange = { newPressure ->
                                engine.updateNodeParameters(
                                    activeNode.gridX,
                                    activeNode.gridY,
                                    activeNode.speedMultiplier,
                                    newPressure,
                                    activeNode.filterSelectivity
                                )
                            },
                            valueRange = 50f..300f,
                            colors = SliderDefaults.colors(
                                thumbColor = HazmatOrange,
                                activeTrackColor = HazmatOrange
                            ),
                            modifier = Modifier.testTag("slider_pressure")
                        )
                    }

                    // Optical Selectivity (for Optical Sorter / Magnet)
                    if (activeNode.type == MachineNodeType.OPTICAL_SORTER || activeNode.type == MachineNodeType.MAGNETIC_SEPARATOR) {
                        Text(
                            text = "Селективность фильтрации: ${(activeNode.filterSelectivity * 100).toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = SleekTextDark
                        )
                        Slider(
                            value = activeNode.filterSelectivity,
                            onValueChange = { newFilter ->
                                engine.updateNodeParameters(
                                    activeNode.gridX,
                                    activeNode.gridY,
                                    activeNode.speedMultiplier,
                                    activeNode.pressureBar,
                                    newFilter
                                )
                            },
                            valueRange = 0.70f..0.99f,
                            colors = SliderDefaults.colors(
                                thumbColor = EcoEmerald,
                                activeTrackColor = EcoEmerald
                            ),
                            modifier = Modifier.testTag("slider_selectivity")
                        )
                    }

                    Text(
                        text = "Всего переработано узлом: ${String.format("%.1f", activeNode.totalProcessedTons)} т",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTealDark
                    )
                }
            }
        } else if (selectedGridCoord != null) {
            // Empty slot selected: Build new machine
            val (gx, gy) = selectedGridCoord!!
            Card(
                colors = CardDefaults.cardColors(containerColor = SleekCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Построить модуль в ячейке [$gx, $gy]",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextDark
                    )
                    Text(
                        text = "Выберите тип промышленного механизма:",
                        fontSize = 11.sp,
                        color = SleekTextMuted
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(MachineNodeType.values()) { mType ->
                            val isSel = selectedShopMachine == mType
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSel) SleekTealMuted else SleekBackground,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSel) SleekTeal else SleekCardBorder
                                ),
                                modifier = Modifier
                                    .width(135.dp)
                                    .clickable { selectedShopMachine = mType }
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = when (mType) {
                                            MachineNodeType.INTAKE_HOPPER -> "📥"
                                            MachineNodeType.CONVEYOR_BELT -> "➡️"
                                            MachineNodeType.HIGH_SPEED_BELT -> "⚡"
                                            MachineNodeType.PRIMARY_SHREDDER -> "⚙️"
                                            MachineNodeType.MAGNETIC_SEPARATOR -> "🧲"
                                            MachineNodeType.OPTICAL_SORTER -> "🔬"
                                            MachineNodeType.HYDRAULIC_PRESS -> "🗜️"
                                            MachineNodeType.BIO_DIGESTER -> "🌱"
                                            MachineNodeType.HAZMAT_SCRUBBER -> "🧪"
                                            MachineNodeType.PRECIOUS_SMELTER -> "🔥"
                                        },
                                        fontSize = 22.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = mType.displayName,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SleekTextDark,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "$${mType.cost.toInt()}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = SleekTeal
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            engine.addOrUpdateConveyorNode(gx, gy, selectedShopMachine, 0)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SleekTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("build_machine_btn")
                    ) {
                        Text(
                            text = "Установить ${selectedShopMachine.displayName} ($${selectedShopMachine.cost.toInt()})",
                            color = SleekTextWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
