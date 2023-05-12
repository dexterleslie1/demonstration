FROM tomcat:9.0.74-jdk8

RUN rm -rf webapps.dist
RUN rm -f /conf/tomcat-users.xml
RUN rm -f /conf/tomcat-users.xsd

# 使用本地server.xml配置
COPY server.xml /usr/local/tomcat/conf/server.xml