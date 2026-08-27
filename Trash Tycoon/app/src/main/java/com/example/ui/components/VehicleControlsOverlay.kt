package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.GameSimulationEngine
import com.example.model.VehicleType
import com.example.ui.theme.*
import kotlin.math.*

@Composable
fun VehicleControlsOverlay(
    engine: GameSimulationEngine,
    modifier: Modifier = Modifier
) {
    val vehicles by engine.vehicles.collectAsState()
    val activeId by engine.activePlayerVehicleId.collectAsState()
    val activeVehicle = vehicles.find { it.id == activeId } ?: vehicles.firstOrNull()

    var joystickOffset by remember { mutableStateOf(Offset.Zero) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(14.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        // Vehicle switcher chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            vehicles.forEach { v ->
                val isSelected = v.id == activeId
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) SleekTeal else SleekCard,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) SleekTealDark else SleekCardBorder
                    ),
                    modifier = Modifier
                        .clickable {
                            engine.switchActiveVehicle(v.id)
                        }
                        .testTag("vehicle_chip_${v.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (v.type) {
                                VehicleType.BULLDOZER -> "🚜 Бульдозер"
                                VehicleType.EXCAVATOR_CLAW -> "🏗️ Грейфер"
                                VehicleType.DUMP_TRUCK -> "🚛 Самосвал"
                                VehicleType.FORKLIFT -> "⚡ Погрузчик"
                            },
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) SleekTextWhite else SleekTextDark
                        )
                        if (v.loadedTons > 0f) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "[${v.loadedTons.toInt()}т]",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) SafetyAmberLight else SleekTeal
                            )
                        }
                    }
                }
            }
        }

        // Active Vehicle Status Bar & Action Controls
        activeVehicle?.let { v ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SleekDarkCard.copy(alpha = 0.92f))
                    .border(1.dp, SleekDarkCardBorder, RoundedCornerShape(18.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = v.type.displayName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextWhite
                    )
                    Text(
                        text = if (v.loadedTons > 0f)
                            "Загрузка: ${v.loadedTons.toInt()}/${v.type.capacity.toInt()} т (${v.loadedType?.displayName ?: ""})"
                        else "Ковш/Кузов пуст (вместимость ${v.type.capacity.toInt()}т)",
                        fontSize = 11.sp,
                        color = if (v.loadedTons > 0f) EcoEmeraldLight else SleekTextLight
                    )
                }

                Button(
                    onClick = { engine.toggleVehicleAutoPilot(v.id) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (v.isAutoPilot) EcoEmeraldDark else Color.Black.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("toggle_autopilot_btn")
                ) {
                    Text(
                        text = if (v.isAutoPilot) "🤖 Авто-AI" else "🕹️ Ручное",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SleekTextWhite
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Virtual Controls: Dual Sticks / Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            // Steering & Drive D-Pad Joystick
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(SleekDarkCard.copy(alpha = 0.88f))
                    .border(2.dp, SleekDarkCardBorder, CircleShape)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val center = Offset(65.dp.toPx(), 65.dp.toPx())
                                val delta = offset - center
                                val maxRadius = 45.dp.toPx()
                                val dist = min(delta.getDistance(), maxRadius)
                                val angle = atan2(delta.y.toDouble(), delta.x.toDouble())
                                joystickOffset = Offset(
                                    (cos(angle) * dist).toFloat(),
                                    (sin(angle) * dist).toFloat()
                                )
                                val speed = -(sin(angle)).toFloat()
                                val turn = (cos(angle) * 8f).toFloat()
                                engine.playerDriveVehicle(speed, turn)
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val newOffset = joystickOffset + dragAmount
                                val maxRadius = 45.dp.toPx()
                                val dist = min(newOffset.getDistance(), maxRadius)
                                val angle = atan2(newOffset.y.toDouble(), newOffset.x.toDouble())
                                joystickOffset = Offset(
                                    (cos(angle) * dist).toFloat(),
                                    (sin(angle) * dist).toFloat()
                                )
                                val speed = -(sin(angle)).toFloat()
                                val turn = (cos(angle) * 9f).toFloat()
                                engine.playerDriveVehicle(speed, turn)
                            },
                            onDragEnd = {
                                joystickOffset = Offset.Zero
                                engine.playerDriveVehicle(0f, 0f)
                            },
                            onDragCancel = {
                                joystickOffset = Offset.Zero
                                engine.playerDriveVehicle(0f, 0f)
                            }
                        )
                    }
                    .testTag("joystick_touch_area"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    tint = SleekTextLight.copy(alpha = 0.4f),
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 4.dp)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = SleekTextLight.copy(alpha = 0.4f),
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = SleekTextLight.copy(alpha = 0.4f),
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 4.dp)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = SleekTextLight.copy(alpha = 0.4f),
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 4.dp)
                )

                // Joystick Thumb Knob
                Box(
                    modifier = Modifier
                        .offset { IntOffset(joystickOffset.x.roundToInt(), joystickOffset.y.roundToInt()) }
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(SleekTeal)
                        .border(2.dp, EcoEmeraldLight, CircleShape)
                )
            }

            // Right Action Controls: Quick Feed button + Scoop/Grab/Dump button
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        activeVehicle?.let { v ->
                            if (v.loadedTons > 0f) {
                                engine.feedIntoIntakeHopper(v.loadedType ?: com.example.model.WasteType.MIXED_TRASH, v.loadedTons)
                                v.loadedTons = 0f
                                v.loadedType = null
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SleekTeal),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("action_quick_hopper_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoveToInbox,
                        contentDescription = "Сброс в бункер",
                        tint = SleekTextWhite,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("В Бункер", fontSize = 12.sp, color = SleekTextWhite, fontWeight = FontWeight.Bold)
                }

                // Primary Large Action Button (Scoop / Grab / Dump)
                Button(
                    onClick = { engine.playerPerformVehicleAction() },
                    colors = ButtonDefaults.buttonColors(containerColor = EcoEmerald),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(80.dp)
                        .testTag("action_primary_scoop_btn"),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Construction,
                            contentDescription = "Действие ковша/грейфера",
                            tint = SleekTextWhite,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = if (activeVehicle?.loadedTons ?: 0f > 0f) "ВЫГРУЗИТЬ" else "ЗАХВАТИТЬ",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SleekTextWhite
                        )
                    }
                }
            }
        }
    }
}
