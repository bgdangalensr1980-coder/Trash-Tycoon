package com.example.data

import kotlinx.coroutines.flow.Flow

class GameRepository(private val gameDao: GameDao) {
    val gameSaveFlow: Flow<GameSaveEntity?> = gameDao.getGameSaveFlow()

    suspend fun getSave(): GameSaveEntity? = gameDao.getGameSaveOnce()

    suspend fun saveGame(save: GameSaveEntity) = gameDao.saveGame(save)

    suspend fun resetGame() = gameDao.resetGame()
}
