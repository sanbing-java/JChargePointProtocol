/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.domain;

import sanbing.jcpp.proto.gen.ProtocolProto.DownlinkRequestMessage;

/**
 * @author baigod
 */
public record SessionToHandlerMsg(DownlinkRequestMessage downlinkMsg, ProtocolSession session) {
}