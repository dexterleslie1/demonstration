FROM openjdk:17-jdk-slim

ADD target/service-order.jar /usr/share/service-order.jar

ENV JAVA_OPTS=""

ENTRYPOINT java ${JAVA_OPTS} -jar /usr/share/service-order.jar --spring.profiles.active=prod
