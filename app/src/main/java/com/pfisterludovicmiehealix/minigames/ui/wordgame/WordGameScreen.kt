package com.pfisterludovicmiehealix.minigames.ui.wordgame

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pfisterludovicmiehealix.minigames.ui.theme.*
import com.pfisterludovicmiehealix.minigames.ui.theme.MiniGamesAppTheme

@Composable
fun WordGameScreen(
    onBackClick: () -> Unit,
    viewModel: WordGameViewModel = viewModel()
) {
    LaunchedEffect(Unit) { viewModel.startGame() }

    val phase     by viewModel.phase.collectAsState()
    val grid      by viewModel.grid.collectAsState()
    val score     by viewModel.score.collectAsState()
    val timer     by viewModel.timer.collectAsState()
    val hintUsed  by viewModel.hintUsed.collectAsState()
    val bestScore by viewModel.bestScore.collectAsState()

    when (phase) {
        WordGameViewModel.Phase.PLAYING -> PlayingScreen(
            timer       = timer,
            score       = score,
            grid        = grid,
            hintUsed    = hintUsed,
            onErase     = viewModel::eraseLast,
            onCellClick = viewModel::selectCell,
            onValidate  = viewModel::validate,
            onPass      = viewModel::pass,
            onHint      = viewModel::useHint,
            onBackClick = onBackClick
        )
        WordGameViewModel.Phase.GAME_OVER -> GameOverScreen(
            score     = score,
            bestScore = bestScore,
            onReplay  = viewModel::reset,
            onBack    = onBackClick
        )
    }
}

@Composable
private fun PlayingScreen(
    timer: Int,
    score: Int,
    grid: WordGameViewModel.WordGrid,
    hintUsed: Boolean,
    onErase: () -> Unit,
    onCellClick: (Int) -> Unit,
    onValidate: () -> Unit,
    onPass: () -> Unit,
    onHint: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 28.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        GameHeader(title = "Mot Caché", timer = timer, score = score)
        WordHintCard(letterCount = grid.word.length)
        HintButton(hintUsed = hintUsed, onHint = onHint)
        InputZone(input = grid.input, onErase = onErase)
        LetterGrid(cells = grid.cells, isFull = grid.input.length >= grid.word.length, onCellClick = onCellClick)
        ActionButtons(onValidate = onValidate, onPass = onPass)
        TextButton(onClick = onBackClick) {
            Text("Retour à l'accueil", color = AppGrey, fontSize = 14.sp)
        }
    }
}

@Composable
private fun GameOverScreen(score: Int, bestScore: Int, onReplay: () -> Unit, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 28.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Partie terminée", color = AppWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Text("Score final", color = AppGrey, fontSize = 16.sp)
        Text("$score", color = AppGreen, fontSize = 64.sp, fontWeight = FontWeight.Black)
        if (bestScore > score) {
            Text("Meilleur score : $bestScore", color = AppGrey, fontSize = 14.sp)
        }
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onReplay,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppBlue)
        ) {
            Text("Rejouer", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onBack) {
            Text("Retour à l'accueil", color = AppGrey, fontSize = 14.sp)
        }
    }
}

@Composable
private fun GameHeader(title: String, timer: Int, score: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = AppWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(text = "Score : $score", color = AppGrey, fontSize = 16.sp)
        Text(text = "$timer", color = AppGreen, fontSize = 32.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun WordHintCard(letterCount: Int) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AppBorder, RoundedCornerShape(4.dp))
    ) {
        Text(
            text = "Mot de $letterCount lettres",
            color = AppWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun InputZone(input: String, onErase: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(6) { index ->
                val letter = input.getOrNull(index)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, AppBorder, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = letter?.toString() ?: "_",
                        color = if (letter != null) AppWhite else AppGrey,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(Modifier.width(12.dp))
        TextButton(onClick = onErase) {
            Text("←", color = AppGrey, fontSize = 20.sp)
        }
    }
}

@Composable
private fun LetterGrid(cells: List<WordGameViewModel.Cell>, isFull: Boolean, onCellClick: (Int) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        cells.chunked(3).forEachIndexed { rowIndex, rowCells ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowCells.forEachIndexed { colIndex, cell ->
                    val index = rowIndex * 3 + colIndex
                    Button(
                        onClick = { onCellClick(index) },
                        // Disable when already selected OR when input is full
                        enabled = !cell.isSelected && !isFull,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .heightIn(max = 80.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppBlue,
                            disabledContainerColor = AppGrey.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(
                            text = cell.char.toString(),
                            color = AppWhite,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButtons(onValidate: () -> Unit, onPass: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onValidate,
            modifier = Modifier.weight(1f).height(54.dp),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppBlue)
        ) {
            Text("Valider", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        OutlinedButton(
            onClick = onPass,
            modifier = Modifier.weight(1f).height(54.dp),
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, AppBorder)
        ) {
            Text("Passer", fontSize = 16.sp, color = AppGrey)
        }
    }
}

@Composable
private fun HintButton(hintUsed: Boolean, onHint: () -> Unit) {
    OutlinedButton(
        onClick = onHint,
        enabled = !hintUsed,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, if (hintUsed) AppBorder else AppGreen)
    ) {
        Text(
            text = if (hintUsed) "Indice utilisé" else "Indice (-1 pt)",
            color = if (hintUsed) AppGrey else AppGreen,
            fontSize = 14.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WordGameScreenPreview() {
    MiniGamesAppTheme {
        WordGameScreen(onBackClick = {})
    }
}
