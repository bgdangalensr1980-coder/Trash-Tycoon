package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.GameSimulationEngine
import com.example.model.*
import com.example.ui.components.VehicleControlsOverlay
import com.example.ui.theme.*
import kotlin.math.*

@Composable
fun YardSimulationView(
    engine: GameSimulationEngine,
    modifier: Modifier = Modifier
) {
    val piles by engine.wastePiles.collectAsState()
    val vehicles by engine.vehicles.collectAsState()
    val activeId by engine.activePlayerVehicleId.collectAsState()
    val totalTons by engine.totalTonsRecycled.collectAsState()

    Box(modifier = modifier.fillMaxSize().background(YardGround)) {
        // 2D Interactive Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("yard_canvas_2d")
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        // Check if player clicked near a pile to target it
                        val clickedPile = piles.minByOrNull {
                            sqrt((it.x - tapOffset.x).pow(2) + (it.y - tapOffset.y).pow(2))
                        }
                        if (clickedPile != null) {
                            val dist = sqrt((clickedPile.x - tapOffset.x).pow(2) + (clickedPile.y - tapOffset.y).pow(2))
                            if (dist < 60f) {
                                engine.postAlert("📍 Выбрана куча: ${clickedPile.type.displayName} (${clickedPile.tons} т)")
                            }
                        }
                    }
                }
        ) {
            // 1. Draw Yard Terrain Grid & Asphalt Pads
            drawYardEnvironment()

            // 2. Draw Waste Piles
            piles.forEach { pile ->
                drawWastePile(pile)
            }

            // 3. Draw Heavy Machinery & Workers
            vehicles.forEach { vehicle ->
                drawHeavyVehicle(vehicle, isPlayerControlled = (vehicle.id == activeId))
            }
        }

        // Overlay with Machine Controls
        VehicleControlsOverlay(
            engine = engine,
            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun DrawScope.drawYardEnvironment() {
    val w = size.width
    val h = size.height

    // Grid lines for industrial zone aesthetic
    val step = 60f
    var x = 0f
    while (x < w) {
        drawLine(
            color = YardGridLine.copy(alpha = 0.4f),
            start = Offset(x, 0f),
            end = Offset(x, h),
            strokeWidth = 1f
        )
        x += step
    }
    var y = 0f
    while (y < h) {
        drawLine(
            color = YardGridLine.copy(alpha = 0.4f),
            start = Offset(0f, y),
            end = Offset(w, y),
            strokeWidth = 1f
        )
        y += step
    }

    // 1. Reception Weigh Station (Top Left)
    drawRoundRect(
        color = YardBuilding,
        topLeft = Offset(40f, 40f),
        size = Size(180f, 100f),
        cornerRadius = CornerRadius(12f, 12f)
    )
    drawRoundRect(
        color = SafetyAmber.copy(alpha = 0.35f),
        topLeft = Offset(45f, 45f),
        size = Size(170f, 90f),
        cornerRadius = CornerRadius(8f, 8f),
        style = Stroke(width = 2f)
    )

    // 2. Main Crusher & Intake Hopper Area (Top Left-Center)
    drawRoundRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(130f, 130f),
        size = Size(90f, 90f),
        cornerRadius = CornerRadius(10f, 10f)
    )
    drawCircle(
        color = DangerRed.copy(alpha = 0.8f),
        radius = 28f,
        center = Offset(175f, 175f),
        style = Stroke(width = 4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)))
    )

    // 3. Sorting & Baler Complex (Top Right)
    drawRoundRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(w - 220f, 40f),
        size = Size(180f, 120f),
        cornerRadius = CornerRadius(12f, 12f)
    )
    drawRect(
        color = EcoEmerald.copy(alpha = 0.25f),
        topLeft = Offset(w - 210f, 50f),
        size = Size(160f, 100f),
        style = Stroke(width = 2f)
    )

    // 4. Hazmat Chemical Containment Pit (Bottom Left)
    drawRoundRect(
        color = Color(0xFF3E1F1F),
        topLeft = Offset(40f, h - 220f),
        size = Size(140f, 120f),
        cornerRadius = CornerRadius(14f, 14f)
    )
    drawRoundRect(
        color = HazmatOrange.copy(alpha = 0.6f),
        topLeft = Offset(45f, h - 215f),
        size = Size(130f, 110f),
        cornerRadius = CornerRadius(10f, 10f),
        style = Stroke(width = 3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f)))
    )

    // 5. Bio-Composting Facility (Bottom Center)
    drawRoundRect(
        color = Color(0xFF1B3320),
        topLeft = Offset(w / 2f - 70f, h - 200f),
        size = Size(140f, 100f),
        cornerRadius = CornerRadius(12f, 12f)
    )
    drawRoundRect(
        color = EcoEmerald.copy(alpha = 0.5f),
        topLeft = Offset(w / 2f - 65f, h - 195f),
        size = Size(130f, 90f),
        cornerRadius = CornerRadius(8f, 8f),
        style = Stroke(width = 2f)
    )

    // 6. Logistics Rail Depot & Export Docks (Right Side)
    drawRoundRect(
        color = Color(0xFF232D3F),
        topLeft = Offset(w - 120f, 200f),
        size = Size(90f, 320f),
        cornerRadius = CornerRadius(8f, 8f)
    )
    // Draw railway tracks
    for (ry in 210..500 step 25) {
        drawLine(
            color = IndustrialBorder,
            start = Offset(w - 110f, ry.toFloat()),
            end = Offset(w - 40f, ry.toFloat()),
            strokeWidth = 3f
        )
    }
    drawLine(
        color = SafetyAmber,
        start = Offset(w - 95f, 200f),
        end = Offset(w - 95f, 520f),
        strokeWidth = 4f
    )
    drawLine(
        color = SafetyAmber,
        start = Offset(w - 55f, 200f),
        end = Offset(w - 55f, 520f),
        strokeWidth = 4f
    )
}

