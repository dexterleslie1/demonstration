create database if not exists demo default character set utf8mb4 collate utf8mb4_general_ci;

use demo;

CREATE TABLE `user`
(
	id BIGINT(20) NOT NULL auto_increment COMMENT '主键ID',
	name VARCHAR(30) NULL DEFAULT NULL unique COMMENT '姓名',
	PRIMARY KEY (id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

# 用于协助测试mysql内存
CREATE TABLE IF NOT EXISTS `memory_assistant`
(
    id BIGINT(20) NOT NULL auto_increment COMMENT '主键ID',
    randomStr VARCHAR(1024) not null,
    extraIndexId BIGINT(20),
    PRIMARY KEY (id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

create index memory_assistant_idx1 on memory_assistant(extraIndexId);

# 用于协助测试mysql join_buffer_size
CREATE TABLE IF NOT EXISTS `memory_assistant_join`
(
    id BIGINT(20) NOT NULL auto_increment primary key COMMENT '主键ID',
    randomStr VARCHAR(1024) not null,
    randomStr2 VARCHAR(1024) not null
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

# mysql5.7报告索引字段太长错误，无法创建索引
# create index memory_assistant_join_idx1 on memory_assistant_join(randomStr);

# 用于协助 read_buffer_size 测试
CREATE TABLE IF NOT EXISTS `memory_assistant_myisam`
(
    id BIGINT(20) NOT NULL auto_increment COMMENT '主键ID',
    randomStr VARCHAR(1024) not null,
    extraIndexId BIGINT(20),
    PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

create index memory_assistant_myisam_idx1 on memory_assistant_myisam(extraIndexId);
