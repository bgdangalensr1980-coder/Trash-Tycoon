package com.example.engine

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.*
import kotlin.random.Random

class GameSimulationEngine {

    val money = MutableStateFlow(45000.0)
    val plantLevel = MutableStateFlow(1)
    val reputationXp = MutableStateFlow(1200L)
    val ecoRating = MutableStateFlow(88.0f) // 0-100%
    val totalTonsRecycled = MutableStateFlow(124.5)
    val gameHours = MutableStateFlow(8.0f) // 08:00 AM

    // Resource Inventory
    val inventory = MutableStateFlow<Map<ResourceType, Double>>(
        mapOf(
            ResourceType.RECYCLED_STEEL to 18.5,
            ResourceType.PLASTIC_PELLETS to 12.0,
            ResourceType.PURE_COPPER to 4.2,
            ResourceType.PRECIOUS_METALS to 0.8,
            ResourceType.BIO_FERTILIZER to 25.0,
            ResourceType.RDF_FUEL to 30.0,
            ResourceType.SAFE_NEUTRALIZED to 6.0,
            ResourceType.GREEN_ENERGY to 45.0
        )
    )

    // 2D Yard State: Waste Piles
    val wastePiles = MutableStateFlow<List<WastePile>>(
        listOf(
            WastePile("p1", WasteType.SCRAP_METAL, 280f, 320f, 18.5f),
            WastePile("p2", WasteType.PLASTIC, 450f, 290f, 14.0f),
            WastePile("p3", WasteType.COPPER_WIRE, 320f, 480f, 6.5f),
            WastePile("p4", WasteType.MIXED_TRASH, 180f, 240f, 32.0f),
            WastePile("p5", WasteType.E_WASTE, 520f, 460f, 5.0f),
            WastePile("p6", WasteType.ORGANIC, 600f, 310f, 22.0f),
            WastePile("p7", WasteType.HAZMAT_CHEMICAL, 410f, 580f, 8.0f)
        )
    )

    // Heavy Vehicles
    val vehicles = MutableStateFlow<List<HeavyVehicle>>(
        listOf(
            HeavyVehicle(
                id = "v_loader",
                type = VehicleType.BULLDOZER,
                x = 220f,
                y = 350f,
                angleDeg = 45f,
                isPlayerDriven = true,
                isAutoPilot = false,
                level = 1
            ),
            HeavyVehicle(
                id = "v_excavator",
                type = VehicleType.EXCAVATOR_CLAW,
                x = 350f,
                y = 280f,
                angleDeg = 120f,
                isPlayerDriven = false,
                isAutoPilot = true,
                level = 1
            ),
            HeavyVehicle(
                id = "v_truck",
                type = VehicleType.DUMP_TRUCK,
                x = 120f,
                y = 180f,
                angleDeg = 90f,
                isPlayerDriven = false,
                isAutoPilot = true,
                level = 1
            ),
            HeavyVehicle(
                id = "v_forklift",
                type = VehicleType.FORKLIFT,
                x = 650f,
                y = 520f,
                angleDeg = 0f,
                isPlayerDriven = false,
                isAutoPilot = true,
                level = 1
            )
        )
    )

    // Active Player-controlled vehicle ID
    val activePlayerVehicleId = MutableStateFlow("v_loader")

    // Conveyor Grid Pipeline Layout (6x6 Grid)
    val conveyorGrid = MutableStateFlow<List<ConveyorNode>>(
        listOf(
            ConveyorNode("c_0_0", 0, 1, MachineNodeType.INTAKE_HOPPER, directionDeg = 0, speedMultiplier = 1.2f),
            ConveyorNode("c_1_0", 1, 1, MachineNodeType.PRIMARY_SHREDDER, directionDeg = 0, speedMultiplier = 1.0f),
            ConveyorNode("c_2_0", 2, 1, MachineNodeType.MAGNETIC_SEPARATOR, directionDeg = 0, speedMultiplier = 1.2f),
            ConveyorNode("c_3_0", 3, 1, MachineNodeType.OPTICAL_SORTER, directionDeg = 0, speedMultiplier = 1.1f),
            ConveyorNode("c_4_0", 4, 1, MachineNodeType.HYDRAULIC_PRESS, directionDeg = 90, speedMultiplier = 1.0f),
            ConveyorNode("c_4_1", 4, 2, MachineNodeType.CONVEYOR_BELT, directionDeg = 90, speedMultiplier = 1.5f),
            ConveyorNode("c_4_2", 4, 3, MachineNodeType.PRECIOUS_SMELTER, directionDeg = 180, speedMultiplier = 0.8f),
            ConveyorNode("c_3_2", 3, 3, MachineNodeType.BIO_DIGESTER, directionDeg = 180, speedMultiplier = 1.0f),
            ConveyorNode("c_2_2", 2, 3, MachineNodeType.HAZMAT_SCRUBBER, directionDeg = 270, speedMultiplier = 1.0f)
        )
    )

