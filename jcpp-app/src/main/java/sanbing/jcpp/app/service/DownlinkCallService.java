/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.app.service;

import sanbing.jcpp.proto.gen.ProtocolProto.DownlinkRestMessage;

/**
 * @author baigod
 */
public interface DownlinkCallService {

    void sendDownlinkMessage(DownlinkRestMessage.Builder downlinkMessageBuilder, String pileCode);
}