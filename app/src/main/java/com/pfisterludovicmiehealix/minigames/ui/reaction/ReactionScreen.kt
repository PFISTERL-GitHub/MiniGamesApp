package com.pfisterludovicmiehealix.minigames.ui.reaction

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random

private data class GameParams(
    val startValue: Long,       // valeur de départ du timer (ms)
    val targetValue: Long,      // valeur cible à atteindre (ms)
    val speedFactor: Float,  // 0.5× à 2.0×
    val isIncrementing: Boolean   // true = croissant, false = décroissant
)

private fun generateGameParams(): GameParams {
    val target = Random.nextLong(1_000L, 10_000L)
    val speed  = Random.nextFloat() * 1.5f + 0.5f
    val incre    = Random.nextBoolean()
    val start  = if (incre) Random.nextLong(0L, target) else Random.nextLong(target + 1L, target + 10_000L)
    return GameParams(start, target, speed, incre)
}

enum class GamePhase {
    IDLE,       // en attente du demarrage
    RUNNING,    // timer en cours
    RESULT      // partie terminee, affichage du resultat
}

private fun Long.formatMs(): String {
    val a   = abs(this)
    val min = a / 60_000
    val sec = (a % 60_000) / 1_000
    val ms  = a % 1_000
    return if (min > 0) "%d:%02d.%03d".format(min, sec, ms)
    else               "%d.%03d".format(sec, ms)
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

@Composable
fun ReactionScreen(onBackClick: () -> Unit) {
    var params by remember { mutableStateOf(generateGameParams()) }

    // --- Etat unique de phase ---
    var phase by remember { mutableStateOf(GamePhase.IDLE) }

    // --- Timer ---
    var timer by remember { mutableLongStateOf(params.startValue) }
    var gap   by remember { mutableLongStateOf(0L) }

    LaunchedEffect(phase, params) {
        if (phase == GamePhase.RUNNING) {
            val tickMs = 16L
            while (phase == GamePhase.RUNNING) {
                delay(tickMs)
                val delta = (tickMs * params.speedFactor).toLong()
                timer = if (params.isIncrementing) timer + delta else timer - delta
            }
        }
    }

    // --- Fonction de reinitialisation pour "Rejouer" ---
    fun resetGame() {
        params  = generateGameParams()
        timer = params.startValue
        phase   = GamePhase.IDLE
        gap = 0L
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {

            Text(
                text = "Jeu de Réaction",
                fontSize = 24.sp,
            )

            Text(
                text = "Cible : ${params.targetValue.formatMs()}",
            )
            Text(
                text = if (params.isIncrementing) "Sens : croissant" else "Sens : décroissant",
            )
            Text(
                text = "Vitesse : ×${"%.1f".format(params.speedFactor)}",
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = timer.formatMs(),
            )
            Spacer(Modifier.height(8.dp))

            // Résultat
            if (phase != GamePhase.RESULT) {
                Text(text = "Écart : ", fontSize = 18.sp)
                Text(
                    text = "Résultat : ",
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
            }

            // Résultat
            if (phase == GamePhase.RESULT) {
                Text(text = "Écart : ${gap.formatMs()}", fontSize = 18.sp)
                Text(
                    text = feedbackMessage(gap),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
            }

            // Boutons
            when (phase) {
                GamePhase.IDLE -> {
                    Button(
                        onClick = {
                            timer = params.startValue
                            phase = GamePhase.RUNNING
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Démarrer") }
                }

                GamePhase.RUNNING -> {
                    Button(
                        onClick = {
                            gap = abs(timer - params.targetValue)
                            phase = GamePhase.RESULT
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Stop !") }
                }

                GamePhase.RESULT -> {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { resetGame() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Rejouer")
                        }
                    }
                }
            }
            Button(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Retour a l'accueil")
            }
        }
    }
}