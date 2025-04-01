CREATE DATABASE IF NOT EXISTS demo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE demo;

CREATE TABLE IF NOT EXISTS test1(
	id          BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `key`       VARCHAR(64) unique,
    `value`       VARCHAR(64) NOT NULL
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

insert into test1(`key`, `value`) values("key1", "value1");
insert into test1(`key`, `value`) values("key2", "value2");
insert into test1(`key`, `value`) values("key3", "value3");
insert into test1(`key`, `value`) values("key4", "value4");
insert into test1(`key`, `value`) values("key5", "value5");