    // Market Commodities
    val marketCommodities = MutableStateFlow<List<MarketCommodity>>(
        listOf(
            MarketCommodity(
                ResourceType.RECYCLED_STEEL,
                240.0,
                232.0,
                listOf(210.0, 218.0, 225.0, 232.0, 240.0),
                +3.4,
                "Высокий спрос (Строительный сектор)"
            ),
            MarketCommodity(
                ResourceType.PLASTIC_PELLETS,
                195.0,
                204.0,
                listOf(215.0, 210.0, 208.0, 204.0, 195.0),
                -4.4,
                "Умеренный спрос (Упаковочные заводы)"
            ),
            MarketCommodity(
                ResourceType.PURE_COPPER,
                820.0,
                785.0,
                listOf(740.0, 760.0, 775.0, 785.0, 820.0),
                +4.5,
                "Дефицит! (Производство электромобилей)"
            ),
            MarketCommodity(
                ResourceType.PRECIOUS_METALS,
                4200.0,
                4120.0,
                listOf(3950.0, 4020.0, 4080.0, 4120.0, 4200.0),
                +1.9,
                "Стабильный рост (Спрос микроэлектроники)"
            ),
            MarketCommodity(
                ResourceType.BIO_FERTILIZER,
                75.0,
                70.0,
                listOf(65.0, 68.0, 70.0, 70.0, 75.0),
                +7.1,
                "Сезонный пик (Агрохолдинги)"
            ),
            MarketCommodity(
                ResourceType.RDF_FUEL,
                90.0,
                92.0,
                listOf(95.0, 94.0, 93.0, 92.0, 90.0),
                -2.2,
                "Базовый спрос (Цементные заводы)"
            ),
            MarketCommodity(
                ResourceType.GREEN_ENERGY,
                110.0,
                105.0,
                listOf(98.0, 102.0, 104.0, 105.0, 110.0),
                +4.8,
                "Гос. субсидии за декарбонизацию"
            )
        )
    )

    // Staff List
    val staffMembers = MutableStateFlow<List<StaffMember>>(
        listOf(
            StaffMember("s1", "Алексей Громов", StaffRole.OPERATOR, "👷‍♂️", 2, 220.0, 98f, 1.25f, true),
            StaffMember("s2", "Елена Соколова", StaffRole.SORTER, "👩‍🔧", 3, 160.0, 92f, 1.30f, true),
            StaffMember("s3", "Виктор Седых", StaffRole.ENGINEER, "👨‍🏭", 2, 290.0, 95f, 1.40f, true),
            StaffMember("s4", "Анна Морозова", StaffRole.ECO_INSPECTOR, "👩‍🔬", 3, 260.0, 96f, 1.50f, true),
            StaffMember("s5", "Сергей Данилов", StaffRole.DISPATCHER, "🧑‍💼", 1, 160.0, 89f, 1.15f, true)
        )
    )

    // Contracts Board
    val activeContracts = MutableStateFlow<List<RecyclingContract>>(
        listOf(
            RecyclingContract(
                id = "c1",
                clientName = "Мэрия Мегаполиса",
                clientType = "Городской округ",
                targetType = WasteType.SCRAP_METAL,
                requiredTons = 50f,
                deliveredTons = 18.5f,
                minimumPurityPercent = 85f,
                payoutTotal = 16000.0,
                deadlineGameHours = 48f,
                remainingGameHours = 36f,
                isAuctionAwarded = false
            ),
            RecyclingContract(
                id = "c2",
                clientName = "ЭкоПак Индустри",
                clientType = "Полимерный концерн",
                targetType = WasteType.PLASTIC,
                requiredTons = 40f,
                deliveredTons = 12.0f,
                minimumPurityPercent = 90f,
                payoutTotal = 12500.0,
                deadlineGameHours = 36f,
                remainingGameHours = 24f,
                isAuctionAwarded = false
            ),
            RecyclingContract(
                id = "c3",
                clientName = "ТехноСпецЭлектроника",
                clientType = "IT-Кластер",
                targetType = WasteType.E_WASTE,
                requiredTons = 15f,
                deliveredTons = 5.0f,
                minimumPurityPercent = 92f,
                payoutTotal = 28000.0,
                deadlineGameHours = 60f,
                remainingGameHours = 52f,
                isAuctionAwarded = true
            )
        )
    )

    // Live Auction
    val liveAuction = MutableStateFlow<LiveAuction?>(
        LiveAuction(
            id = "auc_101",
            title = "ГосТендер: Утилизация выведенного из эксплуатации технопарка",
            wasteType = WasteType.E_WASTE,
            volumeTons = 65f,
            baseEstimatedValue = 95000.0,
            currentHighBid = 42000.0,
            currentLeaderName = "ScrapGlobal LLC",
            isPlayerLeading = false,
            secondsRemaining = 24,
            competitorBidders = listOf(
                AuctionCompetitor("GreenApex Industries", 72000.0, 0.85f),
                AuctionCompetitor("ScrapGlobal LLC", 68000.0, 0.90f),
                AuctionCompetitor("BioCleanse Group", 54000.0, 0.70f)
            )
        )
    )

