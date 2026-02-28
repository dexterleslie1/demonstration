#!/bin/sh
# 在 ES 启动后执行，注册 ILM 策略和索引模板，实现 30 天后自动删除过期索引（无需 Curator）
set -e
ES_URL="${ES_URL:-http://localhost:9200}"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "Waiting for Elasticsearch at $ES_URL..."
until [ "$(curl -s -o /dev/null -w '%{http_code}' "$ES_URL")" = "200" ]; do
  echo "Waiting for Elasticsearch at $ES_URL..."
  sleep 2
done
echo "Elasticsearch is up."

echo "Creating ILM policy: log-delete"
curl -s -X PUT "$ES_URL/_ilm/policy/log-delete" \
  -H "Content-Type: application/json" \
  -d @"$SCRIPT_DIR/ilm-policy-delete.json"
echo ""

echo "Creating index template: log-type-lifecycle"
curl -s -X PUT "$ES_URL/_template/log-type-lifecycle" \
  -H "Content-Type: application/json" \
  -d @"$SCRIPT_DIR/index-template-log-type-lifecycle.json"
echo ""