#!/bin/bash

# --- Configuration ---
SERVER_IP="192.168.2.18"
SERVER_USER="administrator"
APP_NAME="coolleaf-app"
REMOTE_DIR="C:/app/$APP_NAME"

JAR_PATH="build/libs/CoolLeaf-0.0.1-SNAPSHOT.jar"
DOCKER_DIR="."

echo "0: Cleaning and building the application..."
cd ..
./gradlew clean bootJar

echo "1: Ensure remote directory exists..."
ssh $SERVER_USER@$SERVER_IP "powershell -Command \"if (!(Test-Path '$REMOTE_DIR')) { New-Item -ItemType Directory -Path '$REMOTE_DIR' }\""

echo "2: Transfer files to the server..."
scp "$JAR_PATH" "docker/.env" "docker/Dockerfile" "docker/docker-compose.lenovo.yml" "docker/nginx.conf" $SERVER_USER@$SERVER_IP:"$REMOTE_DIR/"

if [ $? -ne 0 ]; then
    echo "Transfer failed! Please check if the local files exist."
    exit 1
fi

echo "3: Deploy remotely via Docker Compose..."
ssh $SERVER_USER@$SERVER_IP "cd /d $REMOTE_DIR && docker compose -f docker-compose.lenovo.yml up -d --build"