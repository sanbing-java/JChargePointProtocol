/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.domain;

import sanbing.jcpp.proto.gen.ProtocolProto.DownlinkRestMessage;

/**
 * @author baigod
 */
public record SessionToHandlerMsg(DownlinkRestMessage downlinkMsg, ProtocolSession session) {
}