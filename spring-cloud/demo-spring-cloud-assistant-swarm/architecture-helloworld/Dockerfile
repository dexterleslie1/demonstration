FROM openjdk:8-jdk-slim

COPY ./sources.list /etc/apt/sources.list

ADD target/demo-microservice-helloworld.jar /usr/share/demo-microservice-helloworld.jar

ENV JAVA_OPTS=""

ENTRYPOINT java ${JAVA_OPTS} -jar /usr/share/demo-microservice-helloworld.jar --spring.profiles.active=prod
