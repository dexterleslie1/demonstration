create database if not exists `storage` character set utf8mb4 collate utf8mb4_general_ci;

use `storage`;

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

create table if not exists `t_storage`
(
    `id`            bigint(11) not null auto_increment primary key comment 'id',
    `product_id`    bigint(11) default null comment '产品id',
    `total`         int(11)    default null comment '总库存',
    `used`          int(11)    default null comment '已用库存',
    `residue`       int(11)    default null comment '剩余库存'
) engine = innodb auto_increment = 1 default charset = utf8mb4;

create unique index idx_storage_product_id on t_storage(product_id);

insert into `t_storage`(`product_id`, `total`, `used`, `residue`) values (1, 100, 0, 100);

/* TCC 事务冻结库存 */
create table if not exists `t_storage_freeze`(
    `xid`                   varchar(128) not null primary key,
    `product_id`            bigint(11) not null,
    `freeze_stock`   int(11) default 0 comment '冻结库存'
) engine = innodb default charset = utf8mb4;

create index idx_storage_freeze_product_id on t_storage_freeze(product_id);
