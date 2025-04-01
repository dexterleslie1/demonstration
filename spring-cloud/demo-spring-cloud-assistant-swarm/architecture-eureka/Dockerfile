FROM openjdk:8-jdk-slim

ADD target/demo-microservice-eureka.jar /usr/share/demo-microservice-eureka.jar

ENV JAVA_OPTS=""

ENTRYPOINT java ${JAVA_OPTS} -jar /usr/share/demo-microservice-eureka.jar --spring.profiles.active=prod
