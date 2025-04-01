FROM mariadb:10.4.19

RUN apt-get update
RUN apt-get install -y curl

ENV DOCKERIZE_VERSION v0.6.1
RUN curl --silent --output dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz https://fut001.oss-cn-hangzhou.aliyuncs.com/docker/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz
RUN tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz

COPY db.sql /docker-entrypoint-initdb.d/

# https://dba.stackexchange.com/questions/335925/mariadb-11-2-using-too-much-ram
# https://stackoverflow.com/questions/60430448/mysterious-mariadb-10-4-1-ram-ussage
# https://stackoverflow.com/questions/78071133/mariadb-not-releasing-ram-after-jobs-finish
# ENV LD_PRELOAD=/usr/lib/x86_64-linux-gnu/libjemalloc.so.2
