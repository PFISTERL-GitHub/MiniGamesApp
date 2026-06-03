package com.pfisterludovicmiehealix.minigames.data

class ScoreRepository(private val dao: ScoreDao) {
    suspend fun insertScore(score: Score) = dao.insertScore(score)
    suspend fun getTopScores(): List<Score> = dao.getTopScores()
}
