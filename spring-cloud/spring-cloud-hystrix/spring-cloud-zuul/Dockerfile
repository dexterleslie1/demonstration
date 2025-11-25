FROM openjdk:8-jdk-slim

ADD target/zuul.jar /usr/share/zuul.jar

ENV JAVA_OPTS=""

ENTRYPOINT java ${JAVA_OPTS} -jar /usr/share/zuul.jar --spring.profiles.active=prod
