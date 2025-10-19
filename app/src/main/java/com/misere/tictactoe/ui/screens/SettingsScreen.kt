package com.misere.tictactoe.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.misere.tictactoe.data.Difficulty
import com.misere.tictactoe.data.GameMode
import com.misere.tictactoe.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: GameViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val difficulty by viewModel.difficulty.collectAsStateWithLifecycle()
    val gameMode by viewModel.gameMode.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopAppBar(
            title = { Text("Settings") },
            navigationIcon = {
                TextButton(onClick = onNavigateBack) {
                    Text("Back")
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Game Mode Selection
        Card(
            modifier = Modifier.fillMaxWidth(),
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
                    GameMode.values().forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (gameMode == mode),
                                    onClick = { viewModel.setGameMode(mode) },
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
                                    GameMode.VS_AI -> "vs AI"
                                    GameMode.VS_HUMAN_ON_DEVICE -> "vs Human (Same Device)"
                                    GameMode.VS_HUMAN_P2P -> "vs Human (Two Devices)"
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
        if (gameMode == GameMode.VS_AI) {
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                        Difficulty.values().forEach { diff ->
                            if (diff != Difficulty.VS_HUMAN) {
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
                                            Difficulty.EASY -> "Easy - Random moves"
                                            Difficulty.MEDIUM -> "Medium - 50% random, 50% optimal"
                                            Difficulty.HARD -> "Hard - Optimal moves (Minimax)"
                                            else -> diff.name
                                        },
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Game Rules
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
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
                    text = "This is Misere Tic-Tac-Toe! The player who completes a line of three (row, column, or diagonal) LOSES the game. Try to force your opponent to make three in a row!",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
