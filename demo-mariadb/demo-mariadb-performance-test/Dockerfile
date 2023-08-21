FROM mariadb:10.4.19

RUN apt-get update
RUN apt-get install -y curl

ENV DOCKERIZE_VERSION v0.6.1
RUN curl --silent --output dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz https://bucketxyh.oss-cn-hongkong.aliyuncs.com/docker/dockerize-linux-amd64-v0.6.1.tar.gz
RUN tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz

COPY db.sql /docker-entrypoint-initdb.d/
COPY my-customize.cnf /etc/mysql/conf.d/my-customize.cnf
