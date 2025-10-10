create database if not exists `account` character set utf8mb4 collate utf8mb4_general_ci;

use `account`;

create table if not exists `t_account`(
    `id`            bigint(11) not null auto_increment primary key comment 'id',
    `user_id`       bigint(11) default null comment '用户id',
    `total`         decimal(10,0) default null comment '总额度',
    `used`          decimal(10,0) default null comment '已用帐户余额',
    `residue`       decimal(10,0) default '0' comment '剩余可用额度'
) engine = innodb auto_increment = 1 default charset = utf8mb4;

insert into `t_account`(`user_id`, `total`, `used`, `residue`) values (1, 1000, 0, 1000);

/* TCC 事务冻结金额 */
create table if not exists `t_account_freeze`(
    `xid`           varchar(128) not null primary key,
    `user_id`       bigint(11) not null,
    `freeze_money`  int(11) default 0 comment '冻结金额',
    `state`         ENUM('Try', 'Confirm', 'Cancel') not null comment '事务状态'
) engine = innodb default charset = utf8mb4;