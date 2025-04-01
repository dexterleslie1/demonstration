# 使用`docker compose`运行`openldap`

>示例的详细用法请参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/demo-openldap)

启动`openldap`服务

```bash
docker compose up -d
```

稍等`openldap`服务启动后访问`http://192.168.1.181:18080/`管理`openldap`

登录`phpldapadmin`

- **Login DN**：cn=admin,dc=example,dc=org
- **Password**：xxx
