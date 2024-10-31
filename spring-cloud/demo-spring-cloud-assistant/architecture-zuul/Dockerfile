FROM openjdk:8-jdk-slim

ADD target/demo-microservice-zuul.jar /usr/share/demo-microservice-zuul.jar

ENV JAVA_OPTS=""

ENTRYPOINT java ${JAVA_OPTS} -jar /usr/share/demo-microservice-zuul.jar --spring.profiles.active=prod
