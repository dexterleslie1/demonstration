# 注意：很多插件已经不支持此版本
# image: jenkins/jenkins:2.221-centos
# 注意：这是新版本UI并且agent兼容jdk8，随着时间推移很多插件都在升级可能不兼容旧版jenkins，所以jenkins镜像也需要定期升级
FROM jenkins/jenkins:2.492.1

# 自动化设置jenkins
# https://www.digitalocean.com/community/tutorials/how-to-automate-jenkins-setup-with-docker-and-jenkins-configuration-as-code
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false

# 自动安装plugin
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt

USER root

RUN apt-get update && \
    apt-get install -y sshpass

USER jenkins
