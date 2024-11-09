# 注意： 不能修改为8-jdk-slim，否则生成验证码时报错
FROM openjdk:8-jre

COPY ./sources.list /etc/apt/sources.list

RUN apt-get update
RUN apt-get install -y curl

RUN curl --output /usr/share/apache-skywalking-apm-es7-8.7.0.tar.gz https://fut001.oss-cn-hangzhou.aliyuncs.com/skywalking/apache-skywalking-apm-es7-8.7.0.tar.gz
RUN cd /usr/share/ && tar -xzf apache-skywalking-apm-es7-8.7.0.tar.gz

RUN rm -f /usr/share/apache-skywalking-apm-bin-es7/agent/config/agent.config
ADD agent.config /usr/share/apache-skywalking-apm-bin-es7/agent/config/agent.config

ADD target/service-level-second-provider.jar /usr/share/service-level-second-provider.jar

ENV JAVA_OPTS=""

ENTRYPOINT java -javaagent:/usr/share/apache-skywalking-apm-bin-es7/agent/skywalking-agent.jar ${JAVA_OPTS} -jar /usr/share/service-level-second-provider.jar --spring.profiles.active=prod