#!/usr/bin/env bash
# 基于 init.sql 中的 t_user 复现 Maxwell schema 漂移：
#   Unable to cast Integer into column ... t_user.username (type 'varchar')
#   position mismatch
#
# 根因：MariaDB 对「列已存在」的 ADD COLUMN IF NOT EXISTS 仍会写 binlog；
# Maxwell(v1.46) 会按 ADD COLUMN 处理，在内部 schema 里重复加列，导致列位置错位。
#
# 用法（需先 docker compose up -d）：
#   ./reproduce-schema-drift.sh
# 观察：
#   docker compose logs -f maxwell

set -euo pipefail
cd "$(dirname "$0")"

mysql() {
  docker compose exec -T db mariadb -uroot -p123456 demo
}

echo "==> 1. 写入一行"
mysql <<'SQL'
INSERT INTO t_user(username, createTime) VALUES('drift-seed', NOW());
SQL
sleep 2

echo "==> 2. 重复执行已存在列的 ADD COLUMN IF NOT EXISTS（MariaDB 无实际变更，但 binlog + Maxwell 会记录）"
for i in 1 2 3; do
  echo "    round $i"
  mysql <<'SQL'
ALTER TABLE t_user ADD COLUMN IF NOT EXISTS `username` VARCHAR(64) NOT NULL COMMENT '名称';
SQL
  sleep 1
done

echo "==> 3. 再加两个新 int 列（错位后 DML 会把 int 映到 username/date）"
mysql <<'SQL'
ALTER TABLE t_user ADD COLUMN IF NOT EXISTS `is_jh` TINYINT(1) NULL DEFAULT 0;
SQL
sleep 2

echo "==> 4. 触发 DML，期望出现 cast / position mismatch 并断开 binlog"
mysql <<'SQL'
UPDATE t_user SET is_jh=1 WHERE username='drift-seed';
-- INSERT INTO t_user(username, createTime, is_jh) VALUES('drift-2', NOW(), 0);
SQL
sleep 3

echo "==> 5. Maxwell 相关错误日志："
docker compose logs --tail 100 maxwell 2>&1 | grep -E 'Unable to cast|position mismatch|schema inconsist|Binlog disconnected' || {
  echo "未匹配到错误，完整看：docker compose logs --tail 200 maxwell"
  exit 1
}

echo
echo "复现成功。恢复环境可执行："
echo "  docker compose exec db mariadb -uroot -p123456 -e 'DROP DATABASE IF EXISTS maxwell;'"
echo "  docker compose restart maxwell"
# 可选：去掉复现时加上的列
echo "  # docker compose exec db mariadb -uroot -p123456 demo -e \"ALTER TABLE t_user DROP COLUMN IF EXISTS is_jh;\""
