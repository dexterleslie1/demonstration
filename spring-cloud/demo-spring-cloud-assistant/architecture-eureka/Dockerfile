FROM openjdk:8-jdk-slim

RUN apt-get update
RUN apt-get install -y curl

RUN curl --output /usr/share/apache-skywalking-java-agent-9.3.0.tgz https://fut001.oss-cn-hangzhou.aliyuncs.com/skywalking/apache-skywalking-java-agent-9.3.0.tgz
RUN cd /usr/share/ && tar -xzf apache-skywalking-java-agent-9.3.0.tgz

ADD target/demo-microservice-eureka.jar /usr/share/demo-microservice-eureka.jar

ENV JAVA_OPTS=""

ENTRYPOINT java -javaagent:/usr/share/skywalking-agent/skywalking-agent.jar ${JAVA_OPTS} -jar /usr/share/demo-microservice-eureka.jar --spring.profiles.active=prod
