#!/usr/bin/env bash
# 绑定挂载 ./flink-data/checkpoints 时宿主机目录常为 root 所有，须在降权前 chown 给 flink 用户
set -euo pipefail
mkdir -p /opt/flink/checkpoints
if [[ "$(id -u)" == "0" ]]; then
  chown -R flink:flink /opt/flink/checkpoints
fi
exec /docker-entrypoint.sh "$@"