    // Tech Upgrades
    val techTree = MutableStateFlow<List<TechUpgrade>>(
        listOf(
            TechUpgrade("t_mag_boost", "Неодимовые магниты N52", "Механика", "+30% к эффективности захвата стали", 4500.0, "🧲", true),
            TechUpgrade("t_laser_ai", "ИИ-Спектрометрия полимеров", "ИИ", "Оптический сортировщик распознает 12 типов пластмасс с чистотой 98%", 12000.0, "🤖", false),
            TechUpgrade("t_hydraulic_turbo", "Турбо-гидравлика 300 Бар", "Механика", "Ускоряет прессование тюков в 2 раза", 8000.0, "⚡", false),
            TechUpgrade("t_catalytic_scrub", "Каталитические фильтры Euro-6", "Экология", "Снижает выбросы дыма и серы на 80%, аннулируя мелкие штрафы", 9500.0, "🌱", false),
            TechUpgrade("t_rail_hub", "Железнодорожный терминал", "Логистика", "Прямая отгрузка оптовых партий поездами без очередей (+25% к цене сбыта)", 25000.0, "🚂", false),
            TechUpgrade("t_drone_fleet", "Флот инспекционных дронов", "ИИ", "Автоматический мониторинг протечек и немедленное предупреждение аварий", 14000.0, "🛸", false)
        )
    )

    // Eco Violations Log
    val ecoViolations = MutableStateFlow<List<EcoViolation>>(emptyList())

    // Real-time Efficiency Stats
    val efficiencyStats = MutableStateFlow(
        ProductionEfficiencyStats(
            tonsProcessedToday = 62.4,
            tonsRecycledTotal = 124.5,
            currentThroughputTonsPerMin = 4.8,
            averagePurityPercent = 91.5f,
            energyConsumptionKw = 168.0,
            grossRevenuePerHour = 18500.0,
            netProfitPerHour = 13200.0,
            overallEquipmentEffectiveness = 92.4f,
            throughputHistory = listOf(3.2f, 3.8f, 4.1f, 4.5f, 4.2f, 4.8f, 5.1f, 4.8f),
            revenueHistory = listOf(12000f, 13500f, 15000f, 16200f, 17500f, 18500f)
        )
    )

    // Feedback notification event (e.g. "+$1,200", "Контракт выполнен!", "Эко-штраф -$5,000")
    val alertMessages = MutableStateFlow<List<String>>(emptyList())

    fun postAlert(msg: String) {
        val current = alertMessages.value.toMutableList()
        current.add(0, msg)
        if (current.size > 8) current.removeAt(current.size - 1)
        alertMessages.value = current
    }

    // --- GAME TICK UPDATE (Called ~30-60 times per second or periodically) ---
    fun updateTick(deltaSeconds: Float) {
        // Advance game time (1 game hour = 30 real seconds)
        var newHours = gameHours.value + deltaSeconds / 30f
        if (newHours >= 24f) newHours -= 24f
        gameHours.value = newHours

        // Update active vehicles movement & AI
        updateVehicles(deltaSeconds)

        // Process Conveyor Pipelines
        updateConveyors(deltaSeconds)

        // Update live auction countdown & AI bids
        updateAuction(deltaSeconds)

        // Update contracts remaining time
        updateContracts(deltaSeconds)

        // Random supply truck arrivals (spawns new waste piles if low)
        checkSupplyArrivals(deltaSeconds)

        // Market price fluctuation cycle
        updateMarketFluctuation(deltaSeconds)

        // Eco-audit risk check
        checkEcoAuditRisk(deltaSeconds)
    }

    private var marketTickCounter = 0f
    private fun updateMarketFluctuation(dt: Float) {
        marketTickCounter += dt
        if (marketTickCounter >= 10f) {
            marketTickCounter = 0f
            val updated = marketCommodities.value.map { item ->
                val deltaPercent = (Random.nextFloat() - 0.48f) * 0.08f // -4% to +4.5%
                val newPrice = (item.currentPricePerUnit * (1.0 + deltaPercent)).coerceAtLeast(item.type.baseMarketPrice * 0.5)
                val newHistory = (item.priceHistory + newPrice).takeLast(10)
                val change24h = ((newPrice - item.previousPrice) / item.previousPrice) * 100.0
                item.copy(
                    currentPricePerUnit = (newPrice * 10.0).roundToInt() / 10.0,
                    priceHistory = newHistory,
                    priceChange24h = (change24h * 10.0).roundToInt() / 10.0
                )
            }
            marketCommodities.value = updated
        }
    }

