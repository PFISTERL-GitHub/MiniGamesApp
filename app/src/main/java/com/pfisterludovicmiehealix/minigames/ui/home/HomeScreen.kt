package com.pfisterludovicmiehealix.minigames.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pfisterludovicmiehealix.minigames.Routes
import com.pfisterludovicmiehealix.minigames.ui.theme.*

// Représente un jeu disponible dans l'application
data class Game(
    val name: String,        // Nom affiché à l'écran
    val description: String, // Message explicatif
    val icone: String,       // emoji utilisé dans la boîte carrée
    val route: String        // Identifiant de navigation passé à onPlayClick
)

// Liste de tous les jeux de l'application
// Pour ajouter un jeu : ajouter une entrée ici et le case correspondant dans MiniGamesApp
val games = listOf(
    Game(
        name = "Jeu de Réaction",
        description = "Arrêtez le chrono au bon moment",
        icone = "⏱️",
        route = Routes.REACTION
    ),
    Game(
        name = "Jeu de Mots",
        description = "Retrouvez le mot cache dans la grille",
        icone = "🔠",
        route = Routes.WORD
    ),
)

@Composable
fun HomeScreen(onPlayClick: (String) -> Unit) {
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

        //Spacer(modifier = Modifier.height(48.dp))
        Text(
            text  = "Choisissez un jeu",
            fontSize = 15.sp,
            color = AppGrey,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        games.forEach { game ->
            GameCard(game = game, onClick = { onPlayClick(game.route) })
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun GameCard(game: Game, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, AppBorder, RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Petite boîte carrée avec l'icône
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

        // Nom + description
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text       = game.name,
                color      = AppWhite,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 16.sp
            )
            Text(
                text     = game.description,
                color    = AppGrey,
                fontSize = 13.sp
            )
        }

        // Flèche verte
        Text(
            text     = "→",
            color    = AppGreen,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}