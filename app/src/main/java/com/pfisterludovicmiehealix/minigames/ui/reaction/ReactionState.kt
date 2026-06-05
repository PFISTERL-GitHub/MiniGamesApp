package com.pfisterludovicmiehealix.minigames.ui.reaction

import kotlin.math.abs
import kotlin.random.Random

data class ReactionParams(
    val startValue: Long,
    val targetValue: Long,
    val speedFactor: Float,
    val isIncrementing: Boolean
)

enum class GamePhase { IDLE, RUNNING, RESULT }

data class ReactionUiState(
    val params: ReactionParams = generateGameParams(),
    val phase: GamePhase       = GamePhase.IDLE,
    val timer: Long            = 0L,
    val gap: Long              = 0L,
)

internal fun generateGameParams(): ReactionParams {
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
