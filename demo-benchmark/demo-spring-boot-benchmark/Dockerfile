#FROM openjdk:8-jdk-slim
FROM openjdk:17-jdk-slim

ADD target/demo.jar /usr/share/demo.jar

ENV JAVA_OPTS=""

ENTRYPOINT java ${JAVA_OPTS} -jar /usr/share/demo.jar --spring.profiles.active=prod
