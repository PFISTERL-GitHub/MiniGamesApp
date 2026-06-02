package com.pfisterludovicmiehealix.minigames.ui.wordgame

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        GameHeader(title = "Mot Caché", timer = 60)
        ScoreRow(score = 0)
        WordHintCard(letterCount = 6)
        InputZone(input = "", onErase = {})
        LetterGrid(cells = listOf('S', 'K', 'O', 'L', 'E', 'Z', 'I', 'L', 'M'), onCellClick = {})
        ActionButtons(onValidate = {}, onPass = {})
        TextButton(onClick = onBackClick) {
            Text("Retour à l'accueil", color = AppGrey, fontSize = 14.sp)
        }
    }
}

@Composable
private fun GameHeader(title: String, timer: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = AppWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(text = "$timer", color = AppGreen, fontSize = 32.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun ScoreRow(score: Int) {
    Text(text = "Score : $score", color = AppGrey, fontSize = 16.sp)
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
private fun LetterGrid(cells: List<Char>, onCellClick: (Int) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        cells.chunked(3).forEachIndexed { rowIndex, rowCells ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowCells.forEachIndexed { colIndex, letter ->
                    Button(
                        onClick = { onCellClick(rowIndex * 3 + colIndex) },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppBlue)
                    ) {
                        Text(text = letter.toString(), color = AppWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
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

@Preview(showBackground = true)
@Composable
private fun WordGameScreenPreview() {
    MiniGamesAppTheme {
        WordGameScreen(onBackClick = {})
    }
}
