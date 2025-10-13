create database if not exists `account` character set utf8mb4 collate utf8mb4_general_ci;

use `account`;

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

create table if not exists `t_account`(
    `id`            bigint(11) not null auto_increment primary key comment 'id',
    `user_id`       bigint(11) default null comment '用户id',
    `total`         decimal(10,0) default null comment '总额度',
    `used`          decimal(10,0) default null comment '已用帐户余额',
    `residue`       decimal(10,0) default '0' comment '剩余可用额度'
) engine = innodb auto_increment = 1 default charset = utf8mb4;

create unique index idx_account_user_id on t_account(user_id);

insert into `t_account`(`user_id`, `total`, `used`, `residue`) values (1, 1000, 0, 1000);

/* TCC 事务冻结金额 */
create table if not exists `t_account_freeze`(
    `xid`           varchar(128) not null primary key,
    `user_id`       bigint(11) not null,
    `freeze_money`  int(11) default 0 comment '冻结金额'
) engine = innodb default charset = utf8mb4;

create index idx_account_freeze_user_id on t_account_freeze(user_id);
