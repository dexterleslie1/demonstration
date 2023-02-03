CREATE DATABASE IF NOT EXISTS demo_springboot_redis_cache_integration CHARACTER SET utf8 COLLATE utf8_unicode_ci;

USE demo_springboot_redis_cache_integration;

create table if not exists t_product (
  id bigint primary key auto_increment,
  quantity int default 0 not null,
  createTime datetime not null
) engine=InnoDB default charset=utf8;