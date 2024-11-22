/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.yunkuaichong.v150.cmd;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import sanbing.jcpp.infrastructure.util.codec.BCDUtil;
import sanbing.jcpp.proto.gen.ProtocolProto.SetPricingResponse;
import sanbing.jcpp.proto.gen.ProtocolProto.UplinkQueueMessage;
import sanbing.jcpp.protocol.ProtocolContext;
import sanbing.jcpp.protocol.listener.tcp.TcpSession;
import sanbing.jcpp.protocol.yunkuaichong.YunKuaiChongUplinkCmdExe;
import sanbing.jcpp.protocol.yunkuaichong.YunKuaiChongUplinkMessage;
import sanbing.jcpp.protocol.yunkuaichong.annotation.YunKuaiChongCmd;

import static sanbing.jcpp.protocol.yunkuaichong.enums.YunKuaiChongDownlinkCmdEnum.QUERY_PRICING_ACK;

/**
 * 云快充1.5.0 计费模型应答
 *
 * @author baigod
 */
@Slf4j
@YunKuaiChongCmd(0x57)
public class YunKuaiChongV150SetPricingModelAckULCmd extends YunKuaiChongUplinkCmdExe {
    @Override
    public void execute(TcpSession tcpSession, YunKuaiChongUplinkMessage yunKuaiChongUplinkMessage, ProtocolContext ctx) {
        log.info("{} 云快充1.5.0计费模型应答", tcpSession);
        ByteBuf byteBuf = Unpooled.copiedBuffer(yunKuaiChongUplinkMessage.getMsgBody());

        // 1.桩编号
        byte[] pileCodeBytes = new byte[7];
        byteBuf.readBytes(pileCodeBytes);
        String pileCode = BCDUtil.toString(pileCodeBytes);

        // 2.设置结果 0x00:失败 0x01:成功
        boolean isSuccess = (byteBuf.readByte() == 0x01);

        // 从缓存取上个请求的pricingId
        Object pricingId = tcpSession.getRequestCache().asMap().getOrDefault(pileCode + QUERY_PRICING_ACK.getCmd(), null);

        if (pricingId instanceof Long pricingIdL) {
            // 转发到后端
            SetPricingResponse setPricingResponse = SetPricingResponse.newBuilder()
                    .setPileCode(pileCode)
                    .setSuccess(isSuccess)
                    .setPricingId(pricingIdL)
                    .build();
            UplinkQueueMessage uplinkQueueMessage = uplinkMessageBuilder(setPricingResponse.getPileCode(), tcpSession, yunKuaiChongUplinkMessage)
                    .setSetPricingResponse(setPricingResponse)
                    .build();
            tcpSession.getForwarder().sendMessage(uplinkQueueMessage);
        }


    }
}