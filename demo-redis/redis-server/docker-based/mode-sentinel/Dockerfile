FROM redis:5.0.14-bullseye

COPY ./sources.list /etc/apt/sources.list

RUN apt-get update
RUN apt-get install -y curl

ENV DOCKERIZE_VERSION v0.6.1
RUN curl --silent --output dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz https://fut001.oss-cn-hangzhou.aliyuncs.com/docker/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz
RUN tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz

COPY ./redis.conf /usr/local/etc/redis/redis.conf

RUN chown -R redis:redis /usr/local/etc/redis/