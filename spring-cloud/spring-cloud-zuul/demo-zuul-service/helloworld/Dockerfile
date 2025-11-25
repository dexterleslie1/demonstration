FROM openjdk:8-jdk-slim

ADD target/helloworld.jar /usr/share/helloworld.jar

ENV JAVA_OPTS=""

ENTRYPOINT java ${JAVA_OPTS} -jar /usr/share/helloworld.jar --spring.profiles.active=prod
