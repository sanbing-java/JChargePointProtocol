/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
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
public abstract class YunKuaiChongUplinkCmdExe extends AbstractYunKuaiChongCmdExe{

    public abstract void execute(TcpSession tcpSession, YunKuaiChongUplinkMessage yunKuaiChongUplinkMessage, ProtocolContext ctx);

    protected static UplinkQueueMessage.Builder uplinkMessageBuilder(String messageKey, TcpSession tcpSession, YunKuaiChongUplinkMessage yunKuaiChongUplinkMessage) {
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