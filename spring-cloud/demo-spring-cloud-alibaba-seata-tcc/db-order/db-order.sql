create database if not exists `order` character set utf8mb4 collate utf8mb4_general_ci;

use `order`;

create table if not exists `t_order`(
    `id`            bigint(11) not null auto_increment primary key,
    `user_id`       bigint(11) default null comment '用户id',
    `product_id`    bigint(11) default null comment '产品id',
    `count`         int(11)    default null comment '数量',
    `money`         decimal(11,0) default null comment '金额',
    `status`        int(1)     default null comment '订单状态:0:创建中;1:已完结'
) engine = innodb auto_increment = 1 default charset = utf8mb4;
