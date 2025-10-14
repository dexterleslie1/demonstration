FROM openjdk:17-jdk-slim

ADD target/service-storage.jar /usr/share/service-storage.jar

ENV JAVA_OPTS=""

ENTRYPOINT java ${JAVA_OPTS} -jar /usr/share/service-storage.jar --spring.profiles.active=prod
