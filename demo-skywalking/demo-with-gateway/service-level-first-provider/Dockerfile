# 注意： 不能修改为8-jdk-slim，否则生成验证码时报错
FROM openjdk:8-jre

COPY ./sources.list /etc/apt/sources.list

RUN apt-get update
RUN apt-get install -y curl

RUN curl --output /usr/share/apache-skywalking-java-agent-9.3.0.tgz https://fut001.oss-cn-hangzhou.aliyuncs.com/skywalking/apache-skywalking-java-agent-9.3.0.tgz
RUN cd /usr/share/ && tar -xzf apache-skywalking-java-agent-9.3.0.tgz

ADD target/service-level-first-provider.jar /usr/share/service-level-first-provider.jar

ENV JAVA_OPTS=""

ENTRYPOINT java -javaagent:/usr/share/skywalking-agent/skywalking-agent.jar ${JAVA_OPTS} -jar /usr/share/service-level-first-provider.jar --spring.profiles.active=prod