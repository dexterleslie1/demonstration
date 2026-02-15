-- 版本化迁移：为 user 表增加 email 列
ALTER TABLE user ADD COLUMN email VARCHAR(255) NULL AFTER name;
