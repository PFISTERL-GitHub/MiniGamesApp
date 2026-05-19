package com.pfisterludovicmiehealix.minigames

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.pfisterludovicmiehealix.minigames.ui.home.HomeScreen
import com.pfisterludovicmiehealix.minigames.ui.reaction.ReactionScreen
import com.pfisterludovicmiehealix.minigames.ui.theme.MiniGamesAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MiniGamesAppTheme {
                MiniGamesApp()
            }
        }
    }
}

@Composable
fun MiniGamesApp() {
    var currentScreen by remember { mutableStateOf("home") }

    when (currentScreen) {
        "reaction" -> ReactionScreen(
            onBackClick = { currentScreen = "home" }
        )

        else -> HomeScreen(
            onPlayClick = { screen -> currentScreen = screen }
        )
    }
}
