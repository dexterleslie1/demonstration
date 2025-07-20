CREATE KEYSPACE IF NOT EXISTS demo WITH REPLICATION ={'class' : 'SimpleStrategy','replication_factor' : '3'};

USE demo;

drop table if exists t_order;
CREATE TABLE IF NOT EXISTS t_order
(
    id            bigint primary key,
    user_id       bigint,
    status        text,      -- 使用text代替ENUM，Cassandra不支持ENUM类型
    pay_time      timestamp, -- 使用timestamp代替datetime
    delivery_time timestamp,
    received_time timestamp,
    cancel_time   timestamp,
    delete_status text,
    create_time   timestamp
);

drop table if exists t_order_list_by_userId;
CREATE TABLE IF NOT EXISTS t_order_list_by_userId
(
    user_id       bigint,
    status        text,
    create_time   timestamp,
    order_id      bigint,

    merchant_id   bigint,
    pay_time      timestamp,
    delivery_time timestamp,
    received_time timestamp,
    cancel_time   timestamp,
    order_detail_json text,
    /* 使用 user_id,status 作为分区键快速定位数据，使用 create_time,order_id 作为聚类键范围查询和排序 */
    primary key ((user_id,status),create_time,order_id)
) with clustering order by (create_time desc,order_id desc);

drop table if exists t_order_detail;
CREATE TABLE IF NOT EXISTS t_order_detail
(
    id          bigint,
    order_id    bigint,
    user_id     bigint,
    product_id  bigint,
    merchant_id bigint,
    amount      int,
    PRIMARY KEY (order_id)
);

drop table if exists t_count;
create table if not exists t_count (
   flag        text,
   count       counter,
   primary key (flag)
);

update t_count set count=count+0 where flag='order';
update t_count set count=count+0 where flag='orderListByUserId';
