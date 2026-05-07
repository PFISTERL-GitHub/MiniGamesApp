package com.pfisterludovicmiehealix.minigames.ui.reaction

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ReactionScreen(onBackClick: () -> Unit) {
    // --- Etats ---
    var hasStarted by remember { mutableStateOf(false) }
    var hasStopped by remember { mutableStateOf(false) }
    var elapsedTime by remember { mutableLongStateOf(0L) }

    // Parametres fixes
    val targetValue = 5_000L
    val speed = 1.0f
    val isIncrementing = true

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

        // Zone centrale : cible et timer
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

        // Zone des boutons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!hasStarted) {
                Button(
                    onClick = { hasStarted = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Demarrer")
                }
            } else if (!hasStopped) {
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