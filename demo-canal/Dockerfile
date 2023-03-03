FROM mysql:5.7

COPY sources.list /etc/apt/sources.list

RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 467B942D3A79BD29

RUN apt-get update
RUN apt-get install -y curl

ENV DOCKERIZE_VERSION v0.6.1
RUN curl --silent --output dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz https://bucketxyh.oss-cn-hongkong.aliyuncs.com/docker/dockerize-linux-amd64-v0.6.1.tar.gz
RUN tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz