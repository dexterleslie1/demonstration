DROP PROCEDURE IF EXISTS sp_insert_parent_order;

CREATE PROCEDURE sp_insert_parent_order()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE next_id BIGINT;

SELECT IFNULL(MAX(id), 0) + 1 INTO next_id FROM parent_order;

WHILE i <= 10000 DO
        INSERT INTO parent_order (id, company_id, event_time)
        VALUES (
            next_id,
            (i MOD 5) + 1,
            DATE_ADD(NOW(), INTERVAL i SECOND)
        );
        SET next_id = next_id + 1;
        SET i = i + 1;
END WHILE;
END;

call sp_insert_parent_order();