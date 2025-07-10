FROM openjdk:17-jdk-slim

ADD target/flash-sale-service-crond.jar /usr/share/flash-sale-service-crond.jar

ENV JAVA_OPTS=""

ENTRYPOINT java ${JAVA_OPTS} -jar /usr/share/flash-sale-service-crond.jar --spring.profiles.active=prod