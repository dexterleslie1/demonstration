FROM mariadb:10.4.19

ADD db.sql /docker-entrypoint-initdb.d/db.sql
ADD my-customize.cnf /etc/mysql/conf.d/my-customize.cnf
