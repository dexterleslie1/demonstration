CREATE TABLE IF NOT EXISTS `dept` (
  id 		    INT NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '部门id',
  parent_id 	INT COMMENT '上级部门id',
  `name`		VARCHAR(255) NOT NULL COMMENT '部门名称',
  order_num     INT NOT NULL DEFAULT 1 COMMENT '显示顺序',
  create_time   DATETIME NOT NULL DEFAULT CURRENT_TIME() COMMENT '创建时间'
) ENGINE = INNODB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门表';

-- 插入部门测试数据
INSERT INTO `dept` (`id`, `parent_id`, `name`, `order_num`) VALUES
-- 顶级部门 (parent_id 为 NULL)
(1, NULL, '总公司', 1),
(2, NULL, '技术中心', 2),
(3, NULL, '运营中心', 3),

-- 技术中心下属部门
(4, 2, '前端开发部', 1),
(5, 2, '后端开发部', 2),
(6, 2, '测试部', 3),
(7, 2, '运维部', 4),
(8, 2, '产品部', 5),

-- 运营中心下属部门
(9, 3, '市场部', 1),
(10, 3, '销售部', 2),
(11, 3, '客服部', 3),
(12, 3, '品牌部', 4),

-- 后端开发部下属团队
(13, 5, 'Java开发组', 1),
(14, 5, 'Python开发组', 2),
(15, 5, 'Go开发组', 3),

-- 前端开发部下属团队
(16, 4, 'Web前端组', 1),
(17, 4, '移动端组', 2),
(18, 4, '小程序组', 3),

-- 销售部下属团队
(19, 10, '华北销售组', 1),
(20, 10, '华南销售组', 2),
(21, 10, '华东销售组', 3),

-- 市场部下属团队
(22, 9, '数字营销组', 1),
(23, 9, '内容创作组', 2),
(24, 9, '活动策划组', 3);

CREATE TABLE IF NOT EXISTS `user`(
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '用户id',
    dept_id BIGINT NOT NULL COMMENT '所属部门id',
    user_name VARCHAR(30) NOT NULL COMMENT '用户账号',
    nick_name VARCHAR(30) NOT NULL COMMENT '用户昵称',
    `password` VARCHAR(64) NULL COMMENT '用户登录密码',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIME() COMMENT '创建时间'
) ENGINE = INNODB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表';

INSERT INTO `user` (dept_id, user_name, nick_name, create_time) VALUES
(1, 'zhangsan', '张三', '2024-01-15 09:30:00'),
(2, 'lisi', '李四', '2024-01-16 10:15:00'),
(1, 'wangwu', '王五', '2024-01-17 14:20:00'),
(3, 'zhaoliu', '赵六', '2024-01-18 11:45:00'),
(2, 'sunqi', '孙七', '2024-01-19 16:30:00'),
(3, 'zhouba', '周八', '2024-01-20 08:50:00'),
(1, 'wujiu', '吴九', '2024-01-21 13:10:00'),
(4, 'zhengshi', '郑十', '2024-01-22 15:25:00'),
(2, 'liuyi', '刘一', '2024-01-23 10:40:00'),
(5, 'chener', '陈二', '2024-01-24 17:05:00');
