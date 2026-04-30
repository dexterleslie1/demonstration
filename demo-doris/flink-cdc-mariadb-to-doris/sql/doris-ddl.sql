-- Doris 侧建表（配合 `flink-cdc.sql` 使用）
--
-- 说明：
-- - Doris 表采用 UNIQUE KEY（按主键 upsert），适合 CDC 同步 “insert/update/delete” 场景。
-- - 这里使用单副本（replication_num=1）以便在单机演示环境运行；如果你是多 BE，请按需调大。
-- - Doris 的时间类型与 MySQL/MariaDB 存在细微差异，这里用 DATETIME(3) / DATETIME 来兼容。
--
-- 执行方式（示例）：
-- - 使用 MySQL 客户端连接 Doris FE 9030 后执行：
--   - mysql -h127.0.0.1 -P9030 -uroot -p123456 < doris-ddl.sql

CREATE DATABASE IF NOT EXISTS demo;

-- student
CREATE TABLE IF NOT EXISTS demo.student (
  id BIGINT NOT NULL,
  `name` VARCHAR(64) NULL,
  create_time DATETIME(3) NULL
)
UNIQUE KEY(id)
DISTRIBUTED BY HASH(id) BUCKETS 1
PROPERTIES (
  "replication_num" = "1"
);

-- course
CREATE TABLE IF NOT EXISTS demo.course (
  id BIGINT NOT NULL,
  student_id BIGINT NULL,
  `name` VARCHAR(64) NULL
)
UNIQUE KEY(id)
DISTRIBUTED BY HASH(id) BUCKETS 1
PROPERTIES (
  "replication_num" = "1"
);

