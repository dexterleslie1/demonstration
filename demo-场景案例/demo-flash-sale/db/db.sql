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
    merchantId  bigint not null comment '商家ID',
    createTime      datetime not null
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

create table if not exists t_order_detail (
  id          bigint not null primary key,
  orderId     bigint not null,
  userId      bigint not null comment '协助简化和提高用户重复下单判断逻辑',
  productId   bigint not null,
  amount      int not null
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

/* 订单getById时使用此索引获取订单明细 */
create index idx_orderDetail_orderId on t_order_detail(orderId);

create table if not exists t_product(
    /*id          bigint not null auto_increment primary key,*/
    id          bigint not null primary key,
    `name`      varchar(255) not null,
    merchantId  bigint not null comment '商家ID',
    stock       int not null comment '商品库存',
    flashSale   bit not null default 0 comment '是否秒杀商品',
    flashSaleStartTime  datetime default null comment '秒杀开始时间',
    flashSaleEndTime    datetime default null comment '秒杀结束时间'
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

/* t_count 幂等协助表，实现不重复递增 t_count 表 */
create table if not exists t_count_idempotent(
    idempotentId    varchar(64) not null primary key,
    createTime      timestamp not null default current_timestamp()
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;
create table if not exists t_count(
    flag        varchar(32) not null primary key,
    `count`     bigint not null default 0
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

delimiter |
create procedure proc_temp()
begin
    declare var_counter int default 1;
    while var_counter<=256 do
		-- 使用 CONCAT 拼接表名
        SET @flag = CONCAT('order', var_counter);
        -- 动态生成 SQL 语句
        SET @sql = CONCAT(
            'insert into t_count(flag,`count`) values("', @flag, '",0)'
        );
        -- 准备并执行动态 SQL
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        SET @flag = CONCAT('product', var_counter);
        -- 动态生成 SQL 语句
        SET @sql = CONCAT(
            'insert into t_count(flag,`count`) values("', @flag, '",0)'
        );
        -- 准备并执行动态 SQL
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        SET @flag = CONCAT('orderListByUserId', var_counter);
        -- 动态生成 SQL 语句
        SET @sql = CONCAT(
            'insert into t_count(flag,`count`) values("', @flag, '",0)'
        );
        -- 准备并执行动态 SQL
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        SET @flag = CONCAT('orderListByMerchantId', var_counter);
        -- 动态生成 SQL 语句
        SET @sql = CONCAT(
            'insert into t_count(flag,`count`) values("', @flag, '",0)'
        );
        -- 准备并执行动态 SQL
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        set var_counter = var_counter + 1;
    end while;
end|
delimiter ;

call proc_temp();
drop procedure if exists proc_temp;

-- insert into t_count(flag,`count`) values("order",0);
-- insert into t_count(flag,`count`) values("product",0);
-- insert into t_count(flag,`count`) values("orderListByUserId",0);
-- insert into t_count(flag,`count`) values("orderListByMerchantId",0);
