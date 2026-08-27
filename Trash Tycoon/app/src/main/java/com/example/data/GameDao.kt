package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM game_save WHERE id = 1")
    fun getGameSaveFlow(): Flow<GameSaveEntity?>

    @Query("SELECT * FROM game_save WHERE id = 1")
    suspend fun getGameSaveOnce(): GameSaveEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGame(save: GameSaveEntity)

    @Query("DELETE FROM game_save")
    suspend fun resetGame()
}
