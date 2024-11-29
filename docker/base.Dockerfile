#
# 抖音关注：程序员三丙
# 知识星球：https://t.zsxq.com/j9b21
#

FROM registry.cn-hangzhou.aliyuncs.com/sanbing/mvn:3.9.9-jdk21 AS base
WORKDIR /app
COPY . .
RUN mvn -U -B -T 0.8C clean install -DskipTests \
    && rm -rf /app

