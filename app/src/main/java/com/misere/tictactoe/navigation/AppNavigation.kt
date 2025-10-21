package com.misere.tictactoe.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.misere.tictactoe.ui.screens.GameScreen
import com.misere.tictactoe.ui.screens.SettingsScreen
import com.misere.tictactoe.ui.screens.PastGamesScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "game",
        modifier = modifier
    ) {
        composable("game") {
            GameScreen(
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToPastGames = { navController.navigate("past_games") }
            )
        }
        composable("settings") {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("past_games") {
            PastGamesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
