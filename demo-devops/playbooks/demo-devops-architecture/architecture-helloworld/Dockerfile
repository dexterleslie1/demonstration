# 注意： 不能修改为8-jdk-slim，否则生成验证码时报错
FROM openjdk:8-jre

COPY ./sources.list /etc/apt/sources.list

RUN apt-get update
RUN apt-get install -y curl

RUN curl --output /usr/share/apache-skywalking-apm-es7-8.7.0.tar.gz https://bucketxyh.oss-cn-hongkong.aliyuncs.com/skywalking/apache-skywalking-apm-es7-8.7.0.tar.gz
RUN cd /usr/share/ && tar -xzf apache-skywalking-apm-es7-8.7.0.tar.gz
RUN rm -f /usr/share/apache-skywalking-apm-es7-8.7.0.tar.gz

ADD target/architecture-helloworld.jar /usr/share/architecture-helloworld.jar

ENV JAVA_OPTS=""

ENTRYPOINT java ${JAVA_OPTS} -jar /usr/share/architecture-helloworld.jar --spring.profiles.active=prod