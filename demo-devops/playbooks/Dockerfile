# NOTE: 很多插件已经不支持此版本
# image: jenkins/jenkins:2.221-centos
# NOTE: 这是新版本UI并且agent兼容jdk8，随着时间推移很多插件都在升级可能不兼容旧版jenkins，所以jenkins镜像也需要定期升级
FROM jenkins/jenkins:2.346.3

# 自动化设置jenkins
# https://www.digitalocean.com/community/tutorials/how-to-automate-jenkins-setup-with-docker-and-jenkins-configuration-as-code
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false

# 自动安装plugin
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt

# 使用configuration-as-code插件自动配置jenkins
COPY casc.yaml /usr/share/jenkins/ref/casc.yaml
ENV CASC_JENKINS_CONFIG /usr/share/jenkins/ref/casc.yaml

