#!/bin/bash
# 在 demo-signoz 目录下停止 SigNoz Docker Compose 服务
set -e
cd "$(dirname "$0")/docker"
docker compose down
echo "SigNoz 已停止"