    private var ecoAuditTimer = 0f
    private fun checkEcoAuditRisk(dt: Float) {
        ecoAuditTimer += dt
        if (ecoAuditTimer >= 35f) {
            ecoAuditTimer = 0f
            val eco = ecoRating.value
            val hasInspector = staffMembers.value.any { it.role == StaffRole.ECO_INSPECTOR && it.isAssigned }
            val hasScrubberTech = techTree.value.any { it.id == "t_catalytic_scrub" && it.isUnlocked }

            if (eco < 65f && Random.nextFloat() < 0.45f) {
                // Violation occurred!
                val baseFine = (70f - eco) * 600.0
                val fine = if (hasInspector) baseFine * 0.4 else baseFine
                val actualFine = if (hasScrubberTech) fine * 0.3 else fine
                val fineFormatted = (actualFine.toInt() / 100) * 100.0

                money.value = (money.value - fineFormatted).coerceAtLeast(0.0)
                val violation = EcoViolation(
                    id = "v_${System.currentTimeMillis()}",
                    title = "Экологический штраф ГосНадзора",
                    fineAmount = fineFormatted,
                    cause = if (eco < 40f) "Критическое загрязнение грунтовых вод фильтратом" else "Превышение предельных концентраций токсичных выбросов",
                    timestampFormatted = String.format("%02d:00", gameHours.value.toInt())
                )
                ecoViolations.value = listOf(violation) + ecoViolations.value.take(6)
                postAlert("⚠️ ШТРАФ ГосНадзора: -$${fineFormatted.toInt()} за нарушение эко-норм!")
                ecoRating.value = (ecoRating.value + 10f).coerceAtMost(100f) // Reset slightly after fine
            }
        }
    }

    private var supplyTruckTimer = 0f
    private fun checkSupplyArrivals(dt: Float) {
        supplyTruckTimer += dt
        val currentPiles = wastePiles.value
        if (supplyTruckTimer >= 18f || currentPiles.isEmpty()) {
            supplyTruckTimer = 0f
            if (currentPiles.size < 12) {
                val randomType = WasteType.values().random()
                val newX = Random.nextFloat() * 450f + 150f
                val newY = Random.nextFloat() * 350f + 200f
                val tons = Random.nextFloat() * 15f + 8f
                val newPile = WastePile(
                    id = "p_${System.currentTimeMillis()}_${Random.nextInt(1000)}",
                    type = randomType,
                    x = newX,
                    y = newY,
                    tons = (tons * 10).roundToInt() / 10f
                )
                wastePiles.value = currentPiles + newPile
                postAlert("🚛 Прибыла фура: ${newPile.type.displayName} (+${newPile.tons} т)")
            }
        }
    }

    private fun updateVehicles(dt: Float) {
        val currentVehicles = vehicles.value.toMutableList()
        val piles = wastePiles.value.toMutableList()

        for (i in currentVehicles.indices) {
            val v = currentVehicles[i]
            if (v.isAutoPilot && !v.isPlayerDriven) {
                // AI Autopilot behavior: Find closest matching waste pile and move towards it, or deliver to intake
                if (v.loadedTons < v.type.capacity * 0.8f && piles.isNotEmpty()) {
                    // Pick target pile
                    val targetPile = piles.minByOrNull { sqrt((it.x - v.x).pow(2) + (it.y - v.y).pow(2)) }
                    if (targetPile != null) {
                        val dx = targetPile.x - v.x
                        val dy = targetPile.y - v.y
                        val dist = sqrt(dx * dx + dy * dy)
                        val targetAngle = (atan2(dy.toDouble(), dx.toDouble()) * 180 / Math.PI).toFloat()

                        v.angleDeg = targetAngle
                        if (dist > 25f) {
                            v.x += (dx / dist) * v.type.speed * 18f * dt
                            v.y += (dy / dist) * v.type.speed * 18f * dt
                        } else {
                            // Scoop/Load trash!
                            val scoopAmount = min(targetPile.tons, v.type.capacity - v.loadedTons)
                            targetPile.tons -= scoopAmount
                            v.loadedTons += scoopAmount
                            v.loadedType = targetPile.type
                            if (targetPile.tons <= 0.5f) {
                                piles.remove(targetPile)
                            }
                        }
                    }
                } else if (v.loadedTons > 0f) {
                    // Deliver to conveyor intake hopper (located at grid 0,1 or yard coords (150f, 150f))
                    val intakeX = 160f
                    val intakeY = 160f
                    val dx = intakeX - v.x
                    val dy = intakeY - v.y
                    val dist = sqrt(dx * dx + dy * dy)
                    val targetAngle = (atan2(dy.toDouble(), dx.toDouble()) * 180 / Math.PI).toFloat()
                    v.angleDeg = targetAngle

                    if (dist > 30f) {
                        v.x += (dx / dist) * v.type.speed * 18f * dt
                        v.y += (dy / dist) * v.type.speed * 18f * dt
                    } else {
                        // Unload into conveyor intake hopper!
                        feedIntoIntakeHopper(v.loadedType ?: WasteType.MIXED_TRASH, v.loadedTons)
                        v.loadedTons = 0f
                        v.loadedType = null
                    }
                }
            } else if (v.isPlayerDriven) {
                // Move according to current speed and angle
                if (abs(v.currentSpeed) > 0.05f) {
                    val rad = (v.angleDeg * Math.PI / 180.0)
                    v.x = (v.x + cos(rad).toFloat() * v.currentSpeed * v.type.speed * 25f * dt).coerceIn(60f, 750f)
                    v.y = (v.y + sin(rad).toFloat() * v.currentSpeed * v.type.speed * 25f * dt).coerceIn(60f, 650f)
                }
            }
        }
        wastePiles.value = piles
        vehicles.value = currentVehicles
    }

