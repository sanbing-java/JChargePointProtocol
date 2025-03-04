#
# 开源代码，仅供学习和交流研究使用，商用请联系三丙
# 微信：mohan_88888
# 抖音：程序员三丙
# 付费课程知识星球：https://t.zsxq.com/aKtXo
#


FROM registry.cn-hangzhou.aliyuncs.com/sanbing/mvn:3.9.9-jdk21 AS base
WORKDIR /app
COPY . .
RUN mvn -U -B -T 0.8C clean install -DskipTests \
    && rm -rf /app

