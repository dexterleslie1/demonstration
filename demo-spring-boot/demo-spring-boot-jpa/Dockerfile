FROM mariadb:10.4.19

COPY scripts/db.sql /docker-entrypoint-initdb.d/

COPY my-customize.cnf /etc/mysql/conf.d/my-customize.cnf
