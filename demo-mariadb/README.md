## 存储过程



### 打印调试信息

> https://stackoverflow.com/questions/3314771/print-debugging-info-from-stored-procedure-in-mysql

```
delimiter |

begin not atomic
	declare my_var1 varchar(64) default '';
    set my_var1 = 'Dexter!';
    
    select concat('Hello', my_var1) as debug_info;
end|

delimiter ;
```



### 游标用法

```
delimiter |

begin not atomic
	-- https://navicat.com/en/company/aboutus/blog/1714-iterate-over-query-result-sets-using-a-cursor
    
    declare finished int default 0;
    declare my_id bigint default 0;
    declare my_col1 varchar(64) default '';
    declare my_code varchar(1024) default '';
    declare my_cursor cursor for select id,col1 from my_test_tbl order by id desc;
    declare continue handler for not found set finished = 1;
    
    -- 如果表存在则删除
    drop table if exists my_test_tbl;
    
    -- 重新创建表
    create table if not exists my_test_tbl(
		id bigint not null primary key auto_increment,
        col1 varchar(64)
    );
    
    -- 准备测试数据
    insert into my_test_tbl(col1) values('1');
    insert into my_test_tbl(col1) values('2');
    insert into my_test_tbl(col1) values('3');
    
    -- 打开游标
    open my_cursor;
    
    my_loop: loop
		-- 读取游标的记录到变量中
		fetch my_cursor into my_id, my_col1;
        
        -- 读取到最后一条记录
        if finished = 1 then
			-- 跳出循环
			leave my_loop;
        end if;
        
		set my_code = concat(my_col1, ';', my_code);
    end loop my_loop;
    
    select my_code as debug_info;
    
    -- 关闭游标
    close my_cursor;
end|

delimiter ;
```



### mariadb匿名存储过程

> NOTE: 使用DataGrid执行匿名存储过程报告语法错误，但使用MySQLWorkbench不会存在此问题

```
delimiter |

begin not atomic
	select now();
end|

delimiter ;
```

