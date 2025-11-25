FROM openjdk:8-jdk-slim

ADD target/eureka.jar /usr/share/eureka.jar

ENV JAVA_OPTS=""

ENTRYPOINT java ${JAVA_OPTS} -jar /usr/share/eureka.jar --spring.profiles.active=prod
