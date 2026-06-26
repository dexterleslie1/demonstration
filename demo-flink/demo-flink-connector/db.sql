USE demo;

CREATE TABLE IF NOT EXISTS `auth`(
    id                  BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    account             VARCHAR(64) NOT NULL UNIQUE COMMENT '账号',
    `password`          VARCHAR(64) NOT NULL COMMENT '密码',
    create_time         DATETIME NOT NULL COMMENT '创建时间'
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `auth` (account, `password`, create_time) VALUES
('admin', 'admin123', NOW()),
('user1', 'user123', NOW()),
('user2', 'user123', NOW());