# 介绍

## 什么是`openldap`？

>OpenLDAP是一个自由和开源的实现，针对的是轻型目录访问协议（Lightweight Directory Access Protocol，LDAP）。

## 概念

### 什么是`DC`？

域名组件（Domain Component），例如：ou=development,dc=xxx,dc=com

### 什么是`OU`？

组织单位（Organizational Unit），例如：

- objectClass(organizationUnit) DN:ou=jira-groups,dc=xxx,dc=com（创建jira-groups用于存储jira权限组）
- objectClass(organizationalUnit) DN:ou=development,dc=xxx,dc=com（创建开发组用于存储公司开发团队人员数据）

### 什么是`CN`？

通用名称（Common Name），例如：

- objectClass(inetOrgPerson) DN:cn=admin,ou=development,dc=xxx,dc=com（在开发组下创建用户admin）
- objectClass(groupOfUniqueNames) DN:cn=jira-administrators,ou=jira-groups,dc=xxx,dc=com
  objectClass(groupOfUniqueNames) DN:cn=jira-software-users,ou=jira-groups,dc=xxx,dc=com（在jira-groups组下创建jira-administrators、jira-software-users组）

### 什么是`DN`？

DN（Distinguished Name）是一个用于唯一标识目录树中条目的名称。以下是对DN的详细解释：

1. 定义：
   - DN，全称Distinguished Name，是LDAP（轻型目录访问协议）中用于唯一标识目录树中条目的名称。
2. 结构：
   - DN通常由多个组件组成，这些组件通过逗号（,）分隔。
   - 例如：`cn=John Doe,ou=People,dc=example,dc=com`。在这个例子中，`cn=John Doe`、`ou=People`、`dc=example`和`dc=com`都是DN的组件。
   - 每个组件都由一个属性类型（如cn、ou、dc等）和一个值（如John Doe、People、example、com等）组成。
3. 用途：
   - DN用于在LDAP目录树中精确定位一个条目。
   - 在LDAP操作中，如添加、修改、删除或检索条目时，DN通常被用作条目的唯一标识符。
4. 组件解释：
   - 常见的属性类型包括：
     - cn：通用名称（Common Name）
     - ou：组织单位（Organizational Unit）
     - dc：域名组件（Domain Component）
     - sn：姓氏（Surname）
     - o：组织（Organization）
     - 等等...
   - 这些属性类型及其值共同构成了DN，用于唯一标识LDAP目录树中的条目。
5. 重要性：
   - DN在LDAP操作中起着至关重要的作用，因为它允许用户和系统精确地引用和操作LDAP目录树中的特定条目。
6. 示例：
   - 如果有一个存储用户信息的LDAP目录树，其中每个用户都是一个条目，并且每个条目都有一个唯一的DN，那么可以使用该DN来检索、修改或删除特定的用户条目。

综上所述，DN是OpenLDAP中用于唯一标识目录树中条目的名称，它由多个组件组成，每个组件都由一个属性类型和一个值构成。DN在LDAP操作中起着至关重要的作用，允许用户和系统精确地引用和操作LDAP目录树中的特定条目。

### 什么是`RDN`？

RDN是指LDAP中每一条记录（条目）在其父节点下的唯一名称标识。简单来说，RDN是DN（Distinguished Name，唯一标识名）中最左边、最具体的部分。

DN是LDAP中每个条目在整个目录树中的唯一名称标识。它由多个RDN组成，这些RDN通过逗号分隔，并且每个RDN都由属性类型（如cn、ou、dc等）和对应的属性值组成。例如，在DN `cn=tom,ou=animals,dc=mydomain,dc=org` 中，`cn=tom`、`ou=animals`、`dc=mydomain` 和 `dc=org` 都是RDN。

在LDAP的目录结构中，每个RDN在其父节点下必须是唯一的。这意味着在同一个组织单元（OU）内，不能有两个具有相同RDN的条目。

