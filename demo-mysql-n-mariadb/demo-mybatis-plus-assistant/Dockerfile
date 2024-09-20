#FROM mysql:8.0.21
#FROM mysql:5.7
FROM mariadb:10.4.19

COPY init.sql /docker-entrypoint-initdb.d/

# ENV LD_PRELOAD=/usr/lib/x86_64-linux-gnu/libjemalloc.so.2