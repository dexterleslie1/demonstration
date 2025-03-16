FROM openjdk:8-jre

ADD target/demo.jar /usr/share/demo.jar

ENV JAVA_OPTS=""

ENTRYPOINT java ${JAVA_OPTS} -jar /usr/share/demo.jar --spring.profiles.active=prod
