#!/bin/bash
#
# 开源代码，仅供学习和交流研究使用，商用请联系三丙
# 微信：mohan_88888
# 抖音：程序员三丙
# 付费课程知识星球：https://t.zsxq.com/aKtXo
#


echo "Starting Server ..."

export JAVA_APP_OPTS="-XX:+UseContainerSupport -XX:InitialRAMPercentage=50 -XX:MaxRAMPercentage=90 \
                                 -Xlog:gc*,heap*,age*,safepoint=debug:file=/home/sanbing/logs/gc/gc.log:time,uptime,level,tags:filecount=10,filesize=10M \
                                 -XX:+HeapDumpOnOutOfMemoryError \
                                 -XX:HeapDumpPath=/home/sanbing/logs/heapdump/ \
                                 -XX:+UseTLAB -XX:+ResizeTLAB -XX:+PerfDisableSharedMem -XX:+UseCondCardMark \
                                 -XX:+UseG1GC -XX:MaxGCPauseMillis=500 -XX:+UseStringDeduplication -XX:+ParallelRefProcEnabled -XX:MaxTenuringThreshold=10 \
                                 -Xss512k -XX:G1ReservePercent=20 \
                                 -XX:-OmitStackTraceInFastThrow \
                                 -Dlogging.config=/home/sanbing/config/log4j2.xml"

#export JAVA_OPTS_EXTEND="-Xdebug -Xrunjdwp:transport=dt_socket,address=0.0.0.0:8000,server=y,suspend=n"

exec java $JAVA_APP_OPTS $JAVA_OPTS_EXTEND $JAVA_OPTS -Dnetworkaddress.cache.ttl=60 -jar application.jar
