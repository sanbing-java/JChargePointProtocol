/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.domain;

import java.util.UUID;

public record ListenerToHandlerMsg(UUID id, byte[] msg, ProtocolSession session) {

}
