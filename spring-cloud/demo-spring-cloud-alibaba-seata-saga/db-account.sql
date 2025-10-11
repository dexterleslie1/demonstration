create database if not exists `account` character set utf8mb4 collate utf8mb4_general_ci;

use `account`;
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

create table if not exists `t_account`(
    `id`            bigint(11) not null auto_increment primary key comment 'id',
    `user_id`       bigint(11) default null comment '用户id',
    `total`         decimal(10,0) default null comment '总额度',
    `used`          decimal(10,0) default null comment '已用帐户余额',
    `residue`       decimal(10,0) default '0' comment '剩余可用额度'
) engine = innodb auto_increment = 1 default charset = utf8mb4;

insert into `t_account`(`user_id`, `total`, `used`, `residue`) values (1, 1000, 0, 1000);