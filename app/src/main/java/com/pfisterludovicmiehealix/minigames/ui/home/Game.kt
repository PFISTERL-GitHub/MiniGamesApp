package com.pfisterludovicmiehealix.minigames.ui.home

import com.pfisterludovicmiehealix.minigames.GameName
import com.pfisterludovicmiehealix.minigames.Routes

data class Game(
    val name: String,
    val description: String,
    val icone: String,
    val route: String
)

val games = listOf(
    Game(
        name = GameName.REACTION,
        description = "Arrêtez le chrono au bon moment",
        icone = "⏱️",
        route = Routes.REACTION
    ),
    Game(
        name = GameName.WORD,
        description = "Retrouvez le mot cache dans la grille",
        icone = "🔠",
        route = Routes.WORD
    ),
)
