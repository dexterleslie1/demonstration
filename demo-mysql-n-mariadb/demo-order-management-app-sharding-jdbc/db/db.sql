create database if not exists demo character set utf8mb4 collate utf8mb4_general_ci;

use demo;

create table if not exists t_order(
    id              decimal(20,0) not null primary key,
    userId          bigint not null,
    `status`        ENUM('Unpay','Undelivery','Unreceive','Received','Canceled') NOT NULL COMMENT '订单状态：未支付、未发货、未收货、已签收、买家取消',
    payTime         datetime default null comment '付款时间',
    deliveryTime    datetime default null comment '发货时间',
    receivedTime    datetime default null comment '签收时间',
    cancelTime      datetime default null comment '取消时间',
    deleteStatus    ENUM('Normal','Deleted') NOT NULL COMMENT '订单删除状态',
    createTime      datetime not null
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

/*create index idx_order_userId_createTime_deleteStatus_status on t_order(userId,createTime,deleteStatus,status);
create index idx_order_status_deleteStatus_createTime_id on t_order(status, deleteStatus, createTime, id);*/

create table if not exists t_order1(
    id              decimal(20,0) not null primary key,
    userId          bigint not null,
    `status`        ENUM('Unpay','Undelivery','Unreceive','Received','Canceled') NOT NULL COMMENT '订单状态：未支付、未发货、未收货、已签收、买家取消',
    payTime         datetime default null comment '付款时间',
    deliveryTime    datetime default null comment '发货时间',
    receivedTime    datetime default null comment '签收时间',
    cancelTime      datetime default null comment '取消时间',
    deleteStatus    ENUM('Normal','Deleted') NOT NULL COMMENT '订单删除状态',
    createTime      datetime not null
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

/*create index idx_order1_userId_createTime_deleteStatus_status on t_order1(userId,createTime,deleteStatus,status);
create index idx_order1_status_deleteStatus_createTime_id on t_order1(status, deleteStatus, createTime, id);*/

create table if not exists t_order2(
    id              decimal(20,0) not null primary key,
    userId          bigint not null,
    `status`        ENUM('Unpay','Undelivery','Unreceive','Received','Canceled') NOT NULL COMMENT '订单状态：未支付、未发货、未收货、已签收、买家取消',
    payTime         datetime default null comment '付款时间',
    deliveryTime    datetime default null comment '发货时间',
    receivedTime    datetime default null comment '签收时间',
    cancelTime      datetime default null comment '取消时间',
    deleteStatus    ENUM('Normal','Deleted') NOT NULL COMMENT '订单删除状态',
    createTime      datetime not null
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

/*create index idx_order2_userId_createTime_deleteStatus_status on t_order2(userId,createTime,deleteStatus,status);
create index idx_order2_status_deleteStatus_createTime_id on t_order2(status, deleteStatus, createTime, id);*/

create table if not exists t_order3(
    id              decimal(20,0) not null primary key,
    userId          bigint not null,
    `status`        ENUM('Unpay','Undelivery','Unreceive','Received','Canceled') NOT NULL COMMENT '订单状态：未支付、未发货、未收货、已签收、买家取消',
    payTime         datetime default null comment '付款时间',
    deliveryTime    datetime default null comment '发货时间',
    receivedTime    datetime default null comment '签收时间',
    cancelTime      datetime default null comment '取消时间',
    deleteStatus    ENUM('Normal','Deleted') NOT NULL COMMENT '订单删除状态',
    createTime      datetime not null
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

/*create index idx_order3_userId_createTime_deleteStatus_status on t_order3(userId,createTime,deleteStatus,status);
create index idx_order3_status_deleteStatus_createTime_id on t_order3(status, deleteStatus, createTime, id);*/

create table if not exists t_order4(
    id              decimal(20,0) not null primary key,
    userId          bigint not null,
    `status`        ENUM('Unpay','Undelivery','Unreceive','Received','Canceled') NOT NULL COMMENT '订单状态：未支付、未发货、未收货、已签收、买家取消',
    payTime         datetime default null comment '付款时间',
    deliveryTime    datetime default null comment '发货时间',
    receivedTime    datetime default null comment '签收时间',
    cancelTime      datetime default null comment '取消时间',
    deleteStatus    ENUM('Normal','Deleted') NOT NULL COMMENT '订单删除状态',
    createTime      datetime not null
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

/*create index idx_order4_userId_createTime_deleteStatus_status on t_order4(userId,createTime,deleteStatus,status);
create index idx_order4_status_deleteStatus_createTime_id on t_order4(status, deleteStatus, createTime, id);*/

create table if not exists t_order5(
    id              decimal(20,0) not null primary key,
    userId          bigint not null,
    `status`        ENUM('Unpay','Undelivery','Unreceive','Received','Canceled') NOT NULL COMMENT '订单状态：未支付、未发货、未收货、已签收、买家取消',
    payTime         datetime default null comment '付款时间',
    deliveryTime    datetime default null comment '发货时间',
    receivedTime    datetime default null comment '签收时间',
    cancelTime      datetime default null comment '取消时间',
    deleteStatus    ENUM('Normal','Deleted') NOT NULL COMMENT '订单删除状态',
    createTime      datetime not null
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

/*create index idx_order5_userId_createTime_deleteStatus_status on t_order5(userId,createTime,deleteStatus,status);
create index idx_order5_status_deleteStatus_createTime_id on t_order5(status, deleteStatus, createTime, id);*/

create table if not exists t_order6(
    id              decimal(20,0) not null primary key,
    userId          bigint not null,
    `status`        ENUM('Unpay','Undelivery','Unreceive','Received','Canceled') NOT NULL COMMENT '订单状态：未支付、未发货、未收货、已签收、买家取消',
    payTime         datetime default null comment '付款时间',
    deliveryTime    datetime default null comment '发货时间',
    receivedTime    datetime default null comment '签收时间',
    cancelTime      datetime default null comment '取消时间',
    deleteStatus    ENUM('Normal','Deleted') NOT NULL COMMENT '订单删除状态',
    createTime      datetime not null
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

/*create index idx_order6_userId_createTime_deleteStatus_status on t_order6(userId,createTime,deleteStatus,status);
create index idx_order6_status_deleteStatus_createTime_id on t_order6(status, deleteStatus, createTime, id);*/

create table if not exists t_order7(
    id              decimal(20,0) not null primary key,
    userId          bigint not null,
    `status`        ENUM('Unpay','Undelivery','Unreceive','Received','Canceled') NOT NULL COMMENT '订单状态：未支付、未发货、未收货、已签收、买家取消',
    payTime         datetime default null comment '付款时间',
    deliveryTime    datetime default null comment '发货时间',
    receivedTime    datetime default null comment '签收时间',
    cancelTime      datetime default null comment '取消时间',
    deleteStatus    ENUM('Normal','Deleted') NOT NULL COMMENT '订单删除状态',
    createTime      datetime not null
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

/*create index idx_order7_userId_createTime_deleteStatus_status on t_order7(userId,createTime,deleteStatus,status);
create index idx_order7_status_deleteStatus_createTime_id on t_order7(status, deleteStatus, createTime, id);*/

create table if not exists t_order8(
    id              decimal(20,0) not null primary key,
    userId          bigint not null,
    `status`        ENUM('Unpay','Undelivery','Unreceive','Received','Canceled') NOT NULL COMMENT '订单状态：未支付、未发货、未收货、已签收、买家取消',
    payTime         datetime default null comment '付款时间',
    deliveryTime    datetime default null comment '发货时间',
    receivedTime    datetime default null comment '签收时间',
    cancelTime      datetime default null comment '取消时间',
    deleteStatus    ENUM('Normal','Deleted') NOT NULL COMMENT '订单删除状态',
    createTime      datetime not null
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

/*create index idx_order8_userId_createTime_deleteStatus_status on t_order8(userId,createTime,deleteStatus,status);
create index idx_order8_status_deleteStatus_createTime_id on t_order8(status, deleteStatus, createTime, id);*/

create table if not exists t_order_detail (
    id          bigint not null primary key,
    orderId     decimal(20,0) not null,
    userId      bigint not null comment '协助简化和提高用户重复下单判断逻辑',
    productId   bigint not null,
    merchantId  bigint not null comment '商家ID',
    amount      int not null,
    constraint fk_order_detail_orderId foreign key(orderId) references t_order(id),
    constraint unique_order_detail_userId_n_productId unique(userId,productId)
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

/*create index idx_orderDetail_merchantId on t_order_detail(merchantId);
create index idx_orderDetail_merchantId_orderId on t_order_detail(merchantId, orderId);*/

create table if not exists t_order_detail1 (
    id          bigint not null primary key,
    orderId     decimal(20,0) not null,
    userId      bigint not null comment '协助简化和提高用户重复下单判断逻辑',
    productId   bigint not null,
    merchantId  bigint not null comment '商家ID',
    amount      int not null,
    constraint fk_order_detail1_orderId foreign key(orderId) references t_order1(id),
    constraint unique_order_detail1_userId_n_productId unique(userId,productId)
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

/*create index idx_orderDetail1_merchantId on t_order_detail1(merchantId);
create index idx_orderDetail1_merchantId_orderId on t_order_detail1(merchantId, orderId);*/

create table if not exists t_order_detail2 (
    id          bigint not null primary key,
    orderId     decimal(20,0) not null,
    userId      bigint not null comment '协助简化和提高用户重复下单判断逻辑',
    productId   bigint not null,
    merchantId  bigint not null comment '商家ID',
    amount      int not null,
    constraint fk_order_detail2_orderId foreign key(orderId) references t_order2(id),
    constraint unique_order_detail2_userId_n_productId unique(userId,productId)
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

/*create index idx_orderDetail2_merchantId on t_order_detail2(merchantId);
create index idx_orderDetail2_merchantId_orderId on t_order_detail2(merchantId, orderId);*/

create table if not exists t_order_detail3 (
    id          bigint not null primary key,
    orderId     decimal(20,0) not null,
    userId      bigint not null comment '协助简化和提高用户重复下单判断逻辑',
    productId   bigint not null,
    merchantId  bigint not null comment '商家ID',
    amount      int not null,
    constraint fk_order_detail3_orderId foreign key(orderId) references t_order3(id),
    constraint unique_order_detail3_userId_n_productId unique(userId,productId)
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

/*create index idx_orderDetail3_merchantId on t_order_detail3(merchantId);
create index idx_orderDetail3_merchantId_orderId on t_order_detail3(merchantId, orderId);*/

create table if not exists t_order_detail4 (
    id          bigint not null primary key,
    orderId     decimal(20,0) not null,
    userId      bigint not null comment '协助简化和提高用户重复下单判断逻辑',
    productId   bigint not null,
    merchantId  bigint not null comment '商家ID',
    amount      int not null,
    constraint fk_order_detail4_orderId foreign key(orderId) references t_order4(id),
    constraint unique_order_detail4_userId_n_productId unique(userId,productId)
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

/*create index idx_orderDetail4_merchantId on t_order_detail4(merchantId);
create index idx_orderDetail4_merchantId_orderId on t_order_detail4(merchantId, orderId);*/

create table if not exists t_order_detail5 (
    id          bigint not null primary key,
    orderId     decimal(20,0) not null,
    userId      bigint not null comment '协助简化和提高用户重复下单判断逻辑',
    productId   bigint not null,
    merchantId  bigint not null comment '商家ID',
    amount      int not null,
    constraint fk_order_detail5_orderId foreign key(orderId) references t_order5(id),
    constraint unique_order_detail5_userId_n_productId unique(userId,productId)
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

/*create index idx_orderDetail5_merchantId on t_order_detail5(merchantId);
create index idx_orderDetail5_merchantId_orderId on t_order_detail5(merchantId, orderId);*/

create table if not exists t_order_detail6 (
    id          bigint not null primary key,
    orderId     decimal(20,0) not null,
    userId      bigint not null comment '协助简化和提高用户重复下单判断逻辑',
    productId   bigint not null,
    merchantId  bigint not null comment '商家ID',
    amount      int not null,
    constraint fk_order_detail6_orderId foreign key(orderId) references t_order6(id),
    constraint unique_order_detail6_userId_n_productId unique(userId,productId)
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

/*create index idx_orderDetail6_merchantId on t_order_detail6(merchantId);
create index idx_orderDetail6_merchantId_orderId on t_order_detail6(merchantId, orderId);*/

create table if not exists t_order_detail7 (
    id          bigint not null primary key,
    orderId     decimal(20,0) not null,
    userId      bigint not null comment '协助简化和提高用户重复下单判断逻辑',
    productId   bigint not null,
    merchantId  bigint not null comment '商家ID',
    amount      int not null,
    constraint fk_order_detail7_orderId foreign key(orderId) references t_order7(id),
    constraint unique_order_detail7_userId_n_productId unique(userId,productId)
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

/*create index idx_orderDetail7_merchantId on t_order_detail7(merchantId);
create index idx_orderDetail7_merchantId_orderId on t_order_detail7(merchantId, orderId);*/

create table if not exists t_order_detail8 (
    id          bigint not null primary key,
    orderId     decimal(20,0) not null,
    userId      bigint not null comment '协助简化和提高用户重复下单判断逻辑',
    productId   bigint not null,
    merchantId  bigint not null comment '商家ID',
    amount      int not null,
    constraint fk_order_detail8_orderId foreign key(orderId) references t_order8(id),
    constraint unique_order_detail8_userId_n_productId unique(userId,productId)
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

/*create index idx_orderDetail8_merchantId on t_order_detail8(merchantId);
create index idx_orderDetail8_merchantId_orderId on t_order_detail8(merchantId, orderId);*/

create table if not exists t_product(
    id          bigint not null primary key,
    `name`      varchar(255) not null,
    merchantId  bigint not null comment '商家ID',
    stock       int not null
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

create table if not exists t_id_cache_assistant (
    id          bigint not null auto_increment primary key,
    orderId     decimal(20,0) not null
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;
