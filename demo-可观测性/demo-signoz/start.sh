#!/bin/bash
# 参考 https://signoz.io/docs/install/docker/#install-signoz-using-docker-compose
# 在 demo-signoz 目录下使用 Docker Compose 启动 SigNoz
set -e
cd "$(dirname "$0")/docker"
docker compose up -d --remove-orphans
echo ""
echo "SigNoz 启动中，请等待所有容器就绪后访问: http://localhost:8080"
echo "查看容器状态: cd docker && docker compose ps"
