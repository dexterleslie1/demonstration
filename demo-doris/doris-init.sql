-- Doris 目标库/表（配合 SeaTunnel `seatunnel-config/mysql_to_doris.conf`）
--
-- 说明：
-- - Doris Stream Load 在目标库不存在时会报 unknown database；`schema_save_mode = CREATE_SCHEMA_WHEN_NOT_EXIST`
--   不能替代「先建库」这一步。
-- - 表采用 UNIQUE KEY（按主键 upsert），与 CDC + sink.enable-delete 一致。
-- - replication_num=1 适合单机演示；多 BE 请按需调大。
--

-- 算子落盘会话变量（GLOBAL：对新连接生效；BE 落盘路径见 doris-config/be.conf）
-- 演示环境 BE mem_limit=4g，exec_mem_limit 设为 2g 便于在复杂查询时触发落盘而非直接 OOM。
-- SET GLOBAL enable_spill = true;
-- SET GLOBAL exec_mem_limit = 2g;
-- SET GLOBAL query_timeout = 3600;

CREATE DATABASE IF NOT EXISTS demot;

DROP TABLE IF EXISTS demot.auth;

CREATE TABLE IF NOT EXISTS demot.auth (
  id BIGINT NOT NULL,
  /*account VARCHAR(64) NOT NULL,*/
  `password` VARCHAR(64) NOT NULL,
  create_time DATETIME NULL
)
UNIQUE KEY(id)
DISTRIBUTED BY HASH(id) BUCKETS 1
PROPERTIES (
  "replication_num" = "1"
);

CREATE TABLE IF NOT EXISTS demot.student (
  id BIGINT NOT NULL,
  `name` VARCHAR(64) NULL,
  create_time DATETIME(3) NULL
)
UNIQUE KEY(id)
DISTRIBUTED BY HASH(id) BUCKETS 1
PROPERTIES (
  "replication_num" = "1"
);

CREATE TABLE IF NOT EXISTS demot.course (
  id BIGINT NOT NULL,
  student_id BIGINT NULL,
  `name` VARCHAR(64) NULL
)
UNIQUE KEY(id)
DISTRIBUTED BY HASH(id) BUCKETS 1
PROPERTIES (
  "replication_num" = "1"
);