    fun feedIntoIntakeHopper(type: WasteType, tons: Float) {
        val currentNodes = conveyorGrid.value.toMutableList()
        val intake = currentNodes.firstOrNull { it.type == MachineNodeType.INTAKE_HOPPER }
        if (intake != null) {
            intake.internalBufferTons += tons
            intake.currentProcessingType = type
            postAlert("📥 В бункер загружено ${tons.toInt()} т ${type.displayName}")
        }
    }

    private var conveyorTickTimer = 0f
    private fun updateConveyors(dt: Float) {
        conveyorTickTimer += dt
        if (conveyorTickTimer >= 0.5f) {
            val stepTime = conveyorTickTimer
            conveyorTickTimer = 0f

            val nodes = conveyorGrid.value
            var totalProcessedInStep = 0.0
            val newInventory = inventory.value.toMutableMap()

            nodes.forEach { node ->
                if (node.internalBufferTons > 0f) {
                    val processRate = node.type.baseProcessingRate * node.speedMultiplier * stepTime
                    val amountToProcess = min(node.internalBufferTons, processRate)
                    node.internalBufferTons -= amountToProcess
                    node.totalProcessedTons += amountToProcess
                    totalProcessedInStep += amountToProcess

                    val wType = node.currentProcessingType ?: WasteType.MIXED_TRASH
                    val outResource = wType.recyclingOutput
                    val purityFactor = (node.type.basePurity * node.filterSelectivity).coerceIn(0.6f, 1.0f)
                    val producedUnits = amountToProcess * purityFactor

                    // Add to inventory
                    val currentStored = newInventory[outResource] ?: 0.0
                    newInventory[outResource] = currentStored + producedUnits

                    // Update eco rating for hazardous / organic
                    if (wType.isHazardous) {
                        if (node.type == MachineNodeType.HAZMAT_SCRUBBER) {
                            ecoRating.value = (ecoRating.value + 0.2f).coerceAtMost(100f)
                        } else {
                            ecoRating.value = (ecoRating.value - 0.5f).coerceAtLeast(10f)
                        }
                    } else if (wType == WasteType.ORGANIC && node.type == MachineNodeType.BIO_DIGESTER) {
                        ecoRating.value = (ecoRating.value + 0.1f).coerceAtMost(100f)
                    }

                    // Forward remaining to next connected conveyor node
                    val nextNode = findConnectedNode(node, nodes)
                    if (nextNode != null && node.internalBufferTons > 0f) {
                        val transfer = min(node.internalBufferTons, nextNode.type.baseProcessingRate * stepTime)
                        node.internalBufferTons -= transfer
                        nextNode.internalBufferTons += transfer
                        nextNode.currentProcessingType = wType
                    }
                }
            }

            inventory.value = newInventory
            if (totalProcessedInStep > 0.0) {
                totalTonsRecycled.value += totalProcessedInStep
                val curStats = efficiencyStats.value
                val newThroughput = (totalProcessedInStep / stepTime) * 60.0
                val hist = (curStats.throughputHistory + newThroughput.toFloat()).takeLast(10)
                efficiencyStats.value = curStats.copy(
                    tonsProcessedToday = curStats.tonsProcessedToday + totalProcessedInStep,
                    tonsRecycledTotal = totalTonsRecycled.value,
                    currentThroughputTonsPerMin = (newThroughput * 10).roundToInt() / 10.0,
                    throughputHistory = hist
                )
            }
        }
    }

    private fun findConnectedNode(source: ConveyorNode, nodes: List<ConveyorNode>): ConveyorNode? {
        val targetX = when (source.directionDeg) {
            0 -> source.gridX + 1
            180 -> source.gridX - 1
            else -> source.gridX
        }
        val targetY = when (source.directionDeg) {
            90 -> source.gridY + 1
            270 -> source.gridY - 1
            else -> source.gridY
        }
        return nodes.find { it.gridX == targetX && it.gridY == targetY }
    }

