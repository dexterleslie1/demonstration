FROM openjdk:8-jdk-slim

ADD target/demo-microservice-gateway.jar /usr/share/demo-microservice-gateway.jar

ENV JAVA_OPTS=""

ENTRYPOINT java ${JAVA_OPTS} -jar /usr/share/demo-microservice-gateway.jar --spring.profiles.active=prod
