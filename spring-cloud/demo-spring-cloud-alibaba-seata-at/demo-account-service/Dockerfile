FROM openjdk:17-jdk-slim

ADD target/service-account.jar /usr/share/service-account.jar

ENV JAVA_OPTS=""

ENTRYPOINT java ${JAVA_OPTS} -jar /usr/share/service-account.jar --spring.profiles.active=prod
