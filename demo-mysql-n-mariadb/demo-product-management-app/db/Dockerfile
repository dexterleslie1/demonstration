#FROM mariadb:10.4
# 不能删除下面注释的配置，用于协助数据库基准测试
FROM mariadb:11.4
#FROM mysql:5.7
#FROM mysql:8.0.18

ADD db.sql /docker-entrypoint-initdb.d/db.sql
ADD my-customize.cnf /etc/mysql/conf.d/my-customize.cnf
