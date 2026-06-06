package com.pfisterludovicmiehealix.minigames.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pfisterludovicmiehealix.minigames.ui.theme.*

@Composable
fun HomeScreen(
    onPlayClick: (route: String, playerName: String) -> Unit,
    onLeaderboardClick: () -> Unit
) {
    var playerName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 48.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Mini Jeux",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Choisissez un jeu",
            fontSize = 15.sp,
            color = AppGrey,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        PseudoField(value = playerName, onValueChange = { playerName = it })

        Spacer(modifier = Modifier.height(16.dp))

        games.forEach { game ->
            GameCard(
                game = game,
                enabled = playerName.isNotBlank(),
                onClick = { onPlayClick(game.route, playerName) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedButton(
            onClick = onLeaderboardClick,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, AppBorder)
        ) {
            Text("Leaderboard", color = AppGrey, fontSize = 16.sp)
        }
    }
}

@Composable
private fun PseudoField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Pseudo") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = AppWhite,
            unfocusedTextColor = AppWhite,
            focusedBorderColor = AppBlue,
            unfocusedBorderColor = AppBorder,
            focusedLabelColor = AppBlue,
            unfocusedLabelColor = AppGrey,
        )
    )
}

@Composable
private fun GameCard(game: Game, enabled: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, AppBorder, RoundedCornerShape(4.dp))
            .clickable(enabled = enabled) { onClick() }
            .alpha(if (enabled) 1f else 0.4f)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(AppBlue.copy(alpha = 0.15f))
                .border(1.dp, AppBlue.copy(alpha = 0.4f), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = game.icone, fontSize = 24.sp)
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = game.name, color = AppWhite, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(text = game.description, color = AppGrey, fontSize = 13.sp)
        }

        Text(text = "→", color = AppGreen, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}