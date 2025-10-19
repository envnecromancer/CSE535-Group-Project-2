# Android Misere Tic-Tac-Toe App Generation Guidelines

## 1. Project Overview

The goal is to create a Misere Tic-Tac-Toe game for Android. This is a group project.

* **Due Date:** 10/31/2025, 11:59 pm
* **Game Logic:** Misere Tic-Tac-Toe, where the player who completes a line of three (row, column, or diagonal) *loses*.
* **Recommended Technology:** Android using Kotlin and Jetpack Compose.
* **Core Features:**
    * Player vs. AI gameplay with varying difficulty.
    * Player vs. Player gameplay (on-device and peer-to-peer).
    * Persistent storage of past game results.

## 2. Core App Structure

The application must have three primary screens accessible to the user:

1.  **Game Screen:** The main interface for playing the game.
2.  **Settings Screen:** To configure game options, primarily the difficulty level.
3.  **Past Games Screen:** A view to display the history of completed games.

## 3. Game Screen Details (Player vs. AI)

This is the main screen for user interaction.

### UI Requirements:

* Use a 3x3 grid for the game board.
* Use different colors and/or symbols to distinguish between the main user's marks (X) and the opponent's marks (O).

### Gameplay Flow:

* The human player always starts the game and uses the symbol 'X'.
* The AI player uses the symbol 'O'.
* Players alternate turns by tapping on an empty cell on the 3x3 grid.
* Once a cell is taken, it cannot be selected again in the same game.
* The game concludes when one player is forced to make a line of three, or the board is full (a draw).
* A message should clearly announce the game's outcome (e.g., "Player X Wins!", "Draw!").
* A "Reset Game" button must be available to clear the board and start a new game at any time.

## 4. Settings Screen Details

This screen allows the user to configure the game's difficulty. The selected mode must take effect immediately, even mid-game.

### Difficulty Modes:

* **Easy:** The AI selects a random available cell for its move.
* **Medium:** The AI's move is random 50% of the time and optimal 50% of the time.
* **Hard:** The AI uses the **Minimax algorithm with alpha-beta pruning** to determine the optimal move.
    * **Important:** Implement the Minimax algorithm and alpha-beta pruning directly. Do not use a third-party library for this logic.
    * While the AI is calculating its move in Hard mode, display a "Thinking..." message to the user.
* **vs. Human:** An option to enable player-vs-player mode.

## 5. Past Games Screen Details

This screen displays a history of all completed games.

### Requirements:

* Display a list or table of past games.
* For each game, show the **date/time**, the **winner** ('X', 'O', or 'Draw'), and the **difficulty mode** the game was played on.
* The game history data **must persist** even after the application is closed and reopened. Use a local database for storage.
* The list should be scrollable.

## 6. Peer-to-Peer Gameplay Details (vs. Human)

The app must support two modes for human-vs-human gameplay.

### 6.1. On-Device Play

* Allows two players to play on the same device.
* Players alternate turns by passing the device to each other.

### 6.2. Two-Device Play

* This mode uses a direct device-to-device connection (e.g., **WiFi Direct** or **Bluetooth**). **Using a central server is not permitted.**

#### Connection Flow:

1.  One user initiates hosting, and the other user searches for available devices.
2.  A list of discoverable devices should be displayed for the user to connect to.
3.  Once the connection is established, a UI must appear on both devices to decide who goes first (e.g., buttons for "ME" and "OPPONENT"). The choice made on one device should be reflected on both.
4.  The first player is assigned 'X'.

#### Gameplay Synchronization:

1.  When a player taps a cell on their device, the move must instantly appear on both devices' screens.
2.  A player's screen should be locked from input when it is not their turn.
3.  Both devices must independently detect and announce game outcomes (win, loss, draw).
4.  If either player resets the game, the board should reset on both devices.
5.  Games played in this mode should be saved to the Past Games screen on both devices.

### 6.3. Data Transfer Format (for Two-Device Play)

Use a structured format like JSON to transmit the game state between devices. The following structure and flow are recommended:

**Base Structure:**<br><br>
{ "gameState": { "board": [ ["","",""], ["","",""], ["","",""] ], "turn": "0", "winner": "", "draw": false, "connectionEstablished": true, "reset": false }, "metadata": { "choices": [ {"id": "player1", "name": "Player 1 MAC Address"}, {"id": "player2", "name": "Player 2 MAC Address"} ], "miniGame": { "player1Choice": "", "player2Choice": "" } } } <br><br>
**Deciding Who Goes First:**

