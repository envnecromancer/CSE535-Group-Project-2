# Misere Tic-Tac-Toe (CSE 535 - Fall 2025)

## Project Description

This project implements Misere Tic-Tac-Toe — a variant of Tic-Tac-Toe — as an Android mobile app using Kotlin and Jetpack Compose.

In Misere Tic-Tac-Toe, the player who completes a line of three (row, column, or diagonal) loses.  
The app supports both AI vs Human and Human vs Human (peer-to-peer) gameplay modes.

### Objectives
- Develop a Misere Tic-Tac-Toe mobile app for Android.
- Implement the Minimax algorithm with Alpha-Beta Pruning for AI gameplay.
- Provide three difficulty levels: Easy, Medium, and Hard.
- Support peer-to-peer (P2P) play via Bluetooth.
- Store completed games persistently with results and difficulty level.
- Build a modern UI using Jetpack Compose.

### Game Features

#### 1. AI Gameplay
- The player (X) always starts, AI or Bot (O) responds.
- Difficulty modes:
  - Easy: AI plays random moves.
  - Medium: AI mixes random and optimal moves.
  - Hard: AI plays optimally (Minimax + Alpha-Beta Pruning).
- Displays “AI Thinking…” when computing optimal moves.

#### 2. Human vs Human
- On-Device: Two players alternate turns on a single device.
- Two-Device (P2P): Players connect via Bluetooth.
- Moves, resets, and results sync across devices using JSON data.
- Includes a “Host/Join" option (ME / OPPONENT), whoever hosts start the game.

#### 3. Persistent Game History
- Each finished game is saved with:
  - Date and time
  - Winner or Draw
  - Difficulty level used
- Data persists even after app restart (Room DB or SharedPreferences).

#### 4. App Screens
- Game Screen: 3×3 grid for playing and resetting.
- Settings Screen: Adjust difficulty or enable vs Human mode.
- Past Games Screen: View history of completed matches.

---

## Usage Instructions

### Requirements
- Android Studio (latest version)
- Kotlin 1.9+ with Jetpack Compose
- Android SDK 33+
- Optional: two Android devices for P2P testing

### Open the Project
1. Launch Android Studio → Open an Existing Project → Select the folder.  
2. Allow Gradle to sync automatically.

### Run the App
1. Click Run ▶ in Android Studio.  
2. Choose a connected Android device or two(for P2P).  
3. The Game Screen will load as the start screen.

### How to Play (vs AI)
1. Tap an empty cell to make your move (X).  
2. The AI (O) will respond immediately.  
3. The game ends when a player is forced to complete a line of three.  
4. Change difficulty anytime from Settings.  
5. Reset the board anytime using the Reset button.  
6. Completed matches appear in the Past Games list.

### How to Play (vs Human)

#### On the Same Device
1. Open Settings → Play vs Human (On-Device).  
2. Players take turns tapping empty cells.  
3. Either player can reset the board anytime.  
4. A message announces the result or draw.

#### Between Two Devices (Peer-to-Peer)
1. Select Settings → Play vs Human (Peer-to-Peer).  
2. Enable Bluetooth on both devices.  
3. One player hosts; the other connects from available devices.  
4. Choose Who Goes First (Host or Client) by choosing Host and the client chooses join.  
5. Moves are mirrored in real time across both devices.  
6. When one device resets or finishes the game, both update automatically.

### View Game History
1. Open the Past Games Screen.  
2. Review each match’s:
   - Date and Time  
   - Winner or Draw  
   - Difficulty used  
3. Game history remains available after app restarts.

### Technology Stack
- Language: Kotlin  
- Framework: Jetpack Compose  
- Algorithm: Minimax + Alpha-Beta Pruning  
- Networking: Bluetooth APIs  
- Storage: Room Database / SharedPreferences  
- IDE: Android Studio
