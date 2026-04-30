-- Flink SQL：MariaDB (CDC) -> Doris (Stream Load)
--
-- 使用前置条件（你当前项目基本已满足）：
-- 1) MariaDB 必须开启 binlog 且为 ROW 格式、设置 server_id（你 `my-customize.cnf` 已配置）
-- 2) Doris 侧需要提前建好目标表（见同目录的 `doris-ddl.sql`）
-- 3) Flink 的 lib/ 下需要有：
--    - flink-sql-connector-mysql-cdc-*.jar
--    - flink-doris-connector-*.jar
--
-- 重要说明：
-- - 这里演示同步 2 张表：demo.student / demo.course
-- - 为了让 Doris 能做 upsert，这里 Doris 表采用 UNIQUE KEY，并在 Flink 表上声明 PRIMARY KEY（NOT ENFORCED）
-- - Doris Sink 连接使用 Doris FE 的 HTTP 端口（默认 8030）做 Stream Load 入口
--   你当前 Doris FE/BE 使用 host 网络，宿主机可通过 127.0.0.1 访问这些端口。

-- -----------------------------------------------------------------------------
-- 1) Source：MariaDB CDC - demo.student
-- -----------------------------------------------------------------------------
CREATE TABLE mariadb_student (
  id BIGINT,
  `name` STRING,
  create_time TIMESTAMP(3),
  PRIMARY KEY (id) NOT ENFORCED
) WITH (
  'connector' = 'mysql-cdc',
  'hostname' = '127.0.0.1',
  'port' = '3306',
  'username' = 'root',
  'password' = '123456',
  'database-name' = 'demo',
  'table-name' = 'student',
  -- 初次启动是否做快照：
  -- - initial：先全量快照，再增量订阅 binlog（演示场景更直观）
  -- - latest-offset：仅从当前 binlog 位点开始（不做历史全量）
  'scan.startup.mode' = 'initial',
  -- 容错：网络抖动时 CDC 连接器自动重连
  'connect.timeout' = '30s',
  'connect.max-retries' = '10'
);

-- -----------------------------------------------------------------------------
-- 2) Source：MariaDB CDC - demo.course
-- -----------------------------------------------------------------------------
CREATE TABLE mariadb_course (
  id BIGINT,
  student_id BIGINT,
  `name` STRING,
  PRIMARY KEY (id) NOT ENFORCED
) WITH (
  'connector' = 'mysql-cdc',
  'hostname' = '127.0.0.1',
  'port' = '3306',
  'username' = 'root',
  'password' = '123456',
  'database-name' = 'demo',
  'table-name' = 'course',
  'scan.startup.mode' = 'initial',
  'connect.timeout' = '30s',
  'connect.max-retries' = '10'
);

-- -----------------------------------------------------------------------------
-- 3) Sink：Doris - demo.student
-- -----------------------------------------------------------------------------
CREATE TABLE doris_student (
  id BIGINT,
  `name` STRING,
  create_time TIMESTAMP(3),
  PRIMARY KEY (id) NOT ENFORCED
) WITH (
  'connector' = 'doris',
  -- Doris FE 的 HTTP/Stream Load 入口（常用 8030）
  'fenodes' = '127.0.0.1:8030',
  'table.identifier' = 'demo.student',
  'username' = 'root',
  'password' = '123456',
  -- Stream Load 相关参数（写入吞吐/延迟权衡）
  -- - sink.batch.size: 每批写入的行数
  -- - sink.batch.interval: 最大等待时间（达到就触发一批）
  'sink.batch.size' = '5000',
  'sink.batch.interval' = '3s',
  -- 开启 2PC：在 checkpoint 语义下实现更可靠的 exactly-once（需要配合开启 checkpoint）
  -- 演示场景下可保持默认；若你要更强一致性，可配置 Flink checkpoint 并打开该项。
  'sink.enable-2pc' = 'false'
);

-- -----------------------------------------------------------------------------
-- 4) Sink：Doris - demo.course
-- -----------------------------------------------------------------------------
CREATE TABLE doris_course (
  id BIGINT,
  student_id BIGINT,
  `name` STRING,
  PRIMARY KEY (id) NOT ENFORCED
) WITH (
  'connector' = 'doris',
  'fenodes' = '127.0.0.1:8030',
  'table.identifier' = 'demo.course',
  'username' = 'root',
  'password' = '123456',
  'sink.batch.size' = '5000',
  'sink.batch.interval' = '3s',
  'sink.enable-2pc' = 'false'
);

-- -----------------------------------------------------------------------------
-- 5) DML：持续同步（INSERT INTO ... SELECT ...）
-- -----------------------------------------------------------------------------
INSERT INTO doris_student
SELECT
  id,
  `name`,
  create_time
FROM mariadb_student;

INSERT INTO doris_course
SELECT
  id,
  student_id,
  `name`
FROM mariadb_course;

