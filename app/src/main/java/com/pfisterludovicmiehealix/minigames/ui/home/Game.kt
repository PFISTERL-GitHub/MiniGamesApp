package com.pfisterludovicmiehealix.minigames.ui.home

import com.pfisterludovicmiehealix.minigames.Routes

data class Game(
    val name: String,
    val description: String,
    val icone: String,
    val route: String
)

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
