FROM logstash:7.17.0

RUN mkdir /usr/share/logstash/data-logstash

RUN rm -f /usr/share/logstash/pipeline/logstash.conf

ADD logstash.conf /usr/share/logstash/pipeline/logstash.conf

ADD mysql-connector-java-8.0.23.jar /usr/share/logstash/mysql-connector-java-8.0.23.jar