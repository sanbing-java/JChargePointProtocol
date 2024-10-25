/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.yunkuaichong.v160.cmd;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import sanbing.jcpp.infrastructure.util.codec.BCDUtil;
import sanbing.jcpp.proto.gen.ProtocolProto;
import sanbing.jcpp.protocol.ProtocolContext;
import sanbing.jcpp.protocol.listener.tcp.TcpSession;
import sanbing.jcpp.protocol.yunkuaichong.YunKuaiChongDownlinkCmdExe;
import sanbing.jcpp.protocol.yunkuaichong.YunKuaiChongDwonlinkMessage;
import sanbing.jcpp.protocol.yunkuaichong.annotation.YunKuaiChongCmd;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static sanbing.jcpp.protocol.yunkuaichong.enums.YunKuaiChongDownlinkCmdEnum.REMOTE_START_CHARGING;

/**
 * 云快充1.6.0 运营平台远程控制并充启机
 *
 * @author baigod
 */
@Slf4j
@YunKuaiChongCmd(0xA4)
public class YunKuaiChongV160RemoteParallelStartDLCmd extends YunKuaiChongDownlinkCmdExe {

    static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Override
    public void execute(TcpSession tcpSession, YunKuaiChongDwonlinkMessage yunKuaiChongDwonlinkMessage, ProtocolContext ctx) {
        log.info("{} 云快充1.6.0运营平台远程控制并充启机", tcpSession);

        if (!yunKuaiChongDwonlinkMessage.getMsg().hasRemoteStartChargingRequest()) {
            return;
        }

        ProtocolProto.RemoteStartChargingRequest remoteStartChargingRequest = yunKuaiChongDwonlinkMessage.getMsg().getRemoteStartChargingRequest();
        String pileCode = remoteStartChargingRequest.getPileCode();
        String gunCode = remoteStartChargingRequest.getGunCode();
        String tradeNo = remoteStartChargingRequest.getTradeNo();
        String limitYuan = remoteStartChargingRequest.getLimitYuan();

        byte[] cardNo = encodeCardNo(tradeNo);

        ByteBuf msgBody = Unpooled.buffer(44);
        // 交易流水号
        msgBody.writeBytes(encodeTradeNo(tradeNo));
        // 桩编码
        msgBody.writeBytes(encodePileCode(pileCode));
        // 枪号
        msgBody.writeBytes(encodeGunCode(gunCode));
        // 逻辑卡号 BCD码
        msgBody.writeBytes(cardNo);
        // 物理卡号
        msgBody.writeBytes(cardNo);
        // 账户余额
        msgBody.writeIntLE(new BigDecimal(limitYuan).intValue());
        // 并充序号
        msgBody.writeBytes(BCDUtil.toBytes(LocalDateTime.now().format(dateTimeFormatter)));

        encodeAndWriteFlush(REMOTE_START_CHARGING,
                msgBody,
                tcpSession);
    }

    /**
     * 用交易流水号做卡号
     */
    private static byte[] encodeCardNo(String tradeNo) {
        tradeNo = StringUtils.right(tradeNo, 16);
        tradeNo = StringUtils.leftPad(tradeNo, 16, '0');
        return BCDUtil.toBytes(tradeNo);
    }
}