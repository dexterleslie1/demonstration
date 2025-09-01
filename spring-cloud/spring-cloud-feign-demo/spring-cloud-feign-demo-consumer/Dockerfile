#FROM openjdk:8-jdk-slim
FROM openjdk:17-jdk-slim

ADD target/demo-spring-cloud-openfeign-consumer.jar /usr/share/demo-spring-cloud-openfeign-consumer.jar

ENV JAVA_OPTS=""

ENTRYPOINT java ${JAVA_OPTS} -jar /usr/share/demo-spring-cloud-openfeign-consumer.jar --spring.profiles.active=prod
