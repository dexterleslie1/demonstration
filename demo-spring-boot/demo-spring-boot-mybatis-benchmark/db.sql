CREATE DATABASE IF NOT EXISTS demo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE demo;

CREATE TABLE IF NOT EXISTS `order`(
    id BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID' ,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    total_amount DECIMAL(15, 2) NOT NULL COMMENT '金额总数',
    total_count INT NOT NULL COMMENT '商品总数',
    `status` ENUM('Unpay','Undelivery','Unreceive','Received','Canceled') NOT NULL COMMENT '订单状态：未支付、未发货、未收货、已签收、买家取消',
    pay_time DATETIME DEFAULT NULL COMMENT '付款时间',
    delivery_time DATETIME DEFAULT NULL COMMENT '发货时间',
    received_time DATETIME DEFAULT NULL COMMENT '签收时间',
    cancel_time DATETIME DEFAULT NULL COMMENT '取消时间',
    delete_status ENUM('Normal','Deleted') NOT NULL COMMENT '订单删除状态',
    INDEX idx_order_user_id(user_id) USING BTREE,
    INDEX idx_order_merchant_id(merchant_id) USING BTREE,
    INDEX idx_order_user_id_and_status_and_delete_status_and_create_time(user_id,status,delete_status,create_time) USING BTREE,
    INDEX idx_order_user_id_and_delete_status_and_create_time(user_id,delete_status,create_time) USING BTREE,
    INDEX idx_order_merchantId_and_status_and_deleteStatus_and_createTime(merchant_id,status,delete_status,create_time) USING BTREE,
    INDEX idx_order_merchantId_and_deleteStatus_and_createTime(merchant_id,delete_status,create_time) USING BTREE
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
