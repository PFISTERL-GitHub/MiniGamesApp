package com.pfisterludovicmiehealix.minigames.ui.reaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs

class ReactionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        generateGameParams().let { p -> ReactionUiState(params = p, timer = p.startValue) }
    )
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
            state.copy(
                phase = GamePhase.RESULT,
                gap   = abs(state.timer - state.params.targetValue)
            )
        }
    }

    fun reset() {
        timerJob?.cancel()
        val newParams = generateGameParams()
        _uiState.update {
            ReactionUiState(params = newParams, timer = newParams.startValue)
        }
    }
}