    private var auctionTimer = 0f
    private fun updateAuction(dt: Float) {
        val auc = liveAuction.value ?: return
        if (auc.isClosed) return

        auctionTimer += dt
        if (auctionTimer >= 1f) {
            auctionTimer = 0f
            val remaining = auc.secondsRemaining - 1
            if (remaining <= 0) {
                // Auction ended!
                val wonByPlayer = auc.isPlayerLeading
                liveAuction.value = auc.copy(secondsRemaining = 0, isClosed = true, isWonByPlayer = wonByPlayer)
                if (wonByPlayer) {
                    val marginEstimate = auc.baseEstimatedValue - auc.currentHighBid
                    postAlert("🏆 ВЫ ВЫИГРАЛИ АУКЦИОН! Маржинальность контракта: +$${marginEstimate.toInt()}")
                    // Add won contract
                    val contract = RecyclingContract(
                        id = "auc_contract_${System.currentTimeMillis()}",
                        clientName = "Победитель Гостендера",
                        clientType = "Тендерный комитет",
                        targetType = auc.wasteType,
                        requiredTons = auc.volumeTons,
                        deliveredTons = 0f,
                        minimumPurityPercent = 90f,
                        payoutTotal = auc.baseEstimatedValue,
                        deadlineGameHours = 72f,
                        remainingGameHours = 72f,
                        isAuctionAwarded = true
                    )
                    activeContracts.value = activeContracts.value + contract
                } else {
                    postAlert("❌ Аукцион завершён. Победитель: ${auc.currentLeaderName}")
                }
            } else {
                // AI Competitors evaluate bidding
                var newLeader = auc.currentLeaderName
                var newHighBid = auc.currentHighBid
                var playerLeading = auc.isPlayerLeading

                if (!playerLeading && Random.nextFloat() < 0.25f) {
                    // One of the AI competitors raises bid
                    val bidder = auc.competitorBidders.random()
                    if (newHighBid + 1500.0 <= bidder.maxBudget) {
                        newHighBid += Random.nextInt(1000, 3000)
                        newLeader = bidder.name
                        playerLeading = false
                    }
                }

                liveAuction.value = auc.copy(
                    secondsRemaining = remaining,
                    currentHighBid = newHighBid,
                    currentLeaderName = newLeader,
                    isPlayerLeading = playerLeading
                )
            }
        }
    }

    private fun updateContracts(dt: Float) {
        val currentContracts = activeContracts.value.map { contract ->
            if (contract.isCompleted || contract.isFailed) return@map contract

            val remainingHours = (contract.remainingGameHours - dt / 30f).coerceAtLeast(0f)
            val isFailed = remainingHours <= 0f && contract.deliveredTons < contract.requiredTons
            val isCompleted = contract.deliveredTons >= contract.requiredTons

            if (isCompleted && !contract.isCompleted) {
                // Payout reward!
                money.value += contract.payoutTotal
                reputationXp.value += 450L
                postAlert("💰 Контракт '${contract.clientName}' выполнен! Получено +$${contract.payoutTotal.toInt()}")
            } else if (isFailed && !contract.isFailed) {
                // Penalty
                val penalty = contract.payoutTotal * 0.25
                money.value = (money.value - penalty).coerceAtLeast(0.0)
                postAlert("❌ Срыв сроков контракта '${contract.clientName}'! Неустойка -$${penalty.toInt()}")
            }

            contract.copy(
                remainingGameHours = remainingHours,
                isCompleted = isCompleted,
                isFailed = isFailed
            )
        }
        activeContracts.value = currentContracts
    }

    // --- USER ACTIONS ---

    fun playerDriveVehicle(speed: Float, turnDeg: Float) {
        val activeId = activePlayerVehicleId.value
        val list = vehicles.value.toMutableList()
        val index = list.indexOfFirst { it.id == activeId }
        if (index != -1) {
            val v = list[index]
            v.currentSpeed = speed
            v.angleDeg = (v.angleDeg + turnDeg) % 360f
            if (v.angleDeg < 0) v.angleDeg += 360f
            vehicles.value = list
        }
    }

