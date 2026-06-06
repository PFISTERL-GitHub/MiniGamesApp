package com.pfisterludovicmiehealix.minigames.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScoreDao {
    @Insert
    suspend fun insertScore(score: Score)

    @Query("SELECT * FROM score ORDER BY score DESC LIMIT 10")
    suspend fun getTopScores(): List<Score>
}
