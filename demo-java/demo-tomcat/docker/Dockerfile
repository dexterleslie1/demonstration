FROM tomcat:9.0.74-jdk8

RUN rm -rf webapps.dist
RUN rm -f conf/tomcat-users.xml
RUN rm -f conf/tomcat-users.xsd

# 使用本地server.xml配置
COPY server.xml /usr/local/tomcat/conf/server.xml

# 使用tomcat用户运行应用
RUN groupadd tomcat
RUN useradd -g tomcat tomcat -s /sbin/nologin

RUN chown -R tomcat:tomcat /usr/local/tomcat
RUN chmod -R g-rwx /usr/local/tomcat
RUN chmod -R o-rwx /usr/local/tomcat

RUN chmod -R u+r /usr/local/tomcat
RUN chmod -R u-w /usr/local/tomcat
RUN chmod -R u+wx /usr/local/tomcat/logs
RUN chmod -R u+wx /usr/local/tomcat/temp
RUN chmod -R u+wx /usr/local/tomcat/work
RUN chmod -R u+wx /usr/local/tomcat/webapps
RUN chmod -R u+x /usr/local/tomcat/bin
RUN chmod -R u+x /usr/local/tomcat/lib
RUN chmod -R u+x /usr/local/tomcat/conf

RUN mkdir -p /usr/local/tomcat/conf/Catalina/localhost
RUN chmod -R u+rx /usr/local/tomcat/conf/Catalina

USER tomcat