    fun playerPerformVehicleAction() {
        val activeId = activePlayerVehicleId.value
        val list = vehicles.value.toMutableList()
        val index = list.indexOfFirst { it.id == activeId }
        if (index == -1) return
        val v = list[index]

        val piles = wastePiles.value.toMutableList()
        val intakeX = 160f
        val intakeY = 160f
        val distToIntake = sqrt((intakeX - v.x).pow(2) + (intakeY - v.y).pow(2))

        if (v.loadedTons > 0f && distToIntake < 80f) {
            // Unload into conveyor intake!
            feedIntoIntakeHopper(v.loadedType ?: WasteType.MIXED_TRASH, v.loadedTons)
            postAlert("🚜 Выгружено в дробилку: ${v.loadedTons.toInt()} т ${v.loadedType?.displayName}")
            v.loadedTons = 0f
            v.loadedType = null
        } else {
            // Try to scoop / grab closest pile
            val closePile = piles.minByOrNull { sqrt((it.x - v.x).pow(2) + (it.y - v.y).pow(2)) }
            if (closePile != null) {
                val dist = sqrt((closePile.x - v.x).pow(2) + (closePile.y - v.y).pow(2))
                if (dist < 70f) {
                    val availableCapacity = v.type.capacity - v.loadedTons
                    if (availableCapacity > 0f) {
                        val grabAmount = min(closePile.tons, availableCapacity)
                        closePile.tons -= grabAmount
                        v.loadedTons += grabAmount
                        v.loadedType = closePile.type
                        if (closePile.tons <= 0.5f) {
                            piles.remove(closePile)
                        }
                        postAlert("🚜 Захвачено ${grabAmount.toInt()} т ${v.loadedType?.displayName}!")
                    } else {
                        postAlert("⚠️ Ковш переполнен! Отвезите груз к приёмному бункеру.")
                    }
                } else {
                    postAlert("Подъедьте ближе к куче мусора или к бункеру")
                }
            }
        }
        wastePiles.value = piles
        vehicles.value = list
    }

    fun switchActiveVehicle(id: String) {
        val list = vehicles.value.map { v ->
            if (v.id == id) {
                v.copy(isPlayerDriven = true, isAutoPilot = false)
            } else {
                v.copy(isPlayerDriven = false, isAutoPilot = true)
            }
        }
        vehicles.value = list
        activePlayerVehicleId.value = id
    }

    fun toggleVehicleAutoPilot(id: String) {
        val list = vehicles.value.map { v ->
            if (v.id == id) {
                v.copy(isAutoPilot = !v.isAutoPilot, isPlayerDriven = false)
            } else v
        }
        vehicles.value = list
    }

    fun sellResource(type: ResourceType, tons: Double) {
        val curInv = inventory.value.toMutableMap()
        val available = curInv[type] ?: 0.0
        if (available >= tons && tons > 0) {
            val commodity = marketCommodities.value.find { it.type == type }
            val pricePerUnit = commodity?.currentPricePerUnit ?: type.baseMarketPrice
            val totalEarned = tons * pricePerUnit

            curInv[type] = available - tons
            inventory.value = curInv
            money.value += totalEarned
            reputationXp.value += (tons * 2).toLong()

            // Also check if any active contracts need this resource!
            val contractList = activeContracts.value.map { c ->
                if (c.targetType.recyclingOutput == type && !c.isCompleted && !c.isFailed) {
                    val needed = c.requiredTons - c.deliveredTons
                    val toDeliver = min(tons.toFloat(), needed)
                    c.copy(deliveredTons = c.deliveredTons + toDeliver)
                } else c
            }
            activeContracts.value = contractList

            postAlert("💵 Продано $tons ${type.unit} '${type.displayName}' на +$${totalEarned.toInt()}")
        } else {
            postAlert("❌ Недостаточно ресурса на складе!")
        }
    }

    fun placeAuctionBid(bidAmount: Double) {
        val auc = liveAuction.value ?: return
        if (auc.isClosed) return
        if (money.value < bidAmount) {
            postAlert("❌ Недостаточно средств для ставки ($${bidAmount.toInt()})!")
            return
        }
        if (bidAmount <= auc.currentHighBid) {
            postAlert("❌ Ставка должна быть выше текущей ($${auc.currentHighBid.toInt()})!")
            return
        }

        liveAuction.value = auc.copy(
            currentHighBid = bidAmount,
            currentLeaderName = "Ваш Завод (Игрок)",
            isPlayerLeading = true,
            secondsRemaining = max(auc.secondsRemaining, 8) // Add anti-snipe time!
        )
        postAlert("⚡ Вы сделали лидирующую ставку: $${bidAmount.toInt()}!")
    }

    fun startNewAuction() {
        val randomType = WasteType.values().random()
        val vol = Random.nextInt(40, 120).toFloat()
        val baseVal = vol * randomType.basePricePerTon * 2.8
        val startingBid = (baseVal * 0.4 / 1000).toInt() * 1000.0
        liveAuction.value = LiveAuction(
            id = "auc_${System.currentTimeMillis()}",
            title = "Контрактный аукцион: Крупная партия ${randomType.displayName}",
            wasteType = randomType,
            volumeTons = vol,
            baseEstimatedValue = (baseVal / 100).toInt() * 100.0,
            currentHighBid = startingBid,
            currentLeaderName = "ScrapGlobal LLC",
            isPlayerLeading = false,
            secondsRemaining = 28,
            competitorBidders = listOf(
                AuctionCompetitor("GreenApex Industries", startingBid * 1.8, 0.85f),
                AuctionCompetitor("ScrapGlobal LLC", startingBid * 1.6, 0.90f),
                AuctionCompetitor("BioCleanse Group", startingBid * 1.4, 0.70f)
            )
        )
        postAlert("📢 Объявлен новый аукцион контракта!")
    }

