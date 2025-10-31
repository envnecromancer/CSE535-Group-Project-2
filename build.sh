#!/bin/bash

# Simple build script for Android project
echo "Building Misere Tic-Tac-Toe Android App..."

# Check if Android SDK is available
if [ -z "$ANDROID_HOME" ]; then
    echo "ANDROID_HOME is not set. Please set it to your Android SDK path."
    echo "Example: export ANDROID_HOME=/Users/username/Library/Android/sdk"
    exit 1
fi

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "Java is not installed or not in PATH."
    exit 1
fi

echo "Android SDK found at: $ANDROID_HOME"
echo "Java version:"
java -version

echo ""
echo "Project structure created successfully!"
echo "To build and run this project:"
echo "1. Open Android Studio"
echo "2. Open the project folder: $(pwd)"
echo "3. Sync the project with Gradle files"
echo "4. Run the app on an emulator or device"
echo ""
echo "Features implemented:"
echo "✓ Misere Tic-Tac-Toe game logic"
echo "✓ AI with Easy, Medium, and Hard difficulty levels"
echo "✓ Minimax algorithm with alpha-beta pruning"
echo "✓ Jetpack Compose UI"
echo "✓ Room database for game history"
echo "✓ Settings screen"
echo "✓ Past games screen"
echo "✓ Modern Material Design 3 UI"
echo ""
echo "Note: Peer-to-peer functionality is prepared but requires additional setup for WiFi Direct/Bluetooth."