private fun DrawScope.drawWastePile(pile: WastePile) {
    val radius = (14f + sqrt(pile.tons) * 4f).coerceIn(16f, 48f)
    val color = Color(pile.type.colorHex)

    // Shadow
    drawCircle(
        color = Color.Black.copy(alpha = 0.45f),
        radius = radius * 1.15f,
        center = Offset(pile.x + 4f, pile.y + 6f)
    )

    // Main pile lump with jagged irregular shape or multi-circles
    drawCircle(
        color = color.copy(alpha = 0.9f),
        radius = radius,
        center = Offset(pile.x, pile.y)
    )

    // Clump details
    drawCircle(
        color = color.copy(alpha = 0.6f),
        radius = radius * 0.6f,
        center = Offset(pile.x - radius * 0.35f, pile.y - radius * 0.3f)
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.25f),
        radius = radius * 0.4f,
        center = Offset(pile.x + radius * 0.3f, pile.y + radius * 0.2f)
    )

    // Outline
    drawCircle(
        color = if (pile.type.isHazardous) DangerRed else IndustrialBorder,
        radius = radius,
        center = Offset(pile.x, pile.y),
        style = Stroke(width = if (pile.type.isHazardous) 2.5f else 1.5f)
    )
}

private fun DrawScope.drawHeavyVehicle(vehicle: HeavyVehicle, isPlayerControlled: Boolean) {
    rotate(degrees = vehicle.angleDeg, pivot = Offset(vehicle.x, vehicle.y)) {
        val vx = vehicle.x
        val vy = vehicle.y
        val vColor = Color(vehicle.type.colorHex)

        // Shadow
        drawRoundRect(
            color = Color.Black.copy(alpha = 0.4f),
            topLeft = Offset(vx - 22f + 4f, vy - 14f + 5f),
            size = Size(48f, 28f),
            cornerRadius = CornerRadius(4f, 4f)
        )

        // Machine Body
        drawRoundRect(
            color = vColor,
            topLeft = Offset(vx - 22f, vy - 14f),
            size = Size(44f, 28f),
            cornerRadius = CornerRadius(5f, 5f)
        )

        // Cabin / Windshield
        drawRoundRect(
            color = Color(0xFF0F172A),
            topLeft = Offset(vx - 6f, vy - 10f),
            size = Size(18f, 20f),
            cornerRadius = CornerRadius(3f, 3f)
        )
        drawRect(
            color = CyberCyan.copy(alpha = 0.7f),
            topLeft = Offset(vx + 2f, vy - 8f),
            size = Size(8f, 16f)
        )

        // Front Shovel / Claw / Tipper Bed
        when (vehicle.type) {
            VehicleType.BULLDOZER -> {
                // Heavy yellow blade in front
                drawRoundRect(
                    color = SafetyAmberDark,
                    topLeft = Offset(vx + 22f, vy - 16f),
                    size = Size(8f, 32f),
                    cornerRadius = CornerRadius(2f, 2f)
                )
                // Hydraulic arms
                drawLine(
                    color = IndustrialBorder,
                    start = Offset(vx + 10f, vy - 10f),
                    end = Offset(vx + 22f, vy - 10f),
                    strokeWidth = 3f
                )
                drawLine(
                    color = IndustrialBorder,
                    start = Offset(vx + 10f, vy + 10f),
                    end = Offset(vx + 22f, vy + 10f),
                    strokeWidth = 3f
                )
            }
            VehicleType.EXCAVATOR_CLAW -> {
                // Rotatable claw arm
                drawLine(
                    color = HazmatOrange,
                    start = Offset(vx + 5f, vy),
                    end = Offset(vx + 26f, vy),
                    strokeWidth = 5f
                )
                // Hydraulic Claw prongs
                drawCircle(
                    color = IndustrialBorder,
                    radius = 7f,
                    center = Offset(vx + 28f, vy)
                )
            }
            VehicleType.DUMP_TRUCK -> {
                // Rear Cargo Bed
                drawRoundRect(
                    color = if (vehicle.loadedTons > 0f) Color(vehicle.loadedType?.colorHex ?: 0xFF8D6E63) else IndustrialSurfaceLight,
                    topLeft = Offset(vx - 20f, vy - 12f),
                    size = Size(20f, 24f),
                    cornerRadius = CornerRadius(3f, 3f)
                )
            }
            VehicleType.FORKLIFT -> {
                // Dual Lift Forks
                drawLine(
                    color = IndustrialBorder,
                    start = Offset(vx + 22f, vy - 7f),
                    end = Offset(vx + 34f, vy - 7f),
                    strokeWidth = 3f
                )
                drawLine(
                    color = IndustrialBorder,
                    start = Offset(vx + 22f, vy + 7f),
                    end = Offset(vx + 34f, vy + 7f),
                    strokeWidth = 3f
                )
            }
        }

        // Animated Headlight Beams
        drawArc(
            color = Color(0xFFFEF08A).copy(alpha = 0.25f),
            startAngle = -25f,
            sweepAngle = 50f,
            useCenter = true,
            topLeft = Offset(vx + 15f, vy - 35f),
            size = Size(80f, 70f)
        )

        // Player Indicator Ring
        if (isPlayerControlled) {
            drawCircle(
                color = CyberCyan,
                radius = 26f,
                center = Offset(vx, vy),
                style = Stroke(width = 2.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)))
            )
        }
    }
}
