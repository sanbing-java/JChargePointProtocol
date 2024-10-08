/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.yunkuaichong.v150.cmd;

import cn.hutool.core.util.HexUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import sanbing.jcpp.infrastructure.util.codec.BCDUtil;
import sanbing.jcpp.infrastructure.util.jackson.JacksonUtil;
import sanbing.jcpp.protocol.ProtocolContext;
import sanbing.jcpp.protocol.listener.tcp.TcpSession;
import sanbing.jcpp.protocol.yunkuaichong.YunKuaiChongUplinkCmdExe;
import sanbing.jcpp.protocol.yunkuaichong.YunKuaiChongUplinkMessage;
import sanbing.jcpp.protocol.yunkuaichong.annotation.YunKuaiChongCmd;

import java.nio.charset.StandardCharsets;

/**
 * 云快充1.5.0 充电握手
 *
 * @author baigod
 */
@Slf4j
@YunKuaiChongCmd(0x15)
public class YunKuaiChongV150BmsHandshakeULCmd extends YunKuaiChongUplinkCmdExe {
    @Override
    public void execute(TcpSession tcpSession, YunKuaiChongUplinkMessage yunKuaiChongUplinkMessage, ProtocolContext ctx) {
        log.debug("{} 云快充1.5.0充电握手", tcpSession);
        ByteBuf byteBuf = Unpooled.copiedBuffer(yunKuaiChongUplinkMessage.getMsgBody());

        ObjectNode additionalInfo = JacksonUtil.newObjectNode();

        // 1.交易流水号
        byte[] tradeNoBytes = new byte[16];
        byteBuf.readBytes(tradeNoBytes);
        String tradeNo = BCDUtil.toString(tradeNoBytes);
        additionalInfo.put("交易流水号", tradeNo);

        // 2.桩编号
        byte[] pileCodeBytes = new byte[7];
        byteBuf.readBytes(pileCodeBytes);
        String pileCode = BCDUtil.toString(pileCodeBytes);
        additionalInfo.put("桩编号", tradeNo);

        // 3.抢号
        byte gunCodeByte = byteBuf.readByte();
        String gunCode = BCDUtil.toString(gunCodeByte);
        additionalInfo.put("抢号", tradeNo);

        // 4.BMS 通信协议版本号
        byte[] bmsConnectVersionBytes = new byte[3];
        byteBuf.readBytes(bmsConnectVersionBytes);
        additionalInfo.put("BMS 通信协议版本号", HexUtil.encodeHexStr(bmsConnectVersionBytes));

        // 5.BMS 电池类型
        int bmsBatteryType = byteBuf.readUnsignedByte();
        additionalInfo.put("BMS电池类型", bmsBatteryType);

        // 6.BMS 整车动力蓄电池系统额定容量
        int bmsPowerCapacity = byteBuf.readUnsignedShortLE();
        additionalInfo.put("BMS整车动力蓄电池系统额定容量", bmsPowerCapacity);

        // 7.BMS 整车动力蓄电池系统额定总电压
        int bmsPowerMaxVoltage = byteBuf.readUnsignedShortLE();
        additionalInfo.put("BMS整车动力蓄电池系统额定总电压", bmsPowerMaxVoltage);

        // 8.BMS 电池生产厂商名称
        byte[] bmsFactoryBytes = new byte[4];
        byteBuf.readBytes(bmsFactoryBytes);
        String bmsFactory = new String(bmsFactoryBytes, StandardCharsets.US_ASCII);
        additionalInfo.put("BMS电池生产厂商名称", bmsFactory);

        // 9.BMS 电池组序号
        int bmsSerialNo = byteBuf.readIntLE();
        additionalInfo.put("BMS 电池组序号", bmsSerialNo);

        // 10.BMS 电池组生产日期年
        int bmsCreateYear = byteBuf.readUnsignedByte();
        additionalInfo.put("BMS 电池组生产日期年", bmsCreateYear);

        // 11.BMS 电池组生产日期月
        int bmsCreateMonth = byteBuf.readUnsignedByte();
        additionalInfo.put("BMS 电池组生产日期月", bmsCreateMonth);

        // 12.BMS 电池组生产日期日
        int bmsCreateDay = byteBuf.readUnsignedByte();
        additionalInfo.put("BMS 电池组生产日期日", bmsCreateDay);

        // 13.BMS 电池组充电次数
        int bmsChargeCount = byteBuf.readUnsignedMedium();
        additionalInfo.put("BMS 电池组充电次数", bmsChargeCount);

        // 14.BMS 电池组产权标识
        int bmsPropertyRightLabel = byteBuf.readUnsignedByte();
        additionalInfo.put("BMS 电池组产权标识", bmsPropertyRightLabel);

        // 15.预留位
        byteBuf.skipBytes(1);

        // 16.BMS 车辆识别码
        byte[] carVINBytes = new byte[17];
        byteBuf.readBytes(carVINBytes);
        String bmsVinCode = new String(carVINBytes, StandardCharsets.US_ASCII);
        additionalInfo.put("电动汽车唯一标识", bmsVinCode);

        // 17.BMS 软件版本号
        byte[] bmsSoftVersionBytes = new byte[8];
        byteBuf.readBytes(bmsSoftVersionBytes);
        additionalInfo.put("BMS 软件版本号", HexUtil.encodeHexStr(bmsSoftVersionBytes));

        // TODO 先打印日志，暂不转发
        log.debug("{} 云快充1.5.0充电握手信息解析完成:{}", tcpSession, additionalInfo);
    }
}