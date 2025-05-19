CREATE KEYSPACE IF NOT EXISTS demo WITH REPLICATION ={'class' : 'SimpleStrategy','replication_factor' : '1'};

USE demo;

CREATE TABLE IF NOT EXISTS t_order
(
    id            decimal PRIMARY KEY,
    user_id       bigint,
    status        text,      -- 使用text代替ENUM，Cassandra不支持ENUM类型
    pay_time      timestamp, -- 使用timestamp代替datetime
    delivery_time timestamp,
    received_time timestamp,
    cancel_time   timestamp,
    delete_status text,
    create_time   timestamp
);

CREATE TABLE IF NOT EXISTS t_order_detail
(
    id          bigint,
    order_id    decimal,
    user_id     bigint,
    product_id  bigint,
    merchant_id bigint,
    amount      int,
    PRIMARY KEY ((order_id), id) -- 复合主键，order_id为分区键，detail_id为聚类键
) WITH CLUSTERING ORDER BY (id ASC);