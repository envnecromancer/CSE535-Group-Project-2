package com.misere.tictactoe

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.misere.tictactoe.ui.screens.GamePlayActivity
import com.misere.tictactoe.ui.screens.P2PSetupScreen
import com.misere.tictactoe.ui.screens.PastGamesActivity
import com.misere.tictactoe.ui.screens.SettingsActivity
import com.misere.tictactoe.viewmodel.GameViewModel
import com.misere.tictactoe.viewmodel.P2PViewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val activity = LocalContext.current as ComponentActivity

    val gameVm: GameViewModel = viewModel(viewModelStoreOwner = activity)
    val p2pVm: P2PViewModel   = viewModel(viewModelStoreOwner = activity)

    gameVm.p2pViewModel = p2pVm
    p2pVm.gameViewModelRef = gameVm

    NavHost(
        navController = navController,
        startDestination = "game",
        modifier = modifier
    ) {
        composable("game") {
            GamePlayActivity(
                viewModel = gameVm,
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToPastGames = { navController.navigate("past_games") }
            )
        }
        composable("settings") {
            SettingsActivity(
                viewModel = gameVm,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToP2P = { navController.navigate("p2p") }
            )
        }
        composable("past_games") {
            PastGamesActivity(
                viewModel = gameVm,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("p2p") {
            P2PSetupScreen(
                onNavigateBack = { navController.popBackStack() },
                vm = p2pVm
            )
        }
    }
}
