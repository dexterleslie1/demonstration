CREATE DATABASE IF NOT EXISTS demo_db CHARACTER SET utf8 COLLATE utf8_unicode_ci;

USE demo_db;

/** 操作记录表 */
CREATE TABLE IF NOT EXISTS `operation_log`(
    id                  BIGINT primary key NOT NULL AUTO_INCREMENT,
    auth_id             BIGINT NOT NULL COMMENT '当前操作日志属于哪个用户，操作日志列表接口根据该字段获取指定用户下的操作日志',
    operator_id         BIGINT NOT NULL COMMENT '操作人',
    passive_id          BIGINT NOT NULL COMMENT '被操作人',
    /*operation_type      VARCHAR(64) NOT NULL COMMENT '操作类型',*/
    operation_type      TINYINT NOT NULL COMMENT '操作类型',
    content             TEXT COMMENT '操作内容',
    create_time         DATETIME NOT NULL COMMENT '操作时间'/*,
    primary key(id, auth_id)*/
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

ALTER TABLE operation_log MODIFY COLUMN passive_id BIGINT COMMENT '被操作人';
ALTER TABLE operation_log ADD COLUMN IF NOT EXISTS auth_id BIGINT NOT NULL COMMENT '当前操作日志属于哪个用户，操作日志列表接口根据该字段获取指定用户下的操作日志';

-- optimize: 创建以下索引后对于查询性能有飞跃式的提高
create index if not exists idx_operation_log_comb1 on operation_log (auth_id, operation_type);