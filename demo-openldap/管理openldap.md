# 管理`openldap`

## 使用`phpldapadmin`管理`openldap`

### 登录`phpldapadmin`

访问`http://192.168.1.181:18080/`

- **Login DN**：cn=admin,dc=example,dc=org
- **Password**：xxx

### 创建`organizationalUnit`

1. `OU`的`DN`：objectClass(organizationalUnit) DN:ou=development,dc=example,dc=org
2. 选中`phpldapadmin`导航中的`dc=example,dc=org`
3. 点击`Create a child entry`，选择`Generic: Organisational Unit`
   - Organisational Unit：developement
4. 点击`Create Object`创建`OU`
5. 查看将要创建的`OU`信息，点击`Commit`创建

### 创建`inetOrgPerson`

1. `inetOrgPerson`的`DN`：objectClass(inetOrgPerson) DN:cn=admin,ou=development,dc=example,dc=org
2. 选中`phpldapadmin`导航中的`ou=development`
3. 点击`Create a child entry`后选择`Default`
4. 点击`Proceed>>`
   - ObjectClasses：inetOrgPerson
5. 填写`inetOrgPerson`信息，点击`Create Object`创建`inetOrgPerson`
   - RDN Attribute：cn（cn）
   - cn：admin
   - sn：admin
6. 查看将要创建的`inetOrgPerson`信息，点击`Commit`创建

### 创建`groupOfUniqueNames`

1. `groupOfUniqueNames`的`DN`：objectClass(groupOfUniqueNames) DN:cn=jira-administrators,ou=jira-groups,dc=example,dc=org
2. 创建`groupOfUniqueNames`步骤参考上面创建`inetOrgPerson`步骤

