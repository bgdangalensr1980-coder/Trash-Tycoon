package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.GameRepository
import com.example.data.GameSaveEntity
import com.example.engine.GameSimulationEngine
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject

enum class ActiveScreenTab(val title: String, val iconName: String) {
    YARD_2D("Полигон 2D", "factory"),
    CONVEYOR_EDITOR("Линии & Прессы", "schema"),
    GLOBAL_MARKET("Рынок Вторсырья", "trending_up"),
    CONTRACTS_AUCTION("Контракты & Аукционы", "gavel"),
    STAFF_MGMT("Персонал", "groups"),
    ECO_COMPLIANCE("Эко-Надзор", "eco"),
    TECH_TREE("Прокачка & НИОКР", "science"),
    ANALYTICS("Аналитика OEE", "analytics")
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository
    val engine = GameSimulationEngine()

    // Navigation Tab
    private val _currentTab = MutableStateFlow(ActiveScreenTab.YARD_2D)
    val currentTab: StateFlow<ActiveScreenTab> = _currentTab.asStateFlow()

    // Selected node for configuration in conveyor editor
    private val _selectedConveyorNode = MutableStateFlow<ConveyorNode?>(null)
    val selectedConveyorNode: StateFlow<ConveyorNode?> = _selectedConveyorNode.asStateFlow()

    // Selected commodity in market
    private val _selectedCommodity = MutableStateFlow<MarketCommodity?>(null)
    val selectedCommodity: StateFlow<MarketCommodity?> = _selectedCommodity.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        repository = GameRepository(db.gameDao())

        // Load saved state if any
        viewModelScope.launch {
            val save = repository.getSave()
            if (save != null) {
                engine.money.value = save.money
                engine.plantLevel.value = save.plantLevel
                engine.reputationXp.value = save.reputationXp
                engine.ecoRating.value = save.ecoRating
                engine.totalTonsRecycled.value = save.totalRecycledTons
            }
        }

        // Start continuous simulation tick loop
        viewModelScope.launch {
            var lastTime = System.currentTimeMillis()
            while (true) {
                val now = System.currentTimeMillis()
                val dt = ((now - lastTime) / 1000f).coerceIn(0.016f, 0.1f)
                lastTime = now
                engine.updateTick(dt)
                delay(33) // ~30 FPS game state update
            }
        }

        // Periodic auto-save every 10 seconds
        viewModelScope.launch {
            while (true) {
                delay(10000)
                saveGameProgress()
            }
        }
    }

    fun selectTab(tab: ActiveScreenTab) {
        _currentTab.value = tab
    }

    fun selectConveyorNode(node: ConveyorNode?) {
        _selectedConveyorNode.value = node
    }

    fun selectCommodity(commodity: MarketCommodity?) {
        _selectedCommodity.value = commodity
    }

    fun saveGameProgress() {
        viewModelScope.launch {
            val save = GameSaveEntity(
                id = 1,
                plantName = "ЭкоКомплекс 'Чистый Город'",
                money = engine.money.value,
                plantLevel = engine.plantLevel.value,
                reputationXp = engine.reputationXp.value,
                ecoRating = engine.ecoRating.value,
                totalRecycledTons = engine.totalTonsRecycled.value,
                inventoryJson = "{}",
                conveyorLayoutJson = "{}",
                unlockedTechsJson = "{}",
                staffJson = "{}",
                unlockedZonesJson = "{}",
                gameTimeTicks = System.currentTimeMillis()
            )
            repository.saveGame(save)
        }
    }
}
