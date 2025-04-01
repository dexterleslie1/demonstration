FROM openjdk:8-jre

COPY sources.list /etc/apt/sources.list

ADD target/demo.jar /usr/share/demo.jar

ENV JAVA_OPTS=""

ENTRYPOINT java ${JAVA_OPTS} -jar /usr/share/demo.jar --spring.profiles.active=prod
