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
    var isPlayingReaction by remember { mutableStateOf(false) }

    if (isPlayingReaction) {
        ReactionScreen(
            onBackClick = { isPlayingReaction = false }
        )
    } else {
        HomeScreen(
            onPlayClick = { isPlayingReaction = true }
        )
    }
}

