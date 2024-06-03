CREATE TABLE if not exists `t1`
(
	id BIGINT(20) NOT NULL auto_increment COMMENT '主键ID',
	name VARCHAR(30) NULL DEFAULT NULL COMMENT '姓名',
	age INT(11) NULL DEFAULT NULL COMMENT '年龄',
	email VARCHAR(50) NULL DEFAULT NULL COMMENT '邮箱',
	authorities varchar(256) default null comment '用户权限模拟字符串逗号分隔到java List类型转换',
	PRIMARY KEY (id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

/*创建用户表*/
create table if not exists t_user(
    id int primary key auto_increment,
    loginname varchar(50) not null comment '登录名',
    password varchar(100) not null comment '登录密码',
    createTime datetime not null comment '创建时间'
)engine=InnoDB default charset=utf8;

