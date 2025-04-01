# FROM maven:3.3-jdk-8 as builder
FROM openjdk:8-jdk-slim as builder
WORKDIR /usr/src/mymaven
COPY ./.m2/settings.xml /root/.m2/settings.xml
COPY ./maven-package-runnable-jar-main/pom.xml ./pom.xml
COPY ./maven-package-runnable-jar-main/.mvn ./.mvn
COPY ./maven-package-runnable-jar-main/mvnw ./mvnw
# 先下载所有依赖
# RUN mvn package
RUN ./mvnw package -s .mvn/wrapper/settings.xml
# 到这里才复制代码是因为代码变动后导致重新下载 maven 依赖
COPY ./maven-package-runnable-jar-main/src ./src
# RUN mvn package
RUN ./mvnw package -s .mvn/wrapper/settings.xml

FROM openjdk:8-jdk-slim
COPY --from=builder /usr/src/mymaven/target/demo.jar /usr/share/demo.jar
ENV JAVA_OPTS=""
ENTRYPOINT java ${JAVA_OPTS} -jar /usr/share/demo.jar --spring.profiles.active=prod
