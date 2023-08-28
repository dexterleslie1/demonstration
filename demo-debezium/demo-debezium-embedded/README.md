## 说明

> 调试demo步骤，docker-compose启动容器后和springboot项目后，执行如下SQL观察springboot控制台输出。

```sql
# 手动执行的SQL
insert into t_user(username,`password`,createTime) values('user2','123456',now());
update t_user set username='userx' where username='user2';
delete from t_user where username='userx';
```

