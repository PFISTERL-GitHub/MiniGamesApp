package com.pfisterludovicmiehealix.minigames.ui.reaction

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random
import com.pfisterludovicmiehealix.minigames.ui.theme.*

@Composable
fun ReactionScreen(onBackClick: () -> Unit) {
    var params by remember { mutableStateOf(generateGameParams()) }
    var phase  by remember { mutableStateOf(GamePhase.IDLE) }
    var timer  by remember { mutableLongStateOf(params.startValue) }
    var gap    by remember { mutableLongStateOf(0L) }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 28.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        // ── Bloc supérieur : titre + infos ────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text       = "Jeu de Réaction",
                color      = AppWhite,
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold
            )

            // Infos de la partie dans une surface unifiée
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AppBorder, RoundedCornerShape(4.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    InfoRow(label = "Cible",   value = params.targetValue.formatMs(), valueColor = AppBlueLight)
                    HorizontalDivider(color = AppBorder)
                    InfoRow(
                        label = "Sens",
                        value = if (params.isIncrementing) "▲  Croissant" else "▼  Décroissant",
                        valueColor = if (params.isIncrementing) AppGreen else Color(0xFFFF5252)
                    )
                    HorizontalDivider(color = AppBorder)
                    InfoRow(
                        label = "Vitesse",
                        value = "×${"%.1f".format(params.speedFactor)}",
                        valueColor = AppWhite
                    )
                }
            }
        }

        // ── Bloc central : timer ──────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text       = timer.formatMs(),
                fontSize   = 56.sp,
                fontWeight = FontWeight.Black,
                color      = if (phase == GamePhase.RUNNING) AppGreen else AppWhite,
                textAlign  = TextAlign.Center
            )

            // Résultat (visible uniquement en phase RESULT)
            if (phase == GamePhase.RESULT) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text       = "Écart : ${gap.formatMs()}",
                    color      = AppGrey,
                    fontSize   = 16.sp
                )
                Text(
                    text       = feedbackMessage(gap),
                    color      = AppWhite,
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign  = TextAlign.Center
                )
            }
        }

        // ── Bloc inférieur : boutons ──────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (phase) {
                // Bouton circulaire Démarrer
                GamePhase.IDLE -> {
                    CircleActionButton(
                        label   = "Démarrer",
                        color   = AppBlue,
                        onClick = {
                            timer = params.startValue
                            phase = GamePhase.RUNNING
                        }
                    )
                }

                // Bouton circulaire Stop
                GamePhase.RUNNING -> {
                    CircleActionButton(
                        label   = "Stop !",
                        color   = Color(0xFFFF1744),
                        onClick = {
                            gap   = abs(timer - params.targetValue)
                            phase = GamePhase.RESULT
                        }
                    )
                }

                // Boutons Rejouer + Accueil
                GamePhase.RESULT -> {
                    Button(
                        onClick  = { resetGame() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape  = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppBlue)
                    ) {
                        Text("Rejouer", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Bouton retour toujours visible
            TextButton(onClick = onBackClick) {
                Text(
                    text  = "Retour à l'accueil",
                    color = AppGrey,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// ── Composants utilitaires ────────────────────────────────────────────────────

@Composable
private fun CircleActionButton(label: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(160.dp)
            .clip(CircleShape)
            .background(color)
            .then(
                Modifier.clickable(onClick = onClick)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = label,
            color      = AppWhite,
            fontSize   = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign  = TextAlign.Center
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = AppGrey, fontSize = 14.sp)
        Text(text = value, color = valueColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}