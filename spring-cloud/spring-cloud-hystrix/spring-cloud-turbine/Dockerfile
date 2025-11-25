FROM openjdk:8-jdk-slim

ADD target/turbine.jar /usr/share/turbine.jar

ENV JAVA_OPTS=""

ENTRYPOINT java ${JAVA_OPTS} -jar /usr/share/turbine.jar --spring.profiles.active=prod
