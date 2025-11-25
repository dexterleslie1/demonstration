FROM openjdk:8-jdk-slim

ADD target/user.jar /usr/share/user.jar

ENV JAVA_OPTS=""

ENTRYPOINT java ${JAVA_OPTS} -jar /usr/share/user.jar --spring.profiles.active=prod
