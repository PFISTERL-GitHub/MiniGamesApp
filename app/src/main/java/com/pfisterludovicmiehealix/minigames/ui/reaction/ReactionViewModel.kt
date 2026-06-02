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
import kotlin.random.Random

// MODEL
data class ReactionParams(
    val startValue: Long,       // valeur de départ du timer (ms)
    val targetValue: Long,      // valeur cible à atteindre (ms)
    val speedFactor: Float,     // 0.5× à 2.0×
    val isIncrementing: Boolean // true = croissant, false = décroissant
)

enum class GamePhase {
    IDLE,       // en attente du demarrage
    RUNNING,    // timer en cours
    RESULT      // partie terminee, affichage du resultat
}

private fun generateGameParams(): ReactionParams {
    val incre  = Random.nextBoolean()
    val target = if (incre) Random.nextLong(1_000L, 10_000L)
    else       Random.nextLong(1_000L,  8_000L)
    val speed  = Random.nextFloat() * 1.5f + 0.5f
    val start  = if (incre) Random.nextLong(0L, target)
    else       Random.nextLong(target + 500L, target + 2_000L)
    return ReactionParams(start, target, speed, incre)
}

private fun Long.formatMs(): String {
    val a   = abs(this)
    val min = a / 60_000
    val sec = (a % 60_000) / 1_000
    val ms  = a % 1_000
    return if (min > 0) "%d:%02d.%03d".format(min, sec, ms)
    else                "%d.%03d".format(sec, ms)
}

// Retourne un message selon l'ecart en millisecondes
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

class ReactionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ReactionUiState(
        params = generateParams(),
    ).let { it.copy(timer = it.params.startValue) })
    val uiState: StateFlow<ReactionUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun startGame() { // genere une partie et lance le timer
        _uiState.update { it.copy(phase = GamePhase.RUNNING, timer = it.params.startValue) }
        timerJob = viewModelScope.launch {
            val tickMs = 16L
            while (_uiState.value.phase == GamePhase.RUNNING) {
                delay(tickMs)
                val delta = (tickMs * _uiState.value.params.speedFactor).toLong()
                _uiState.update { state ->
                    state.copy(
                        timer = if (state.params.isIncrementing) state.timer + delta
                        else                             state.timer - delta
                    )
                }
            }
        }
    }

    fun stopTimer() { // stoppe et calcule l'ecart
        timerJob?.cancel()
        _uiState.update { state ->
            state.copy(
                phase = GamePhase.RESULT,
                gap   = abs(state.timer - state.params.targetValue)
            )
        }
    }

    fun reset() { // retour a l'etat initial
        timerJob?.cancel()
        val newParams = generateParams()
        _uiState.update {
            ReactionUiState(params = newParams, timer = newParams.startValue)
        }
    }
}