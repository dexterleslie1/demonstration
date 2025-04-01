# 使用`docker compose`运行`jira`

>[atlassian jira docker镜像](https://hub.docker.com/r/cptactionhank/atlassian-jira-software)
>
>[Atlassian jira 8.1.0 破解](https://www.cnblogs.com/tchua/p/10862670.html)
>
>示例中使用`mariadb:10.4.19`作为`jira`数据库，`jira docker`镜像版本为`cptactionhank/atlassian-jira-software:8.1.0`
>
>`jira`搭建详细请参考示例 [链接](https://github.com/dexterleslie1/demonstration/tree/master/demo-jira)

## 使用`docker compose`启动`jira`服务

运行`jira`服务

```bash
docker compose up -d
```

注意：如果`jira`启动失败，通过查看日志分析原因，`jira`日志文件位置`/var/atlassian/jira/log`

等待`jira`服务启动后，访问`http://localhost:18080/`设置`jira`

在设置界面中选择`I'll set it up myself`，点击`next`

选择`My Own Database (recommended for production environments)`自己配置数据库，点击`Test Connection`测试是否成功连接数据库，连接成功后点击`next`

- Database Type： MySQL 5.6
- Hostname：db
- Port：3306
- Database：jiradb
- Username：root
- Password：xxx

设置应用属性信息，点击`next`

- Application Title：xxx（公司的英文名称）
- Mode：private（Only administrators can create new users.）
- Base URL：http://xxx.com:18080（访问`jira`的域名或`ip`地址）

设置`license key`，登录`myatlassian`（`https://my.atlassian.com` g@Kl...）创建`jira`的试用`license`，填写`license key`之后点击`next`

设置管理员帐号，点击`next`

验证`jira`是否已经破解，登录管理元帐号，点击“右上角设置按钮 > 应用程序”，即可看到破解已成功

## `jira`和`openldap`集成

>`jira`集成`openldap` [链接1](https://confluence.atlassian.com/adminjiraserver/connecting-to-an-ldap-directory-938847052.html) [链接2](https://blog.csdn.net/qq_40140473/article/details/95624150)

### 准备`openldap`数据基础数据

1. 访问`http://192.168.1.181:18081/`登录`phpldapadmin`，帐号：cn=admin,dc=example,dc=org，密码：xxx

2. 参考 <a href="/openldap/%E7%AE%A1%E7%90%86openldap.html" target="_blank">管理`openldap`</a> 在`openldap`中手动创建如下数据：

   - 创建开发组用于存储公司开发团队人员数据
     - objectClass(organizationalUnit) DN:ou=development,dc=example,dc=org

   - 在开发组下创建用户admin
     - objectClass(inetOrgPerson) DN:cn=admin,ou=development,dc=example,dc=org

   - 创建jira-groups用于存储jira权限组
     - objectClass(organizationUnit) DN:ou=jira-groups,dc=example,dc=org

   - 在jira-groups组下创建jira-administrators、jira-software-users组
     - objectClass(groupOfUniqueNames) DN:cn=jira-administrators,ou=jira-groups,dc=example,dc=org
     - objectClass(groupOfUniqueNames) DN:cn=jira-software-users,ou=jira-groups,dc=example,dc=org

### 设置`jira`集成`openldap`

1. 访问`http://192.168.1.181:18080`登录`jira`

2. 选择菜单`Settings > User management > User Directories`新增`openldap`

   - Directory Type：OpenLDAP
   - Hostname：openldap

   - Username：cn=admin,dc=example,dc=org
   - Password：上面管理ldap管理员密码
   - Base DN：dc=example,dc=org
   - Additional User DN：ou=development （ldap开发组）
   - Additional Group DN：ou=jira-groups （ldap jira权限组）
   - LDAP Permissions：Read/Write

3. 点击测试按钮能够检查`ldap`配置是否正确
4. 在`ldap server`列表中点击`synchronise`功能同步`ldap`中用户数据到`jira`中。

## `jira`和`openldap`集成后支持用户修改密码

`openldap`配置里的`LDAP Permissions`从`Read Only`修改为`Read/Write`

用户登陆后在`Profile`里会显示`Change Password`功能，注意：如果`LDAP Permissions`选中`Read Only`用户登陆后`Profile`不会显示`Change Password`功能

