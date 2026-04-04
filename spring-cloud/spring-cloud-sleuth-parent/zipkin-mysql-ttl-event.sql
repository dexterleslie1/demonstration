-- =============================================================================
-- Zipkin MySQL/MariaDB：按时间自动清理过期链路数据
-- =============================================================================
-- 背景：
--   OpenZipkin 的 mysql-v1 存储不会在服务端自动做 TTL；数据会一直留在 zipkin_spans /
--   zipkin_annotations 中。本脚本通过 MariaDB「事件调度器（Event Scheduler）」周期性执行
--   DELETE，实现「超过保留窗口的链路数据从库中物理删除」。
--
-- 前置条件：
--   1. 已执行 zipkin-mysql-schema.sql，表 zipkin_spans / zipkin_annotations 已存在。
--   2. 实例启动参数包含 --event-scheduler=ON（见 docker-compose 中 demo-zipkin-db 的 command），
--      否则事件即使创建也可能不执行。
--   3. 本文件挂载到 docker-entrypoint-initdb.d/ 时，文件名在字母序上应晚于 schema 脚本，
--      保证先建表、再建事件（当前：zipkin-mysql-schema.sql < zipkin-mysql-ttl-event.sql）。
--
-- 注意（首次初始化 vs 已有数据卷）：
--   docker-entrypoint-initdb.d/ 仅在数据目录为空、首次初始化时执行。若 volume 里已有库，
--   需手动在目标库执行本脚本（或 DROP EVENT 后重建），否则不会出现本事件定义。
--
-- 大数据量下的删除策略（性能与稳定性）：
--   - 避免单条 DELETE 删除全表过期行：事务持有时间长、undo/redo 体积大、复制延迟高。
--   - 采用「小批次 + REPEAT 循环」：每批最多 batch_size 个 span，多轮删除直到删净。
--   - zipkin_spans 上官方 schema 含 start_ts 索引；内层查询按 start_ts 过滤并 ORDER BY，
--     便于优化器走索引范围扫描，按时间顺序成批取出待删主键。
--   - 每一批内顺序：先删 zipkin_annotations（子表），再删 zipkin_spans（父逻辑行）。官方未建
--     外键，但若先删 span 会留下孤儿注解行，故必须先删注解。
--   - 结束条件：以「第二批 DELETE（删 span）的 ROW_COUNT()」是否为 0 为准。为 0 表示当前
--     cutoff 下已没有可删 span；注解要么已随上一批 span 删光，要么本批 span 本就没有注解，
--     第一种 DELETE 可能返回 0 不影响退出判断。
--   - IN (子查询) 使用「外层 SELECT + 内层派生表」两层结构：内层先物化出一批主键，外层再供
--     DELETE 引用，避免 MySQL/MariaDB「不能在子查询中直接引用正在更新的同一张表」类限制。
--   - 单次事件触发内会循环直到删完本 cutoff 下全部符合条件数据；积压极大时本轮可能较久，
--     可通过减小 batch_size 降低单批锁与日志压力（总批次数增加）。
-- =============================================================================

-- 使用下面SQL查看Event执行状态
-- SELECT EVENT_SCHEMA, EVENT_NAME, STATUS, LAST_EXECUTED, INTERVAL_VALUE, INTERVAL_FIELD, STARTS, ENDS FROM information_schema.EVENTS WHERE EVENT_SCHEMA = 'zipkin' AND EVENT_NAME = 'ev_zipkin_purge';

-- 与 mysqld --event-scheduler=ON 双保险；部分环境仅配置文件开启后，此处再设一次 GLOBAL。
SET GLOBAL event_scheduler = ON;

