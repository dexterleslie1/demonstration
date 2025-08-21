create database if not exists demo character set utf8mb4 collate utf8mb4_general_ci;

use demo;

create table if not exists t_order(
    id              /*decimal(30,0)*/bigint not null,
    userId          bigint not null,
    `status`        ENUM('Unpay','Undelivery','Unreceive','Received','Canceled') NOT NULL COMMENT '订单状态：未支付、未发货、未收货、已签收、买家取消',
    payTime         datetime default null comment '付款时间',
    deliveryTime    datetime default null comment '发货时间',
    receivedTime    datetime default null comment '签收时间',
    cancelTime      datetime default null comment '取消时间',
    deleteStatus    ENUM('Normal','Deleted') NOT NULL COMMENT '订单删除状态',
    createTime      datetime not null,
    primary key(id)
    /* 分区表测试时使用下面主键 */
    /*primary key(id,userId)*/
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

create index idx_order_test1 on t_order(userId,deleteStatus,status,createTime);

create table if not exists t_order_detail (
    id          bigint not null primary key,
    orderId     /*decimal(30,0)*/bigint not null,
    userId      bigint not null comment '协助简化和提高用户重复下单判断逻辑',
    productId   bigint not null,
    merchantId  bigint not null comment '商家ID',
    amount      int not null/*,
    constraint fk_order_detail_orderId foreign key(orderId) references t_order(id),
    constraint unique_order_detail_userId_n_productId unique(userId,productId)*/
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

/*create index idx_orderDetail_merchantId on t_order_detail(merchantId);
create index idx_orderDetail_merchantId_orderId on t_order_detail(merchantId, orderId);*/
create index idx_order_detail_orderId on t_order_detail(orderId);

delimiter |

begin not atomic
	declare var_counter int default 1;
    while var_counter<=32 do
		-- 使用 CONCAT 拼接表名
        SET @table_name = CONCAT('t_order', var_counter);

        -- 动态生成 SQL 语句
        SET @sql = CONCAT(
            'CREATE TABLE IF NOT EXISTS ', @table_name, ' (',
            'id              /*DECIMAL(30,0)*/bigint NOT NULL PRIMARY KEY,',
            'userId          BIGINT NOT NULL,',
            '`status`        ENUM(''Unpay'',''Undelivery'',''Unreceive'',''Received'',''Canceled'') NOT NULL COMMENT ''订单状态：未支付、未发货、未收货、已签收、买家取消'',',
            'payTime         DATETIME DEFAULT NULL COMMENT ''付款时间'',',
            'deliveryTime    DATETIME DEFAULT NULL COMMENT ''发货时间'',',
            'receivedTime    DATETIME DEFAULT NULL COMMENT ''签收时间'',',
            'cancelTime      DATETIME DEFAULT NULL COMMENT ''取消时间'',',
            'deleteStatus    ENUM(''Normal'',''Deleted'') NOT NULL COMMENT ''订单删除状态'',',
            'createTime      DATETIME NOT NULL',
            ') ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;'
        );
        -- 准备并执行动态 SQL
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        set @index_name = concat('idx_order', var_counter, '_userId_deleteStatus_status_createTime');
        set @sql = concat('create index ', @index_name, ' on ', @table_name, '(userId,deleteStatus,status,createTime);');
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        /* create index idx_order1_status_deleteStatus_createTime_id on t_order1(status, deleteStatus, createTime, id); */

        /* 根据用户ID查询订单列表索引表 */
        SET @table_name = CONCAT('t_order_index_list_by_userid', var_counter);
        SET @sql = CONCAT(
            'CREATE TABLE IF NOT EXISTS ', @table_name, ' (',
            'id              bigint not null primary key,',
            'userId          bigint not null,',
            '`status`        ENUM(''Unpay'',''Undelivery'',''Unreceive'',''Received'',''Canceled'') NOT NULL COMMENT ''订单状态：未支付、未发货、未收货、已签收、买家取消'',',
            'deleteStatus    ENUM(''Normal'',''Deleted'') NOT NULL COMMENT ''订单删除状态'',',
            'createTime      datetime not null,',
            'orderId         bigint not null',
            ') ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;'
        );
        -- 准备并执行动态 SQL
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        /* 订单修改时，用于协助快速修改索引数据 */
        set @index_name = concat('idx_t_order_index_list_by_userid_on_orderId_', var_counter);
        set @sql = concat('create index ', @index_name, ' on ', @table_name, '(orderId);');
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        set @index_name = concat('idx_t_order_index_list_by_userid_on_x1_', var_counter);
        set @sql = concat('create index ', @index_name, ' on ', @table_name, '(userId,deleteStatus,`status`,createTime);');
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        SET @table_name = CONCAT('t_order_detail', var_counter);
        -- 动态生成 SQL 语句
        SET @sql = CONCAT(
            'CREATE TABLE IF NOT EXISTS ', @table_name, ' (',
            'id              bigint not null primary key,',
            'orderId         /*decimal(30,0)*/bigint not null,',
            'userId          bigint not null comment ''协助简化和提高用户重复下单判断逻辑'',',
            'productId       bigint not null,',
            'merchantId      bigint not null comment ''商家ID'',',
            'amount          int not null/*,*/',
            '/*constraint fk_order_detail', var_counter, '_orderId foreign key(orderId) references t_order', var_counter, '(id),*/',
            '/*constraint unique_order_detail', var_counter, '_userId_n_productId unique(userId,productId)*/',
            ') ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;'
        );
        -- 准备并执行动态 SQL
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        set @index_name = concat('idx_order_detail_orderId_', var_counter);
        set @sql = concat('create index ', @index_name, ' on ', @table_name, '(orderId);');
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        /*create index idx_orderDetail1_merchantId on t_order_detail1(merchantId);
        create index idx_orderDetail1_merchantId_orderId on t_order_detail1(merchantId, orderId);*/

        set var_counter = var_counter + 1;
    end while;
end|

delimiter ;

create table if not exists t_product(
    id          bigint not null primary key,
    `name`      varchar(255) not null,
    merchantId  bigint not null comment '商家ID',
    stock       int not null
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;
