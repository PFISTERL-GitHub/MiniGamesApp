package com.pfisterludovicmiehealix.minigames.ui.reaction

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pfisterludovicmiehealix.minigames.data.AppDatabase
import com.pfisterludovicmiehealix.minigames.data.Score
import com.pfisterludovicmiehealix.minigames.data.ScoreRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random

// MODEL

data class ReactionParams(
    val startValue: Long,
    val targetValue: Long,
    val speedFactor: Float,
    val isIncrementing: Boolean
)

enum class GamePhase { IDLE, RUNNING, RESULT }

data class ReactionUiState(
    val params: ReactionParams  = generateGameParams(),
    val phase: GamePhase        = GamePhase.IDLE,
    val timer: Long             = 0L,
    val gap: Long               = 0L,
)

private fun generateGameParams(): ReactionParams {
    val incre  = Random.nextBoolean()
    val target = if (incre) Random.nextLong(1_000L, 10_000L)
                 else       Random.nextLong(1_000L,  8_000L)
    val speed  = Random.nextFloat() * 1.5f + 0.5f
    val start  = if (incre) Random.nextLong(0L, target)
                 else       Random.nextLong(target + 500L, target + 2_000L)
    return ReactionParams(start, target, speed, incre)
}

fun Long.formatMs(): String {
    val a   = abs(this)
    val min = a / 60_000
    val sec = (a % 60_000) / 1_000
    val ms  = a % 1_000
    return if (min > 0) "%d:%02d.%03d".format(min, sec, ms)
    else                "%d.%03d".format(sec, ms)
}

fun feedbackMessage(gap: Long): String {
    return when {
        gap < 10   -> "SSSensationnel"
        gap < 100  -> "Excellent !"
        gap < 300  -> "Tres bien"
        gap < 600  -> "Pas mal"
        gap < 1000 -> "Peut mieux faire"
        else       -> "Mauvais"
    }
}

// VIEWMODEL

class ReactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScoreRepository(AppDatabase.getDatabase(application).scoreDao())
    private var playerName = ""

    private val _uiState = MutableStateFlow(ReactionUiState(
        params = generateGameParams(),
    ).let { it.copy(timer = it.params.startValue) })
    val uiState: StateFlow<ReactionUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun startGame(playerName: String) {
        this.playerName = playerName
        _uiState.update { it.copy(phase = GamePhase.RUNNING, timer = it.params.startValue) }
        timerJob = viewModelScope.launch {
            val tickMs = 16L
            while (_uiState.value.phase == GamePhase.RUNNING) {
                delay(tickMs)
                val delta = (tickMs * _uiState.value.params.speedFactor).toLong()
                _uiState.update { state ->
                    state.copy(
                        timer = if (state.params.isIncrementing) state.timer + delta
                        else                                     state.timer - delta
                    )
                }
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        _uiState.update { state ->
            state.copy(phase = GamePhase.RESULT, gap = abs(state.timer - state.params.targetValue))
        }
        saveScore()
    }

    fun reset() {
        timerJob?.cancel()
        val newParams = generateGameParams()
        _uiState.update {
            ReactionUiState(params = newParams, timer = newParams.startValue)
        }
    }

    private fun saveScore() {
        viewModelScope.launch {
            repository.insertScore(
                Score(
                    playerName = playerName,
                    gameName   = "Reaction",
                    score      = maxOf(0, 1000 - _uiState.value.gap.toInt())
                )
            )
        }
    }
}
