/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.yunkuaichong.commoncmd;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import sanbing.jcpp.infrastructure.util.codec.BCDUtil;
import sanbing.jcpp.infrastructure.util.jackson.JacksonUtil;
import sanbing.jcpp.infrastructure.util.trace.TracerContextUtil;
import sanbing.jcpp.proto.gen.ProtocolProto.RemoteStopChargingResponse;
import sanbing.jcpp.proto.gen.ProtocolProto.UplinkQueueMessage;
import sanbing.jcpp.protocol.ProtocolContext;
import sanbing.jcpp.protocol.listener.tcp.TcpSession;
import sanbing.jcpp.protocol.yunkuaichong.YunKuaiChongUplinkCmdExe;
import sanbing.jcpp.protocol.yunkuaichong.YunKuaiChongUplinkMessage;
import sanbing.jcpp.protocol.yunkuaichong.annotation.YunKuaiChongCmd;

/**
 * 云快充1.5.0 远程停机命令回复
 *
 * @author baigod
 */
@Slf4j
@YunKuaiChongCmd(0x35)
public class YunKuaiChongV150RemoteStopResultULCmd extends YunKuaiChongUplinkCmdExe {
    @Override
    public void execute(TcpSession tcpSession, YunKuaiChongUplinkMessage yunKuaiChongUplinkMessage, ProtocolContext ctx) {
        log.info("{} 云快充1.5.0远程停机命令回复", tcpSession);
        ByteBuf byteBuf = Unpooled.copiedBuffer(yunKuaiChongUplinkMessage.getMsgBody());

        // 从Tracer总获取当前时间
        long ts = TracerContextUtil.getCurrentTracer().getTracerTs();

        ObjectNode additionalInfo = JacksonUtil.newObjectNode();

        // 1.桩编号
        byte[] pileCodeBytes = new byte[7];
        byteBuf.readBytes(pileCodeBytes);
        String pileCode = BCDUtil.toString(pileCodeBytes);

        // 2.抢号
        byte gunCodeByte = byteBuf.readByte();
        String gunCode = BCDUtil.toString(gunCodeByte);

        // 3.命令执行结果 0x00失败 0x01成功
        boolean isSuccess = (byteBuf.readByte() == 0x01);

        // 4.失败原因 0无 1设备编码不匹配 2枪已在充电 3设备故障 4设备离线 5未插枪
        byte failReasonByte = byteBuf.readByte();
        String failReason = mapFailCode(failReasonByte);

        RemoteStopChargingResponse remoteStopChargingResponse = RemoteStopChargingResponse.newBuilder()
                .setTs(ts)
                .setPileCode(pileCode)
                .setGunCode(gunCode)
                .setSuccess(isSuccess)
                .setFailReason(failReason)
                .setAdditionalInfo(additionalInfo.toString())
                .build();

        // 转发到后端
        UplinkQueueMessage uplinkQueueMessage = uplinkMessageBuilder(pileCode, tcpSession, yunKuaiChongUplinkMessage)
                .setRemoteStopChargingResponse(remoteStopChargingResponse)
                .build();

        tcpSession.getForwarder().sendMessage(uplinkQueueMessage);
    }

    public static String mapFailCode(byte failCode) {
        return switch (failCode) {
            case 0x00 -> "无";
            case 0x01 -> "设备编号不匹配";
            case 0x02 -> "枪未处于充电状态";
            case 0x03 -> "其他";
            default -> "未知错误"; // 可以根据需求自定义
        };
    }
}