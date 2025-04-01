FROM library/rabbitmq:3.7.16-rc.3-management

RUN apt-get update
RUN apt-get install curl -y
RUN curl --silent --output rabbitmq_delayed_message_exchange-20171201-3.7.x.ez https://bucketxy.oss-cn-hangzhou.aliyuncs.com/rabbitmq-rpms/rabbitmq_delayed_message_exchange-20171201-3.7.x.ez

#将队列延迟插件拷贝到rabbitmq插件目录
RUN cp /rabbitmq_delayed_message_exchange-20171201-3.7.x.ez /opt/rabbitmq/plugins/
#安装常用插件
RUN /opt/rabbitmq/sbin/rabbitmq-plugins enable --offline rabbitmq_management rabbitmq_delayed_message_exchange