    fun hireStaff(role: StaffRole) {
        val cost = role.baseSalary * 5.0
        if (money.value >= cost) {
            money.value -= cost
            val names = listOf("Дмитрий Власов", "Ольга Смирнова", "Игорь Кравцов", "Татьяна Белова", "Максим Орлов")
            val newStaff = StaffMember(
                id = "s_${System.currentTimeMillis()}",
                name = names.random(),
                role = role,
                avatarEmoji = when (role) {
                    StaffRole.OPERATOR -> "👷‍♂️"
                    StaffRole.SORTER -> "👩‍🔧"
                    StaffRole.ENGINEER -> "👨‍🏭"
                    StaffRole.ECO_INSPECTOR -> "👩‍🔬"
                    StaffRole.DISPATCHER -> "🧑‍💼"
                },
                level = 1,
                salaryDaily = role.baseSalary,
                moralePercent = 100f,
                efficiencyBonus = 1.20f,
                isAssigned = true
            )
            staffMembers.value = staffMembers.value + newStaff
            postAlert("👤 Нанят новый сотрудник: ${newStaff.name} (${role.title})")
        } else {
            postAlert("❌ Недостаточно средств для найма ($${cost.toInt()})")
        }
    }

    fun unlockTech(techId: String) {
        val list = techTree.value.toMutableList()
        val index = list.indexOfFirst { it.id == techId }
        if (index != -1) {
            val tech = list[index]
            if (money.value >= tech.cost && !tech.isUnlocked) {
                money.value -= tech.cost
                list[index] = tech.copy(isUnlocked = true)
                techTree.value = list
                postAlert("🔬 Исследование '${tech.title}' успешно завершено!")
            } else {
                postAlert("❌ Недостаточно средств ($${tech.cost.toInt()})")
            }
        }
    }

    fun addOrUpdateConveyorNode(gridX: Int, gridY: Int, type: MachineNodeType, directionDeg: Int) {
        val list = conveyorGrid.value.toMutableList()
        val existingIndex = list.indexOfFirst { it.gridX == gridX && it.gridY == gridY }
        if (existingIndex != -1) {
            // Update node
            list[existingIndex] = list[existingIndex].copy(type = type, directionDeg = directionDeg)
            postAlert("🔧 Конфигурация конвейерного узла [$gridX, $gridY] обновлена")
        } else {
            // Buy and place new node
            if (money.value >= type.cost) {
                money.value -= type.cost
                val newNode = ConveyorNode(
                    id = "c_${gridX}_${gridY}",
                    gridX = gridX,
                    gridY = gridY,
                    type = type,
                    directionDeg = directionDeg,
                    speedMultiplier = 1.0f
                )
                list.add(newNode)
                postAlert("🏗️ Установлен новый узел: ${type.displayName} (-$${type.cost.toInt()})")
            } else {
                postAlert("❌ Недостаточно средств для постройки ($${type.cost.toInt()})")
                return
            }
        }
        conveyorGrid.value = list
    }

    fun removeConveyorNode(gridX: Int, gridY: Int) {
        val list = conveyorGrid.value.toMutableList()
        val index = list.indexOfFirst { it.gridX == gridX && it.gridY == gridY }
        if (index != -1) {
            val node = list.removeAt(index)
            val refund = node.type.cost * 0.65
            money.value += refund
            conveyorGrid.value = list
            postAlert("🗑️ Узел демонтирован (+$$refund возврат)")
        }
    }

    fun updateNodeParameters(gridX: Int, gridY: Int, speedMultiplier: Float, pressureBar: Float, filterSelectivity: Float) {
        val list = conveyorGrid.value.toMutableList()
        val index = list.indexOfFirst { it.gridX == gridX && it.gridY == gridY }
        if (index != -1) {
            val current = list[index]
            list[index] = current.copy(
                speedMultiplier = speedMultiplier,
                pressureBar = pressureBar,
                filterSelectivity = filterSelectivity
            )
            conveyorGrid.value = list
            postAlert("⚙️ Параметры узла [${gridX}, ${gridY}] оптимизированы!")
        }
    }

    fun upgradeVehicle(vehicleId: String) {
        val list = vehicles.value.toMutableList()
        val index = list.indexOfFirst { it.id == vehicleId }
        if (index != -1) {
            val v = list[index]
            val upgradeCost = v.level * 4500.0
            if (money.value >= upgradeCost) {
                money.value -= upgradeCost
                list[index] = v.copy(
                    level = v.level + 1,
                    conditionPercent = 100f
                )
                vehicles.value = list
                postAlert("🛠️ Машина '${v.type.displayName}' улучшена до уровня ${v.level + 1}!")
            } else {
                postAlert("❌ Недостаточно средств ($${upgradeCost.toInt()})")
            }
        }
    }
}
