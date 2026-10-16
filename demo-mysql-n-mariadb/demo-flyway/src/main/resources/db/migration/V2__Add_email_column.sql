-- 版本化迁移：为 user 表增加 email、status 列，并插入待 Java 迁移整理的样本数据
ALTER TABLE user ADD COLUMN email VARCHAR(255) NULL AFTER name;

INSERT INTO user (name, email) VALUES
('Alice', 'Alice@Example.com'),
('   ', '  BOB@Example.COM  '),
('Carol', NULL);
