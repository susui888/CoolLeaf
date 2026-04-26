#!/bin/bash

# --- Configuration ---
SERVER_IP="192.168.2.18"
SERVER_USER="administrator"
APP_NAME="coolleaf-app"
REMOTE_DIR="C:/app/$APP_NAME"

JAR_PATH="../build/libs/CoolLeaf-0.0.1-SNAPSHOT.jar"
DOCKER_DIR="."

echo "1: Ensure remote directory exists..."
ssh $SERVER_USER@$SERVER_IP "powershell -Command \"if (!(Test-Path '$REMOTE_DIR')) { New-Item -ItemType Directory -Path '$REMOTE_DIR' }\""

echo "2: Transfer files to the server..."
# 确保所有需要的文件都包含在内
# 如果 Dockerfile 在当前目录，写 Dockerfile；如果在上一级，写 ../Dockerfile
scp "$JAR_PATH" "Dockerfile" "docker-compose.lenovo.yml" "nginx.conf" $SERVER_USER@$SERVER_IP:"$REMOTE_DIR/"

if [ $? -ne 0 ]; then
    echo "Transfer failed! Please check if the local files exist."
    exit 1
fi

echo "3: Deploy remotely via Docker Compose..."
# 关键点：cd 进去之后，必须指定 -f 参数，因为你的文件名不是默认的 docker-compose.yml
ssh $SERVER_USER@$SERVER_IP "cd /d $REMOTE_DIR && docker compose -f docker-compose.lenovo.yml up -d --build"