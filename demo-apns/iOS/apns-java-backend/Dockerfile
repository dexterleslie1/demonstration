FROM openjdk:8-jdk-slim

ARG jarFile
ADD target/${jarFile} /usr/share/demo-apns-pushy/demo-apns-pushy.jar

ENV JAVA_OPTS=""

ENTRYPOINT java ${JAVA_OPTS} -jar /usr/share/demo-apns-pushy/demo-apns-pushy.jar --spring.profiles.active=prod