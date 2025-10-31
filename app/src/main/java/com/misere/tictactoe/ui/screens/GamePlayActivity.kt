package com.misere.tictactoe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.misere.tictactoe.data.Difficulty
import com.misere.tictactoe.data.GameMode
import com.misere.tictactoe.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamePlayActivity(
    viewModel: GameViewModel = viewModel(),
    onNavigateToSettings: () -> Unit,
    onNavigateToPastGames: () -> Unit
) {
    val gameState by viewModel.gameState.observeAsState()
    val gameMode by viewModel.gameMode.observeAsState(GameMode.PLAYER_VS_BOT)
    val difficulty by viewModel.difficulty.observeAsState(Difficulty.EASY)
    val isThinking by viewModel.isThinking.observeAsState(false)

    val isGameOver = gameState?.winner != "" || gameState?.draw == true

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Top Bar ---
        TopAppBar(
            title = { Text("Misere Tic-Tac-Toe") },
            actions = {
                TextButton(onClick = onNavigateToSettings) {
                    Text("Settings")
                }
                TextButton(onClick = onNavigateToPastGames) {
                    Text("History")
                }
            },
            modifier = Modifier.height(60.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Mode Card ---
        Card(
            modifier = Modifier.fillMaxWidth(0.9f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Mode: ${gameMode.name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (gameMode == GameMode.PLAYER_VS_BOT) {
                    Text(
                        text = "Difficulty: ${difficulty.name}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                // To show the current turn for human vs human mode
                if ((gameMode == GameMode.PLAYER_VS_PLAYER_ON_DEVICE || gameMode == GameMode.PLAYER_VS_PLAYER_P2P) && !isGameOver) {
                    val currentPlayer = if ((gameState?.turn ?: 0) % 2 == 0) "1" else "2"
                    Text(
                        text = "Current Turn: Player $currentPlayer",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (currentPlayer == "1")
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Game Board ---
        gameState?.let { state ->
            GameBoard(
                board = state.board,
                onCellClick = { r, c ->
                    if (!isGameOver && !isThinking) {
                        viewModel.makeMove(r, c)
                    }
                },
                isEnabled = !isThinking && !isGameOver
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- AI Thinking / Winner Info ---
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
            gameState?.winner != "" -> {
                val winnerText = when (gameMode) {
                    GameMode.PLAYER_VS_BOT -> {
                        if (gameState?.winner == "X") "Congratulations, You Win!" else "Oho, AI Wins!"
                    }
                    else -> {
                        if (gameState?.winner == "X") {
                            "Congratulations, Player 1 Wins!"
                        } else{
                            "Congratulations, Player 2 Wins!"
                        }
                    }
                }

                val winnerMessage = when (gameMode) {
                    GameMode.PLAYER_VS_BOT -> {
                        if (gameState?.winner == "X")
                            "Your opponent completed a line!"
                        else "You completed a line!"
                    }
                    else -> {
                        val loser = if (gameState?.winner == "X") "2" else "1"
                        "Player $loser completed a line and lost!"
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (gameState?.winner == "X")
                            MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = winnerText,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = winnerMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            gameState?.draw == true -> {
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
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "It's a Draw!",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No one completed a line!",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Reset Button
        Button(
            onClick = { viewModel.resetGame() },
            modifier = Modifier.fillMaxWidth(0.6f)
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
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
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
}

@Composable
fun GameCell(symbol: String, onClick: () -> Unit, isEnabled: Boolean) {
    val bg = when (symbol) {
        "X" -> MaterialTheme.colorScheme.primary
        "O" -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.surface
    }
    val color = when (symbol) {
        "X" -> MaterialTheme.colorScheme.onPrimary
        "O" -> MaterialTheme.colorScheme.onSecondary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .clickable(enabled = isEnabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(symbol, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = color)
    }
}
