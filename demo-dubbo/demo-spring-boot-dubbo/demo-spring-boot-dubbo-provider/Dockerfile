FROM openjdk:8-jdk-slim
#FROM openjdk:17-jdk-slim

ADD target/demo-spring-boot-dubbo-provider.jar /usr/share/demo-spring-boot-dubbo-provider.jar

ENV JAVA_OPTS=""

ENTRYPOINT java ${JAVA_OPTS} -jar /usr/share/demo-spring-boot-dubbo-provider.jar --spring.profiles.active=prod
