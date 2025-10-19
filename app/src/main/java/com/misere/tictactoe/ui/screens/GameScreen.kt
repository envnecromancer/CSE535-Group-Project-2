package com.misere.tictactoe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.misere.tictactoe.data.Difficulty
import com.misere.tictactoe.data.GameMode
import com.misere.tictactoe.data.Player
import com.misere.tictactoe.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit,
    onNavigateToPastGames: () -> Unit
) {
    val gameState by viewModel.gameState.collectAsStateWithLifecycle()
    val difficulty by viewModel.difficulty.collectAsStateWithLifecycle()
    val gameMode by viewModel.gameMode.collectAsStateWithLifecycle()
    val isThinking by viewModel.isThinking.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Misere Tic-Tac-Toe") },
            actions = {
                TextButton(onClick = onNavigateToSettings) {
                    Text("Settings")
                }
                TextButton(onClick = onNavigateToPastGames) {
                    Text("History")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Game Mode and Difficulty Display
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Mode: ${gameMode.name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (gameMode == GameMode.VS_AI) {
                    Text(
                        text = "Difficulty: ${difficulty.name}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Game Board
        GameBoard(
            board = gameState.board,
            onCellClick = { row, col ->
                viewModel.makeMove(row, col)
            },
            isEnabled = !isThinking && gameState.winner == "" && !gameState.draw
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Game Status
        when {
            isThinking -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "AI is thinking...",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            gameState.winner != "" -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (gameState.winner == "X") 
                            MaterialTheme.colorScheme.errorContainer 
                        else MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (gameState.winner == "X") "You Win!" else "AI Wins!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (gameState.winner == "X") 
                                MaterialTheme.colorScheme.onErrorContainer 
                            else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            gameState.draw -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "It's a Draw!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Reset Button
        Button(
            onClick = { viewModel.resetGame() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset Game", fontSize = 18.sp)
        }
    }
}

@Composable
fun GameBoard(
    board: List<List<String>>,
    onCellClick: (Int, Int) -> Unit,
    isEnabled: Boolean
) {
    Card(
        modifier = Modifier
            .size(300.dp)
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            repeat(3) { row ->
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    repeat(3) { col ->
                        GameCell(
                            symbol = board[row][col],
                            onClick = { if (isEnabled) onCellClick(row, col) },
                            isEnabled = isEnabled && board[row][col] == ""
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameCell(
    symbol: String,
    onClick: () -> Unit,
    isEnabled: Boolean
) {
    val backgroundColor = when {
        symbol == "X" -> MaterialTheme.colorScheme.primary
        symbol == "O" -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.surface
    }
    
    val textColor = when {
        symbol == "X" -> MaterialTheme.colorScheme.onPrimary
        symbol == "O" -> MaterialTheme.colorScheme.onSecondary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = isEnabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}
