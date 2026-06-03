package com.pfisterludovicmiehealix.minigames

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pfisterludovicmiehealix.minigames.ui.home.HomeScreen
import com.pfisterludovicmiehealix.minigames.ui.leaderboard.LeaderboardScreen
import com.pfisterludovicmiehealix.minigames.ui.reaction.ReactionScreen
import com.pfisterludovicmiehealix.minigames.ui.wordgame.WordGameScreen

object Routes {
    const val HOME        = "home"
    const val REACTION    = "reaction"
    const val WORD        = "word"
    const val LEADERBOARD = "leaderboard"
}

@Composable
fun MiniGamesNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onPlayClick = { route, playerName -> navController.navigate("$route/$playerName") },
                onLeaderboardClick = { navController.navigate(Routes.LEADERBOARD) }
            )
        }
        composable("${Routes.REACTION}/{playerName}") { backStackEntry ->
            val playerName = backStackEntry.arguments?.getString("playerName") ?: ""
            ReactionScreen(playerName = playerName, onBackClick = { navController.popBackStack() })
        }
        composable("${Routes.WORD}/{playerName}") { backStackEntry ->
            val playerName = backStackEntry.arguments?.getString("playerName") ?: ""
            WordGameScreen(playerName = playerName, onBackClick = { navController.popBackStack() })
        }
        composable(Routes.LEADERBOARD) {
            LeaderboardScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