1.  After connecting, one player (e.g., Player 1) taps "ME".
2.  Player 1's device sends a JSON object where `miniGame.player1Choice` is set to their device address.
3.  Player 2's device receives this, confirms the choice by setting `miniGame.player2Choice` to the same address, and sends it back. Both devices now know Player 1 is 'X'.

**During Gameplay:**

* The `board` array is updated with "X" or "O".
* The `turn` counter is incremented with each move.
* When a game ends, the `winner` field is populated with the winner's device address, or `draw` is set to `true`.

## 7. Submission Deliverables

### 7.1. GitHub Repository

* Create a private GitHub repository named `CSE 55 F25 - Project 2`.
* Add `aganes40@asu.edu`, `tashahee@asu.edu`, and `jaejong@asu.edu` as collaborators.
* Include a `README.md` file detailing project description and instructions to build and run.
* Use proper version control, with frequent, descriptive commits. Each team member should work on their own branch.
* Place the repository link at the top of the Project Report.

### 7.2. Demonstration Video

* Record a demonstration of the application (maximum 5 minutes).
* Upload it as an unlisted YouTube video.
* The video must cover all functionality outlined in the grading criteria.
* Place the video link at the top of the Project Report.

### 7.3. Project Report

* Use the **IEEE transactions template**.
* The report should not exceed 8 pages, excluding references.
* **Structure:**
    1.  Abstract, Introduction, and Links (GitHub and YouTube).
    2.  Technical Approach (Minimax, alpha-beta pruning, peer-to-peer feature, etc.).
    3.  Design Choices (UI/UX).
    4.  Implications, Limitations, or Challenges.
* Submit the final report PDF to Canvas.

## 8. Grading Criteria

### Code Submission (10 points total)

* **Repo Setup (1 pt):** Repository created with the correct name.
* **README (5 pts):** Includes a project description and usage instructions.
* **Version Control (4 pts):** Proper use of branches and frequent, descriptive commits.

### Demonstration: Player vs. AI (35 points total)

* **Gameplay (5 pts):** Symbols appear correctly; occupied spaces are not tappable.
* **Settings (5 pts):** Difficulty modes can be changed mid-game and are mutually exclusive.
* **Difficulty Logic (15 pts):**
    * Hard mode plays optimally (draws or wins).
    * Easy mode makes random moves.
    * Medium mode makes 50% random moves.
* **Past Games (10 pts):**
    * Displays date/time, winner, and difficulty.
    * Scrollable history.
    * Data persists after the app is closed.

### Demonstration: Peer-to-Peer (30 points total)

* **Gameplay (18 pts):**
    * Moves are reflected on both devices.
    * Input is locked during the opponent's turn.
    * Game outcomes are detected correctly on both devices.
    * Game state is transmitted correctly.
    * Reset works for both players.
* **Who Goes First (5 pts):** Selection UI works and correctly informs both devices.
* **Past Games (2 pts):** P2P games are saved to history on both devices.
* **Video Proof (5 pts):** Video shows both on-device and two-device gameplay.

### Project Report (25 points total)

* **Intro & Links (3 pts):** Provides context, objectives, and correct links.
* **Technical Approach (19 pts):** Clear explanation of Minimax, alpha-beta pruning, P2P implementation, and game state messaging.
* **Design Choices (2 pts):** Explanation of UI/UX decisions.
* **Limitations (1 pt):** Thoughtful discussion of challenges.

## 9. Resources

* **Minimax Algorithm:** `https://en.wikipedia.org/wiki/Minimax`
* **Alpha-Beta Pruning:** `https://en.wikipedia.org/wiki/Alpha-beta_pruning`
* **IEEE Template:** `https://journals.ieeeauthorcenter.ieee.org/create-your-ieeejournal-article/authoring-tools-and-templates/ieee-article-templates/templates-for-transactions`
* **Android Developer Docs:** `https://developer.android.com/docs`
* **Jetpack Compose:** `https://developer.android.com/jetpack/compose/documentation`
* **WiFi Direct:** `https://developer.android.com/develop/connectivity/wifi/wifip2p`
* **Bluetooth:** `https://developer.android.com/develop/connectivity/bluetooth`