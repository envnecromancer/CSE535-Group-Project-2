package com.misere.tictactoe.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.misere.tictactoe.data.Difficulty
import com.misere.tictactoe.data.GameMode
import com.misere.tictactoe.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsActivity(
    viewModel: GameViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToP2P: () -> Unit
)
 {
    val difficulty by viewModel.difficulty.observeAsState(Difficulty.EASY)
    val gameMode by viewModel.gameMode.observeAsState(GameMode.PLAYER_VS_BOT)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = { Text("Settings") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            modifier = Modifier.height(60.dp)
        )

        // Activity Layout for setting

        Spacer(modifier = Modifier.height(24.dp))

            // Game Mode Selection
        Card(
            modifier = Modifier.fillMaxWidth(0.92f),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Game Mode",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Column(
                    modifier = Modifier.selectableGroup()
                ) {
                    GameMode.entries.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (gameMode == mode),
                                    onClick = {
                                        // updating View Model
                                        viewModel.setGameMode(mode)
                                        // if user picked P2P, immediately go to pairing screen
                                        if (mode == GameMode.
                                            PLAYER_VS_PLAYER_P2P
                                        ) {
                                            onNavigateToP2P()
                                        }
                                    },
                                    role = Role.RadioButton
                                )
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (gameMode == mode),
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (mode) {
                                    GameMode.PLAYER_VS_BOT ->
                                        "Player vs BOT"
                                    GameMode.PLAYER_VS_PLAYER_ON_DEVICE ->
                                        "Player vs Player (On-Device Play)"
                                    GameMode.PLAYER_VS_PLAYER_P2P ->
                                        "Player vs Player (Two-Device Play)"
                                },
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Difficulty Selection (only for AI mode)
        if (gameMode == GameMode.PLAYER_VS_BOT) {
            Card(
                modifier = Modifier.fillMaxWidth(0.92f),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "AI Difficulty",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Column(
                        modifier = Modifier.selectableGroup()
                    ) {
                        listOf(
                            Difficulty.EASY,
                            Difficulty.MEDIUM,
                            Difficulty.HARD
                        ).forEach { diff ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (difficulty == diff),
                                        onClick = { viewModel.setDifficulty(diff) },
                                        role = Role.RadioButton
                                    )
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (difficulty == diff),
                                    onClick = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when (diff) {
                                        Difficulty.EASY -> "Easy"
                                        Difficulty.MEDIUM -> "Medium"
                                        Difficulty.HARD -> "Hard"
                                    },
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

            // Game Rules
        Card(
            modifier = Modifier.fillMaxWidth(0.92f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Game Rules",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Misere Tic-Tac-Toe: The player who completes a line LOSES.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Continue button to navigate back to the Game
        Button(
            onClick = {
                onNavigateBack()
            },
            modifier = Modifier
                .fillMaxWidth(0.7f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Continue to Game",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
