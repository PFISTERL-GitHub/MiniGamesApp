package com.pfisterludovicmiehealix.minigames.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Représente un jeu disponible dans l'application
data class Game(
    val nom: String,        // Nom affiché à l'écran
    val route: String       // Identifiant de navigation passé à onPlayClick
)

// Liste de tous les jeux de l'application
// Pour ajouter un jeu : ajouter une entrée ici et le case correspondant dans MiniGamesApp
val games = listOf(
    Game(nom = "Jeu de Réaction", route = "reaction")
)

@Composable
fun HomeScreen(onPlayClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Mini Jeux",
            style = MaterialTheme.typography.displayMedium
        )

        Spacer(modifier = Modifier.height(48.dp))

        games.forEach { game ->
            Button(
                onClick = { onPlayClick(game.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = game.nom)
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}