DELIMITER $$
-- ev_zipkin_purge：事件名，IF NOT EXISTS 避免重复执行本脚本时报错（升级时需自行 DROP/ALTER）
CREATE EVENT IF NOT EXISTS ev_zipkin_purge
-- EVERY 1 MINUTE：调度周期。保留窗口为 5 分钟时，每分钟扫一次即可；过密增加负载，过疏则
-- 过期数据在库中多驻留一段时间。
ON SCHEDULE EVERY 1 DAY
-- STARTS CURRENT_TIMESTAMP：创建后尽快第一次执行，不必等到「整分」。
STARTS CURRENT_TIMESTAMP
-- ON COMPLETION PRESERVE：事件执行结束后保留定义（默认行为亦多为保留；写明确便于阅读）。
ON COMPLETION PRESERVE
-- ENABLE：创建后立即启用；对应还有 DISABLE ON SLAVE 等场景，此处为单机清理场景。
ENABLE
DO
BEGIN
  -- cutoff：保留窗口下界。小于该 start_ts 的 span 视为过期（单位：微秒，与 Zipkin 写入一致）。
  DECLARE cutoff BIGINT;
  -- 每轮循环最多处理多少个 span（按最旧优先）。仅影响批大小，不改变「删到没有为止」的语义。
  DECLARE batch_size INT DEFAULT 2000;
  -- spans_deleted：上一批「DELETE zipkin_spans」删除的行数，作为 UNTIL 的退出依据。
  DECLARE spans_deleted INT;

  -- UNIX_TIMESTAMP() 为秒；Zipkin start_ts 为微秒，故 * 1000000。
  -- 3600 * 24 * 30 即保留最近 30 天。若改保留时长，须同步修改 docker-compose 中 QUERY_LOOKBACK、
  -- ZIPKIN_UI_DEFAULT_LOOKBACK（毫秒）等业务配置，避免「库已删、UI 仍查很久」或反之。
  SET cutoff = (UNIX_TIMESTAMP() - 3600 * 24 * 30) * 1000000;

  -- 循环体：每迭代删除「当前最旧的、最多 batch_size 条」过期 span 及其注解，直到 span 删无可删。
  REPEAT
    -- ---------- 第一批 DELETE：zipkin_annotations ----------
    -- 仅删除「本批 span 主键」所对应的注解行。span_batch 与下面删 span 的子查询条件一致，
    -- 保证同一迭代内处理的 span 集合相同。
    DELETE FROM zipkin_annotations
    WHERE (trace_id_high, trace_id, span_id) IN (
      SELECT trace_id_high, trace_id, id FROM (
        SELECT trace_id_high, trace_id, id
        FROM zipkin_spans
        -- start_ts 为 NULL 的 span 不参与 TTL（异常或未完成数据），避免误删或无法排序定位。
        WHERE start_ts IS NOT NULL AND start_ts < cutoff
        -- 最旧优先，与索引方向一致，便于稳定、可重复的批量删除。
        ORDER BY start_ts
        LIMIT batch_size
      ) AS span_batch
    );

    -- ---------- 第二批 DELETE：zipkin_spans ----------
    -- 删除与上一语句相同的一批主键行。若本批已无过期 span，此处删除 0 行，循环结束。
    DELETE FROM zipkin_spans
    WHERE (trace_id_high, trace_id, id) IN (
      SELECT trace_id_high, trace_id, id FROM (
        SELECT trace_id_high, trace_id, id
        FROM zipkin_spans
        WHERE start_ts IS NOT NULL AND start_ts < cutoff
        ORDER BY start_ts
        LIMIT batch_size
      ) AS span_batch
    );
    SET spans_deleted = ROW_COUNT();
  UNTIL spans_deleted = 0 END REPEAT;
END$$
DELIMITER ;

-- ---------------------------------------------------------------------------
-- 未处理 zipkin_dependencies 的原因（简要）：
--   该表存按「自然日」聚合的依赖边，由依赖任务从 span 计算写入，与「span 5 分钟 TTL」粒度
--   不一致；若需收缩，可另加针对 day 条件的分批 DELETE，或依赖离线任务重建。
-- ---------------------------------------------------------------------------
