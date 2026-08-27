package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_save")
data class GameSaveEntity(
    @PrimaryKey val id: Int = 1,
    val plantName: String,
    val money: Double,
    val plantLevel: Int,
    val reputationXp: Long,
    val ecoRating: Float, // 0.0 - 100.0%
    val totalRecycledTons: Double,
    val inventoryJson: String,
    val conveyorLayoutJson: String,
    val unlockedTechsJson: String,
    val staffJson: String,
    val unlockedZonesJson: String,
    val gameTimeTicks: Long,
    val timestamp: Long = System.currentTimeMillis()
)
