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

/*create index idx_order_userId_createTime_deleteStatus_status on t_order(userId,createTime,deleteStatus,status);
create index idx_order_status_deleteStatus_createTime_id on t_order(status, deleteStatus, createTime, id);*/

create table if not exists t_order_detail (
    id          bigint not null primary key,
    orderId     bigint not null,
    userId      bigint not null comment '协助简化和提高用户重复下单判断逻辑',
    productId   bigint not null,
    merchantId  bigint not null comment '商家ID',
    amount      int not null
    /*分布式数据库不支持外键特性，所以在orderId中建立索引即可*/
    /*constraint fk_order_detail_orderId foreign key(orderId) references t_order(id),*/
    /*constraint unique_order_detail_userId_n_productId unique(userId,productId)*/
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

/* 订单getById时使用此索引获取订单明细 */
create index idx_orderDetail_orderId on t_order_detail(orderId);
/*create index idx_orderDetail_merchantId on t_order_detail(merchantId);
create index idx_orderDetail_merchantId_orderId on t_order_detail(merchantId, orderId);*/

create table if not exists t_product(
    id          bigint not null auto_increment primary key,
    `name`      varchar(255) not null,
    merchantId  bigint not null comment '商家ID',
    stock       int not null
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

create table if not exists t_id_cache_assistant (
    id          bigint not null auto_increment primary key,
    orderId     bigint not null
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;
