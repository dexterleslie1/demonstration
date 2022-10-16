FROM openjdk:8-jdk-slim

ARG jarFile
ADD target/${jarFile} /usr/share/demo-springboot-performance.jar

ENV JAVA_OPTS=""

ENTRYPOINT java ${JAVA_OPTS} -jar /usr/share/demo-springboot-performance.jar
