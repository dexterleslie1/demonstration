FROM mysql:8.0.18

ADD ./db-storage.sql /docker-entrypoint-initdb.d/db.sql
ADD ./my-customize.cnf /etc/mysql/conf.d/my-customize.cnf
