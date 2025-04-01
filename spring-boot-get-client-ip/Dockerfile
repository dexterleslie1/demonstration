FROM openjdk:8-jre

#COPY ./sources.list /etc/apt/sources.list
#
#RUN apt-get update
#RUN apt-get install -y curl
#
#RUN curl --output /usr/share/apache-skywalking-apm-es7-8.7.0.tar.gz https://bucketxyh.oss-cn-hongkong.aliyuncs.com/skywalking/apache-skywalking-apm-es7-8.7.0.tar.gz
#RUN cd /usr/share/ && tar -xzf apache-skywalking-apm-es7-8.7.0.tar.gz
#RUN rm -f /usr/share/apache-skywalking-apm-es7-8.7.0.tar.gz

ADD target/demo.jar /usr/share/demo.jar

ENV JAVA_OPTS=""

ENTRYPOINT java ${JAVA_OPTS} -jar /usr/share/demo.jar --spring.profiles.active=prod