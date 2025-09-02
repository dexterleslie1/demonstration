create database if not exists demo character set utf8mb4 collate utf8mb4_general_ci;

use demo;

CREATE TABLE `performance_test` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(100) NOT NULL,
    `value` int NOT NULL,
    `description` text,
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`),
    KEY `idx_value` (`value`)
) engine=innodb character set utf8mb4 collate utf8mb4_general_ci;

-- 插入100万条测试数据
DELIMITER //
CREATE PROCEDURE insert_test_data()
BEGIN
  DECLARE i INT DEFAULT 1;
  WHILE i <= 1000000 DO
    INSERT INTO performance_test (name, value, description)
    VALUES (CONCAT('Item-', i), FLOOR(RAND()*1000),
            CONCAT('Description for item ', i, ' with random value ', FLOOR(RAND()*1000)));
    SET i = i + 1;
END WHILE;
END//
DELIMITER ;

CALL insert_test_data();
