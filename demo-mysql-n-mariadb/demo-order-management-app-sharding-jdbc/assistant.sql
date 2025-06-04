delimiter |

begin not atomic
	declare var_counter int default 1;
    declare var_total int default 32;
    set @sql = '';
    /*set @sql = 'select sum(count) from ( ';*/
    while var_counter<=var_total do
		-- 使用 CONCAT 拼接表名
		SET @table_name = CONCAT('t_order', var_counter);

		-- 动态生成 SQL 语句
		SET @sql = CONCAT(@sql,'select ', var_counter, ',count(id) as count from ', @table_name);

		if var_counter<var_total then
			set @sql = concat(@sql, ' union ');
        end if;

		set var_counter = var_counter + 1;
    end while;
    /*set @sql = concat(@sql, ' ) a;');*/

    -- 准备并执行动态 SQL
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
end|

delimiter ;

/* 查询 t_order_index_list_by_userid */
delimiter |

begin not atomic
	declare var_counter int default 1;
    declare var_total int default 32;
    set @sql = '';
    /*set @sql = 'select sum(count) from ( ';*/
    while var_counter<=var_total do
		-- 使用 CONCAT 拼接表名
		SET @table_name = CONCAT('t_order_index_list_by_userid', var_counter);

		-- 动态生成 SQL 语句
		SET @sql = CONCAT(@sql,'select ', var_counter, ',count(id) as count from ', @table_name);

		if var_counter<var_total then
			set @sql = concat(@sql, ' union ');
end if;

		set var_counter = var_counter + 1;
end while;
    /*set @sql = concat(@sql, ' ) a;');*/

    -- 准备并执行动态 SQL
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
end|

delimiter ;

select count(id) from t_product;
