FROM openjdk:8-jdk-slim

COPY ./sources.list /etc/apt/sources.list

RUN apt-get update
RUN apt-get install -y curl

RUN curl --output /usr/share/apache-skywalking-java-agent-9.3.0.tgz https://fut001.oss-cn-hangzhou.aliyuncs.com/skywalking/apache-skywalking-java-agent-9.3.0.tgz
RUN cd /usr/share/ && tar -xzf apache-skywalking-java-agent-9.3.0.tgz

ADD target/demo-microservice-helloworld.jar /usr/share/demo-microservice-helloworld.jar

ENV JAVA_OPTS=""

ENTRYPOINT java -javaagent:/usr/share/skywalking-agent/skywalking-agent.jar ${JAVA_OPTS} -jar /usr/share/demo-microservice-helloworld.jar --spring.profiles.active=prod
