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

class ReactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScoreRepository(AppDatabase.getDatabase(application).scoreDao())
    private var playerName = ""

    private val _uiState = MutableStateFlow(ReactionUiState())
    val uiState: StateFlow<ReactionUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun startGame() {
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
        _uiState.update { ReactionUiState(params = generateGameParams()) }
    }
}
