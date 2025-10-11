create database if not exists `order` character set utf8mb4 collate utf8mb4_general_ci;

use `order`;
CREATE TABLE IF NOT EXISTS `undo_log`
(
    `branch_id`     BIGINT       NOT NULL COMMENT 'branch transaction id',
    `xid`           VARCHAR(128) NOT NULL COMMENT 'global transaction id',
    `context`       VARCHAR(128) NOT NULL COMMENT 'undo_log context,such as serialization',
    `rollback_info` LONGBLOB     NOT NULL COMMENT 'rollback info',
    `log_status`    INT(11)      NOT NULL COMMENT '0:normal status,1:defense status',
    `log_created`   DATETIME(6)  NOT NULL COMMENT 'create datetime',
    `log_modified`  DATETIME(6)  NOT NULL COMMENT 'modify datetime',
    UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COMMENT ='AT transaction mode undo table';
ALTER TABLE `undo_log` ADD INDEX `ix_log_created` (`log_created`);

create table if not exists `t_order`(
    `id`            bigint(11) not null auto_increment primary key,
    `user_id`       bigint(11) default null comment '用户id',
    `product_id`    bigint(11) default null comment '产品id',
    `count`         int(11)    default null comment '数量',
    `money`         decimal(11,0) default null comment '金额',
    `status`        int(1)     default null comment '订单状态:0:创建中;1:已完结'
) engine = innodb auto_increment = 1 default charset = utf8mb4;
