FROM mariadb:11.4.8
#FROM mysql:8.0.18

ADD db.sql /docker-entrypoint-initdb.d/db.sql
ADD my-customize.cnf /etc/mysql/conf.d/my-customize.cnf
