FROM openjdk:17-jdk-slim

ADD target/eureka-server.jar /usr/share/eureka-server.jar

ENV JAVA_OPTS=""

ENTRYPOINT java ${JAVA_OPTS} -jar /usr/share/eureka-server.jar --spring.profiles.active=prod
