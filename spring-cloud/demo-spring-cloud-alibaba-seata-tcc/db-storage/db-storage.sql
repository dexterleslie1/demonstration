create database if not exists `storage` character set utf8mb4 collate utf8mb4_general_ci;

use `storage`;

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
    `freeze_stock`   int(11) default 0 comment '冻结库存',
    `state`                 ENUM('Try', 'Confirm', 'Cancel') not null comment '事务状态'
) engine = innodb default charset = utf8mb4;

create index idx_storage_freeze_product_id on t_storage_freeze(product_id);
