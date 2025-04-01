FROM openjdk:8-jdk-slim

ARG jarFile
ADD target/${jarFile} /usr/share/demo-springboot-performance.jar

ENV JAVA_OPTS=""

ENTRYPOINT java ${JAVA_OPTS} -jar \
    -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps \
    -Xloggc:./gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 \
    -XX:GCLogFileSize=128m -XX:+HeapDumpOnOutOfMemoryError \
    -XX:+CrashOnOutOfMemoryError -XX:HeapDumpPath=./ \
#    -Dcom.sun.management.jmxremote \
#    -Dcom.sun.management.jmxremote.port=9010 \
#    -Dcom.sun.management.jmxremote.local.only=false \
#    -Dcom.sun.management.jmxremote.authenticate=false \
#    -Dcom.sun.management.jmxremote.ssl=false \
    /usr/share/demo-springboot-performance.jar
