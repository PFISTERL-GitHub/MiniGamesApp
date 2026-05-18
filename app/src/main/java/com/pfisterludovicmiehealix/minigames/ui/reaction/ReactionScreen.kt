package com.pfisterludovicmiehealix.minigames.ui.reaction

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

enum class GamePhase {
    IDLE,       // en attente du demarrage
    RUNNING,    // timer en cours
    RESULT      // partie terminee, affichage du resultat
}

// Retourne un message selon l'ecart en millisecondes
fun feedbackMessage(gap: Long): String {
    return when {
        gap <=0    -> "debug: gap = 0"
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
    // --- Etat unique de phase ---
    var phase by remember { mutableStateOf(GamePhase.IDLE) }

    // --- Timer ---
    var elapsedTimeFloat by remember { mutableFloatStateOf(0f) }
    val elapsedTime = elapsedTimeFloat.toLong()

    // --- Resultat ---
    var gap by remember { mutableLongStateOf(0L) }

    // --- Parametres aleatoires ---
    var targetValue by remember { mutableLongStateOf(0L) }
    var speed by remember { mutableFloatStateOf(1.0f) }
    var isIncrementing by remember { mutableStateOf(true) }

    // --- Generation des parametres ---
    fun generateGameParams() {
        targetValue = (1_000..10_000).random().toLong()
        speed = (5..20).random() / 10f
        isIncrementing = listOf(true, false).random()
    }


    // --- Fonction de reinitialisation pour "Rejouer" ---
    fun resetGame() {
        generateGameParams()
        phase = GamePhase.IDLE
        elapsedTimeFloat = if (isIncrementing) 0f else targetValue.toFloat()
        gap = 0L
    }

    LaunchedEffect(Unit) {
        generateGameParams()
        elapsedTimeFloat = if (isIncrementing) 0f else targetValue.toFloat()
    }

    // --- Timer ---
    LaunchedEffect(phase) {
        if (phase != GamePhase.RUNNING) return@LaunchedEffect

        var lastFrameTime = -1L

        while (phase == GamePhase.RUNNING) {
            withFrameMillis { frameTime ->
                if (lastFrameTime < 0L) {
                    // Premier frame : on initialise sans incrementer
                    lastFrameTime = frameTime
                } else {
                    val delta = ((frameTime - lastFrameTime) * speed)
                    lastFrameTime = frameTime

                    elapsedTimeFloat = if (isIncrementing) {
                        elapsedTimeFloat + delta
                    } else {
                        elapsedTimeFloat - delta
                    }
                }
            }
        }
        // Calcul de l'ecart une fois le timer stoppe
        gap = if (isIncrementing) {
            abs(elapsedTimeFloat.toLong() - targetValue)  // ecart avec la cible
        } else {
            abs(elapsedTimeFloat.toLong())                 // ecart avec 0
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // En-tete
        Text(
            text = "Jeu de reaction",
            style = MaterialTheme.typography.headlineMedium
        )

        // Zone centrale : jeu ou resultat
        when (phase) {
            GamePhase.IDLE, GamePhase.RUNNING -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (isIncrementing) "Valeur cible" else "Valeur de depart",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$targetValue ms",
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }

                    Text(
                        text = "$elapsedTime ms",
                        fontSize = 56.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            GamePhase.RESULT -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Valeur cible
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isIncrementing) "Cible" else "Valeur de depart",
                                    style = MaterialTheme.typography.labelLarge
                                )
                                Text(
                                    text = "$targetValue ms",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Votre arret",
                                    style = MaterialTheme.typography.labelLarge
                                )
                                Text(
                                    text = "$elapsedTime ms",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            HorizontalDivider()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isIncrementing) "Ecart avec la cible" else "Ecart avec 0",
                                    style = MaterialTheme.typography.labelLarge
                                )
                                Text(
                                    text = "$gap ms",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }

                    Text(
                        text = feedbackMessage(gap),
                        fontSize = 32.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // --- Zone des boutons ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (phase) {
                GamePhase.IDLE -> {
                    Button(
                        onClick = { phase = GamePhase.RUNNING },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Demarrer")
                    }
                }

                GamePhase.RUNNING -> {
                    Button(
                        onClick = { phase = GamePhase.RESULT },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Stop !")
                    }
                }

                GamePhase.RESULT -> {
                    Button(
                        onClick = { resetGame() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Rejouer")
                    }
                }
            }

            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Retour a l'accueil")
            }
        }
    }
}