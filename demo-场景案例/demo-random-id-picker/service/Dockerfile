FROM openjdk:17-jdk-slim

ADD target/random-id-picker-service-api.jar /usr/share/random-id-picker-service-api.jar

ENV JAVA_OPTS=""

ENTRYPOINT java ${JAVA_OPTS} -jar /usr/share/random-id-picker-service-api.jar --spring.profiles.active=prod
