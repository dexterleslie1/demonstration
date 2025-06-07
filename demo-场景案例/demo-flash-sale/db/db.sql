create database if not exists demo character set utf8mb4 collate utf8mb4_general_ci;

use demo;

create table if not exists t_order(
    id              bigint not null primary key,
    userId          bigint not null,
    `status`        ENUM('Unpay','Undelivery','Unreceive','Received','Canceled') NOT NULL COMMENT '订单状态：未支付、未发货、未收货、已签收、买家取消',
    payTime         datetime default null comment '付款时间',
    deliveryTime    datetime default null comment '发货时间',
    receivedTime    datetime default null comment '签收时间',
    cancelTime      datetime default null comment '取消时间',
    deleteStatus    ENUM('Normal','Deleted') NOT NULL COMMENT '订单删除状态',
    createTime      datetime not null
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

create table if not exists t_order_detail (
  id          bigint not null primary key,
  orderId     bigint not null,
  userId      bigint not null comment '协助简化和提高用户重复下单判断逻辑',
  productId   bigint not null,
  merchantId  bigint not null comment '商家ID',
  amount      int not null
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

/* 订单getById时使用此索引获取订单明细 */
create index idx_orderDetail_orderId on t_order_detail(orderId);

create table if not exists t_product(
    id          bigint not null auto_increment primary key,
    `name`      varchar(255) not null,
    merchantId  bigint not null comment '商家ID',
    stock       int not null
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

create table if not exists t_count(
    flag        varchar(32) not null primary key,
    `count`     bigint not null default 0
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

insert into t_count(flag,`count`) values("order",0);
