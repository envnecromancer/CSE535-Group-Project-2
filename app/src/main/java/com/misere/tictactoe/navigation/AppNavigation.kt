package com.misere.tictactoe.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.misere.tictactoe.ui.screens.SimplePastGamesScreen
import com.misere.tictactoe.ui.screens.SimpleGameScreen
import com.misere.tictactoe.ui.screens.SimpleSettingsScreen
import com.misere.tictactoe.viewmodel.GameViewModel

@Composable
fun SimpleAppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    // Get the activity as the ViewModelStoreOwner to share ViewModel across destinations
    val context = LocalContext.current
    val activity = context as? androidx.activity.ComponentActivity
    
    // Create ViewModel scoped to activity, so it's shared across all screens
    val sharedViewModel: GameViewModel = viewModel(
        viewModelStoreOwner = activity ?: error("Context is not an Activity")
    )
    
    NavHost(
        navController = navController,
        startDestination = "game",
        modifier = modifier
    ) {
        composable("game") {
            SimpleGameScreen(
                viewModel = sharedViewModel,
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToPastGames = { navController.navigate("past_games") }
            )
        }
        composable("settings") {
            SimpleSettingsScreen(
                viewModel = sharedViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("past_games") {
            SimplePastGamesScreen(
                viewModel = sharedViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

