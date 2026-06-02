package com.pfisterludovicmiehealix.minigames

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.pfisterludovicmiehealix.minigames.ui.theme.MiniGamesAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MiniGamesAppTheme {
                MiniGamesNavHost()
            }
        }
    }
}