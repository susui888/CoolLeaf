#!/bin/bash

# --- Configuration ---
SERVER_IP="192.168.2.18"
SERVER_USER="administrator"
APP_NAME="coolleaf-app"
# If using OpenSSH on Windows, this path format is recommended
REMOTE_DIR="C:/app/$APP_NAME"
JAR_PATH="build/libs/CoolLeaf-0.0.1-SNAPSHOT.jar"

echo "1: Build the project locally..."
./gradlew build -x test

if [ $? -ne 0 ]; then
    echo "Build failed, please check your code!"
    exit 1
fi

echo "2: Transfer files to the server..."
# Ensure the remote directory exists
ssh $SERVER_USER@$SERVER_IP "if not exist \"$REMOTE_DIR\" mkdir \"$REMOTE_DIR\""

# Transfer key files
scp $JAR_PATH Dockerfile docker-compose.yml $SERVER_USER@$SERVER_IP:"$REMOTE_DIR/"

echo "3: Deploy remotely via Docker Compose..."
# Note: Avoid using << EOF (can cause issues), directly pass command string instead
ssh $SERVER_USER@$SERVER_IP "cd /d $REMOTE_DIR && docker compose up -d --build && echo ' Deployment completed!' && docker compose ps"