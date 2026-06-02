package com.pfisterludovicmiehealix.minigames

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pfisterludovicmiehealix.minigames.ui.home.HomeScreen
import com.pfisterludovicmiehealix.minigames.ui.reaction.ReactionScreen
import com.pfisterludovicmiehealix.minigames.ui.wordgame.WordGameScreen

// ROUTES

object Routes {
    const val HOME     = "home"
    const val REACTION = "reaction"
    const val WORD     = "word"
}

// NAVIGATION

@Composable
fun MiniGamesNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController    = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onPlayClick = { route -> navController.navigate(route) }
            )
        }

        composable(Routes.REACTION) {
            ReactionScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.WORD) {
            WordGameScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}