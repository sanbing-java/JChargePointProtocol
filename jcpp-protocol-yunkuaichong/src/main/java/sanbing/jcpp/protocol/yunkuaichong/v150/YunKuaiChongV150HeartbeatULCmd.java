/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.yunkuaichong.v150;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import sanbing.jcpp.infrastructure.util.codec.BCDUtil;
import sanbing.jcpp.infrastructure.util.jackson.JacksonUtil;
import sanbing.jcpp.proto.gen.ProtocolProto.HeartBeatRequest;
import sanbing.jcpp.proto.gen.ProtocolProto.UplinkQueueMessage;
import sanbing.jcpp.protocol.ProtocolContext;
import sanbing.jcpp.protocol.listener.tcp.TcpSession;
import sanbing.jcpp.protocol.yunkuaichong.YunKuaiChongUplinkCmdExe;
import sanbing.jcpp.protocol.yunkuaichong.YunKuaiChongUplinkMessage;
import sanbing.jcpp.protocol.yunkuaichong.annotation.YunKuaiChongCmd;

import static sanbing.jcpp.protocol.yunkuaichong.enums.YunKuaiChongDownlinkCmdEnum.HEARTBEAT;

/**
 * 云快充1.5.0 充电桩心跳包
 *
 * @author baigod
 */
@Slf4j
@YunKuaiChongCmd(0x03)
public class YunKuaiChongV150HeartbeatULCmd extends YunKuaiChongUplinkCmdExe {
    @Override
    public void execute(TcpSession tcpSession, YunKuaiChongUplinkMessage yunKuaiChongUplinkMessage, ProtocolContext ctx) {
        log.debug("{} 云快充1.5.0充电桩心跳包", tcpSession);
        ByteBuf byteBuf = Unpooled.copiedBuffer(yunKuaiChongUplinkMessage.getMsgBody());

        ObjectNode additionalInfo = JacksonUtil.newObjectNode();

        byte[] pileCodeBytes = new byte[7];
        byteBuf.readBytes(pileCodeBytes);
        String pileCode = BCDUtil.toString(pileCodeBytes);

        byte gunCodeByte = byteBuf.readByte();
        int gunCode = Integer.parseInt(BCDUtil.toString(gunCodeByte));
        additionalInfo.put("枪号", gunCode);

        int gunState = byteBuf.readUnsignedByte();
        additionalInfo.put("枪状态(0正常 1故障)", gunState);

        // 刷新前置会话
        ctx.getProtocolSessionRegistryProvider().activate(tcpSession);

        // 转发到后端
        HeartBeatRequest heartBeatRequest = HeartBeatRequest.newBuilder()
                .setPileCode(pileCode)
                .setRemoteAddress(tcpSession.getAddress().toString())
                .setNodeId(ctx.getServiceInfoProvider().getServiceId())
                .setNodeWebapiIpPort(ctx.getServiceInfoProvider().getServiceWebapiEndpoint())
                .setAdditionalInfo(additionalInfo.toString())
                .build();
        UplinkQueueMessage uplinkQueueMessage = uplinkMessageBuilder(heartBeatRequest.getPileCode(), tcpSession, yunKuaiChongUplinkMessage)
                .setHeartBeatRequest(heartBeatRequest)
                .build();
        tcpSession.getForwarder().sendMessage(uplinkQueueMessage);

        pingAck(tcpSession, pileCodeBytes, gunCodeByte);
    }

    private void pingAck(TcpSession tcpSession, byte[] pileCodeBytes, byte gunCodeByte) {
        ByteBuf pingAckMsgBody = Unpooled.buffer(9);
        pingAckMsgBody.writeBytes(pileCodeBytes);
        pingAckMsgBody.writeByte(gunCodeByte);
        pingAckMsgBody.writeByte(0);

        encodeAndWriteFlush(HEARTBEAT,
                pingAckMsgBody,
                tcpSession);
    }
}