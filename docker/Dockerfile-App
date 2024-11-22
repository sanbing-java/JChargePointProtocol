#
# 抖音关注：程序员三丙
# 知识星球：https://t.zsxq.com/j9b21
#

FROM registry.cn-hangzhou.aliyuncs.com/sanbing/jcpp-base:latest AS base
WORKDIR /app
COPY . .
RUN mvn -U -B -T 0.8C clean install -DskipTests

#分层
FROM registry.cn-hangzhou.aliyuncs.com/sanbing/openjdk:21-jdk-slim-bullseye AS builder
WORKDIR /app
COPY --from=base /app/jcpp-app-bootstrap/target/application.jar application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

# 执行
FROM registry.cn-hangzhou.aliyuncs.com/sanbing/openjdk:21-jdk-slim-bullseye
RUN useradd -m sanbing
WORKDIR /home/sanbing

COPY --from=builder /app/extracted/dependencies/ ./
COPY --from=builder /app/extracted/spring-boot-loader/ ./
COPY --from=builder /app/extracted/snapshot-dependencies/ ./
COPY --from=builder /app/extracted/application/ ./
COPY --from=base /app/jcpp-app-bootstrap/target/conf ./config
COPY --from=base /app/docker/start.sh .

RUN chmod a+x start.sh && \
    mkdir -p /home/sanbing/logs/jcpp &&  \
    mkdir -p /home/sanbing/logs/accesslog &&  \
    mkdir -p /home/sanbing/logs/gc &&  \
    mkdir -p /home/sanbing/logs/heapdump &&  \
    chmod 700 -R /home/sanbing/logs/* &&  \
    chown -R sanbing:sanbing /home/sanbing

EXPOSE 8080 8080

ENV APP_LOG_LEVEL=INFO
ENV PROTOCOLS_LOG_LEVEL=INFO

USER sanbing
CMD ["/bin/bash", "start.sh"]
