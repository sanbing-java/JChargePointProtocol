/**
 * 开源代码，仅供学习和交流研究使用，商用请联系三丙
 * 微信：mohan_88888
 * 抖音：程序员三丙
 * 付费课程知识星球：https://t.zsxq.com/aKtXo
 */
package sanbing.jcpp.protocol.yunkuaichong;

import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import sanbing.jcpp.infrastructure.util.jackson.JacksonUtil;
import sanbing.jcpp.proto.gen.ProtocolProto.UplinkQueueMessage;
import sanbing.jcpp.protocol.ProtocolContext;
import sanbing.jcpp.protocol.listener.tcp.TcpSession;

/**
 * @author baigod
 */
@Slf4j
public abstract class YunKuaiChongUplinkCmdExe extends AbstractYunKuaiChongCmdExe {

    public abstract void execute(TcpSession tcpSession, YunKuaiChongUplinkMessage yunKuaiChongUplinkMessage, ProtocolContext ctx);

    protected UplinkQueueMessage.Builder uplinkMessageBuilder(String messageKey, TcpSession tcpSession, YunKuaiChongUplinkMessage yunKuaiChongUplinkMessage) {
        return UplinkQueueMessage.newBuilder()
                .setMessageIdMSB(yunKuaiChongUplinkMessage.getId().getMostSignificantBits())
                .setMessageIdLSB(yunKuaiChongUplinkMessage.getId().getLeastSignificantBits())
                .setSessionIdMSB(tcpSession.getId().getMostSignificantBits())
                .setSessionIdLSB(tcpSession.getId().getLeastSignificantBits())
                .setRequestData(ByteString.copyFrom(JacksonUtil.writeValueAsBytes(yunKuaiChongUplinkMessage)))
                .setMessageKey(messageKey)
                .setProtocolName(tcpSession.getProtocolName());
    }

}