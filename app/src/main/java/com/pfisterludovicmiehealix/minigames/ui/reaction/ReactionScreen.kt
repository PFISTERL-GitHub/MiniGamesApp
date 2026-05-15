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

// Retourne un message selon l'ecart en millisecondes
fun feedbackMessage(gap: Long): String {
    return when {
        gap < 1   -> "SSSensationnel"
        gap < 100  -> "Excellent !"
        gap < 300  -> "Tres bien"
        gap < 600  -> "Pas mal"
        gap < 1000 -> "Peut mieux faire"
        else       -> "Mauvais"
    }
}

@Composable
fun ReactionScreen(onBackClick: () -> Unit) {
    // --- Etats ---
    var hasStarted by remember { mutableStateOf(false) }
    var hasStopped by remember { mutableStateOf(false) }

    // --- Timer ---
    var elapsedTime by remember { mutableLongStateOf(0L) }

    // --- Resultat ---
    var gap by remember { mutableLongStateOf(0L) }

    // Parametres fixes
    val targetValue = 5_000L
    val speed = 1.0f
    val isIncrementing = true

    // --- Fonction de reinitialisation pour "Rejouer" ---
    fun resetGame() {
        hasStarted = false
        hasStopped = false
        elapsedTime = 0L
        gap = 0L
    }

    // --- Timer ---
    LaunchedEffect(hasStarted) {
        if (!hasStarted) return@LaunchedEffect

        var lastFrameTime = -1L

        while (!hasStopped) {
            withFrameMillis { frameTime ->
                if (lastFrameTime < 0L) {
                    // Premier frame : on initialise sans incrementer
                    lastFrameTime = frameTime
                } else {
                    val delta = ((frameTime - lastFrameTime) * speed).toLong()
                    lastFrameTime = frameTime

                    elapsedTime = if (isIncrementing) {
                        elapsedTime + delta
                    } else {
                        elapsedTime - delta
                    }
                }
            }
        }
        // Calcul de l'ecart une fois le timer stoppe
        gap = abs(elapsedTime - targetValue)
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
        if (hasStopped) {
            // --- Phase de resultat ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Valeur atteinte vs cible
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Cible",
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
                                text = "Ecart",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                text = "$gap ms",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                // Message de feedback
                Text(
                    text = feedbackMessage(gap),
                    fontSize = 32.sp,
                    textAlign = TextAlign.Center
                )
            }

        } else {
            // --- Phase de jeu ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Valeur cible
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Valeur cible",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$targetValue ms",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }

                // Timer
                Text(
                    text = "$elapsedTime ms",
                    fontSize = 56.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // --- Zone des boutons ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (hasStopped) {
                Button(
                    onClick = { resetGame() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Rejouer")
                }
            } else if (!hasStarted) {
                Button(
                    onClick = { hasStarted = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Demarrer")
                }
            } else {
                Button(
                    onClick = { hasStopped = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Stop !")
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