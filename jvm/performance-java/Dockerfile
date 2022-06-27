FROM openjdk:8-jre

ARG jarFile
ADD target/${jarFile} /usr/share/demo-java-performance.jar

ENV JAVA_OPTS=""

ENTRYPOINT java ${JAVA_OPTS} -jar /usr/share/demo-java-performance.jar