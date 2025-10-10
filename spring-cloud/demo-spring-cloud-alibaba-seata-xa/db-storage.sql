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

insert into `t_storage`(`product_id`, `total`, `used`, `residue`) values (1, 100, 0, 100);
