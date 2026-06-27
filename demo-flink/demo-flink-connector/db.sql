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

CREATE TABLE IF NOT EXISTS `parent_order`(
    id                  BIGINT NOT NULL PRIMARY KEY,
    company_id          BIGINT NOT NULL,
    event_time          DATETIME NOT NULL COMMENT '事件时间'
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `child_item`(
    id                  BIGINT NOT NULL PRIMARY KEY,
    dj_id               BIGINT NOT NULL COMMENT '关联主表 id',
    content             VARCHAR(255) NOT NULL COMMENT '子表内容'
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `parent_order` (id, company_id, event_time) VALUES
(1, 1, NOW()),
(2, 2, NOW());

INSERT INTO `child_item` (id, dj_id, content) VALUES
(101, 1, 'item-1'),
(102, 2, 'item-2');