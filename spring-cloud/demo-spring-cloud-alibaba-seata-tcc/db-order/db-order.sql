create database if not exists `order` character set utf8mb4 collate utf8mb4_general_ci;

use `order`;

CREATE TABLE IF NOT EXISTS `tcc_fence_log`
(
    `xid`           VARCHAR(128)  NOT NULL COMMENT 'global id',
    `branch_id`     BIGINT        NOT NULL COMMENT 'branch id',
    `action_name`   VARCHAR(64)   NOT NULL COMMENT 'action name',
    `status`        TINYINT       NOT NULL COMMENT 'status(tried:1;committed:2;rollbacked:3;suspended:4)',
    `gmt_create`    DATETIME(3)   NOT NULL COMMENT 'create time',
    `gmt_modified`  DATETIME(3)   NOT NULL COMMENT 'update time',
    PRIMARY KEY (`xid`, `branch_id`),
    KEY `idx_xid` (`xid`),
    KEY `idx_gmt_modified` (`gmt_modified`),
    KEY `idx_status` (`status`)
) engine = innodb auto_increment = 1 default charset = utf8mb4;

create table if not exists `t_order`(
    `id`            bigint(11) not null auto_increment primary key,
    `user_id`       bigint(11) default null comment '用户id',
    `product_id`    bigint(11) default null comment '产品id',
    `count`         int(11)    default null comment '数量',
    `money`         decimal(11,0) default null comment '金额',
    `status`        int(1)     default null comment '订单状态:0:创建中;1:已完结'
) engine = innodb auto_increment = 1 default charset = utf8mb4;
