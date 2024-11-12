FROM openjdk:8-jdk-slim

RUN apt-get update
RUN apt-get install -y curl

RUN curl --output /usr/share/apache-skywalking-java-agent-9.3.0.tgz https://fut001.oss-cn-hangzhou.aliyuncs.com/skywalking/apache-skywalking-java-agent-9.3.0.tgz
RUN cd /usr/share/ && tar -xzf apache-skywalking-java-agent-9.3.0.tgz

# springcloud-gateway需要复制以下optional插件到plugins目录，否则gateway无法进行链路追踪
RUN cp /usr/share/skywalking-agent/optional-plugins/apm-spring-webflux-5.x-plugin-9.3.0.jar /usr/share/skywalking-agent/plugins/apm-spring-webflux-5.x-plugin-9.3.0.jar
RUN cp /usr/share/skywalking-agent/optional-plugins/apm-spring-cloud-gateway-3.x-plugin-9.3.0.jar /usr/share/skywalking-agent/plugins/apm-spring-cloud-gateway-3.x-plugin-9.3.0.jar

ADD target/demo-microservice-gateway.jar /usr/share/demo-microservice-gateway.jar

ENV JAVA_OPTS=""

ENTRYPOINT java -javaagent:/usr/share/skywalking-agent/skywalking-agent.jar ${JAVA_OPTS} -jar /usr/share/demo-microservice-gateway.jar --spring.profiles.active=prod
