-- DataSourceInitializer不支持DELIMITER语法，所以存储过程需要独立到一个文件中
-- 并使用 $$ 作为语句的结束符号，因为populator已经设置Separator为$$

/*用户表约束*/
create procedure proc1()
begin
	if not exists(select * from information_schema.TABLE_CONSTRAINTS where
        CONSTRAINT_SCHEMA=database() and
        CONSTRAINT_NAME='t_user_unique_1' and
        CONSTRAINT_TYPE='UNIQUE') then
        alter table t_user add constraint t_user_unique_1 unique(loginname);
    end if;
end $$
call proc1() $$
drop procedure if exists proc1 $$

/*初始化插入用户数据*/
insert into t_user(loginname,password,createTime)
select 'root','jlklkjj',now() from dual
where not exists(select id from t_user where loginname='root') $$
