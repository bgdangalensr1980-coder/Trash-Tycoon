package com.example.model

data class WastePile(
    val id: String,
    val type: WasteType,
    var x: Float,
    var y: Float,
    var tons: Float,
    var vx: Float = 0f,
    var vy: Float = 0f,
    var isScooped: Boolean = false
)

data class HeavyVehicle(
    val id: String,
    val type: VehicleType,
    var x: Float,
    var y: Float,
    var angleDeg: Float,
    var currentSpeed: Float = 0f,
    var loadedTons: Float = 0f,
    var loadedType: WasteType? = null,
    var isPlayerDriven: Boolean = false,
    var isAutoPilot: Boolean = true,
    var targetX: Float = 0f,
    var targetY: Float = 0f,
    var clawStateOpen: Boolean = true,
    var armAngle: Float = 0f,
    var conditionPercent: Float = 100f,
    var level: Int = 1
)

data class ConveyorNode(
    val id: String,
    val gridX: Int,
    val gridY: Int,
    val type: MachineNodeType,
    var directionDeg: Int = 0, // 0 = right, 90 = down, 180 = left, 270 = up
    var speedMultiplier: Float = 1.0f,
    var pressureBar: Float = 120.0f,
    var filterSelectivity: Float = 0.90f,
    var isOperational: Boolean = true,
    var internalBufferTons: Float = 0f,
    var currentProcessingType: WasteType? = null,
    var jamRiskPercent: Float = 5f,
    var totalProcessedTons: Double = 0.0
)

data class StaffMember(
    val id: String,
    val name: String,
    val role: StaffRole,
    val avatarEmoji: String,
    var level: Int = 1,
    var salaryDaily: Double,
    var moralePercent: Float = 95f,
    var efficiencyBonus: Float = 1.15f,
    var isAssigned: Boolean = true
)

enum class StaffRole(
    val title: String,
    val perkDescription: String,
    val baseSalary: Double
) {
    OPERATOR("Оператор спецтехники", "+25% к скорости погрузки и вместимости ковша", 180.0),
    SORTER("Сортировщик конвейера", "+20% к чистоте фракций и снижению засоров", 120.0),
    ENGINEER("Главный механик", "-50% риска аварий узлов и быстрый ремонт", 240.0),
    ECO_INSPECTOR("Эколог-аудитор", "-60% риска экологических штрафов и проверок", 210.0),
    DISPATCHER("Логист-диспетчер", "+30% к частоте прибытия фур с дорогим ломом", 160.0)
}

data class MarketCommodity(
    val type: ResourceType,
    var currentPricePerUnit: Double,
    var previousPrice: Double,
    val priceHistory: List<Double>,
    val priceChange24h: Double,
    val supplyDemandStatus: String
)

data class RecyclingContract(
    val id: String,
    val clientName: String,
    val clientType: String, // Муниципалитет, Авиазавод, Автоконцерн, IT-корпорация
    val targetType: WasteType,
    val requiredTons: Float,
    var deliveredTons: Float = 0f,
    val minimumPurityPercent: Float,
    val payoutTotal: Double,
    val deadlineGameHours: Float,
    var remainingGameHours: Float,
    val isCompleted: Boolean = false,
    val isFailed: Boolean = false,
    val isAuctionAwarded: Boolean = false
)

data class LiveAuction(
    val id: String,
    val title: String,
    val wasteType: WasteType,
    val volumeTons: Float,
    val baseEstimatedValue: Double,
    var currentHighBid: Double,
    var currentLeaderName: String,
    var isPlayerLeading: Boolean = false,
    var secondsRemaining: Int = 25,
    val competitorBidders: List<AuctionCompetitor>,
    var isClosed: Boolean = false,
    var isWonByPlayer: Boolean = false
)

data class AuctionCompetitor(
    val name: String,
    val maxBudget: Double,
    val aggressionFactor: Float
)

data class EcoViolation(
    val id: String,
    val title: String,
    val fineAmount: Double,
    val cause: String,
    val timestampFormatted: String
)

data class TechUpgrade(
    val id: String,
    val title: String,
    val category: String, // Логистика, Механика, Экология, ИИ
    val description: String,
    val cost: Double,
    val icon: String,
    var isUnlocked: Boolean = false,
    val prerequisiteId: String? = null
)

data class ProductionEfficiencyStats(
    val tonsProcessedToday: Double = 0.0,
    val tonsRecycledTotal: Double = 0.0,
    val currentThroughputTonsPerMin: Double = 0.0,
    val averagePurityPercent: Float = 88.5f,
    val energyConsumptionKw: Double = 145.0,
    val grossRevenuePerHour: Double = 0.0,
    val netProfitPerHour: Double = 0.0,
    val overallEquipmentEffectiveness: Float = 91.2f,
    val throughputHistory: List<Float> = emptyList(),
    val revenueHistory: List<Float> = emptyList()
)
