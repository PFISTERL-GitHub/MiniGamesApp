package com.pfisterludovicmiehealix.minigames.ui.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pfisterludovicmiehealix.minigames.data.Score
import com.pfisterludovicmiehealix.minigames.ui.theme.*

@Composable
fun LeaderboardScreen(
    onBackClick: () -> Unit,
    viewModel: LeaderboardViewModel = viewModel()
) {
    val scores by viewModel.scores.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 48.dp)
    ) {
        Text("Leaderboard", color = AppWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Top 10 toutes parties", color = AppGrey, fontSize = 14.sp)
        Spacer(Modifier.height(24.dp))

        if (scores.isEmpty()) {
            Text("Aucun score enregistré.", color = AppGrey, fontSize = 14.sp)
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(scores) { index, score ->
                    ScoreRow(rank = index + 1, score = score)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onBackClick) {
            Text("Retour à l'accueil", color = AppGrey, fontSize = 14.sp)
        }
    }
}

@Composable
private fun ScoreRow(rank: Int, score: Score) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, AppBorder, RoundedCornerShape(4.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("#$rank", color = AppGrey, fontSize = 14.sp, modifier = Modifier.width(32.dp))
        Text(score.playerName, color = AppWhite, fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        Text(score.gameName, color = AppGrey, fontSize = 12.sp,
            modifier = Modifier.weight(1f))
        Text("${score.score}", color = AppGreen, fontSize = 16.sp,
            fontWeight = FontWeight.Bold)
    }
}
