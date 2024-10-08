/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.cfg.enums;

public enum ForwarderType {

    memory,  // 本地队列模式

    kafka   // Kafka模式 - 发送到外部
}