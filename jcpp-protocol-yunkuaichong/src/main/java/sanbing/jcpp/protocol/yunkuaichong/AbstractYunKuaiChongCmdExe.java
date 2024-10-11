/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.yunkuaichong;

import cn.hutool.core.text.CharSequenceUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.StringUtils;
import sanbing.jcpp.infrastructure.util.codec.BCDUtil;
import sanbing.jcpp.proto.gen.ProtocolProto;
import sanbing.jcpp.protocol.listener.tcp.TcpSession;
import sanbing.jcpp.protocol.listener.tcp.enums.SequenceNumberLength;
import sanbing.jcpp.protocol.yunkuaichong.v150.enums.YunKuaiChongV150DownlinkCmdEnum;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.util.List;

import static sanbing.jcpp.infrastructure.util.codec.ByteUtil.crcSum;
import static sanbing.jcpp.infrastructure.util.codec.ByteUtil.toBytes;

/**
 * @author baigod
 */
public class AbstractYunKuaiChongCmdExe {

    private static final byte TOP_BYTE = 0x00;
    private static final byte PEAK_BYTE = 0x01;
    private static final byte FLAT_BYTE = 0x02;
    private static final byte VALLEY_BYTE = 0x03;

    protected static final byte YUNKUAICHONG_HEAD = 0x68;
    protected static final int YUNKUAICHONG_NORMAL_ENCRYPTION_FLAG = 0;

    private static final DecimalFormat PRICING_ID_DECIMAL_FORMAT = new DecimalFormat("0000");

    protected static String decodeTradeNo(byte[] tradeNo) {
        String tradeNoStr = BCDUtil.toString(tradeNo);
        return CharSequenceUtil.strip(tradeNoStr, "0", null);
    }

    protected byte[] encodePricingId(long pricingId) {
        return BCDUtil.toBytes(PRICING_ID_DECIMAL_FORMAT.format(pricingId % 10000));
    }

    protected static byte getFlagForCurrentTime(List<ProtocolProto.PeriodProto> periodList, LocalTime currentTime) {
        for (ProtocolProto.PeriodProto period : periodList) {
            LocalTime beginLt = LocalTime.parse(period.getBegin());
            LocalTime endLt = "00:00".equals(period.getEnd()) ? LocalTime.MAX : LocalTime.parse(period.getEnd());
            if ((currentTime.equals(beginLt) || currentTime.isAfter(beginLt)) && currentTime.isBefore(endLt)) {
                switch (period.getFlag()) {
                    case TOP:
                        return TOP_BYTE;
                    case PEAK:
                        return PEAK_BYTE;
                    case VALLEY:
                        return VALLEY_BYTE;
                    default:
                        return FLAT_BYTE;
                }
            }
        }
        return FLAT_BYTE; // 默认情况下返回平价
    }

    protected static byte[] encodePileCode(String pileCode) {
        if (StringUtils.length(pileCode) > 32) {
            throw new IllegalArgumentException("云快充1.5可接受最大桩编号为14位");
        }

        String pileCodeStr = StringUtils.leftPad(pileCode, 14, '0');

        return BCDUtil.toBytes(pileCodeStr);
    }

    protected static byte[] encodeGunCode(String gunCode) {
        if (StringUtils.length(gunCode) > 2) {
            throw new IllegalArgumentException("云快充1.5可接受最大枪编号为2位");
        }

        String gunCodeStr = StringUtils.leftPad(gunCode, 2, '0');

        return BCDUtil.toBytes(gunCodeStr);
    }

    protected static byte[] encodeTradeNo(String tradeNo) {
        if (StringUtils.length(tradeNo) > 32) {
            throw new IllegalArgumentException("云快充1.5可接受最大交易流水号为32位");
        }

        String tradeNoStr = StringUtils.leftPad(tradeNo, 32, '0');

        return BCDUtil.toBytes(tradeNoStr);
    }


    protected byte[] encode(YunKuaiChongV150DownlinkCmdEnum downlinkCmd,
                            int seqNo,
                            int encryptionFlag,
                            ByteBuf msgBody) {
        int msgBodyLength = msgBody.readableBytes();
        ByteBuf response = Unpooled.buffer(msgBodyLength + 6);
        response.writeByte(YUNKUAICHONG_HEAD);
        response.writeByte(msgBodyLength + 4);
        response.writeShortLE(seqNo);
        response.writeByte(encryptionFlag);
        response.writeByte(downlinkCmd.getCmd());
        response.writeBytes(msgBody);

        // 帧校验域：从序列号域到数据域的 CRC 校验，校验多项式为 0x180D,低字节在前，高字节在后
        byte[] checkArr = new byte[msgBodyLength + 4];
        System.arraycopy(response.array(), 2, checkArr, 0, checkArr.length);

        response.writeShortLE(crcSum(checkArr));

        return toBytes(response);
    }

    protected void encodeAndWriteFlush(YunKuaiChongV150DownlinkCmdEnum downlinkCmd,
                                       int seqNo,
                                       int encryptionFlag,
                                       ByteBuf msgBody,
                                       TcpSession tcpSession) {

        byte[] encode = encode(downlinkCmd, seqNo, encryptionFlag, msgBody);

        tcpSession.writeAndFlush(Unpooled.copiedBuffer(encode));
    }

    protected void encodeAndWriteFlush(YunKuaiChongV150DownlinkCmdEnum downlinkCmd,
                                       ByteBuf msgBody,
                                       TcpSession tcpSession) {

        byte[] encode = encode(downlinkCmd,
                tcpSession.nextSeqNo(SequenceNumberLength.SHORT),
                YUNKUAICHONG_NORMAL_ENCRYPTION_FLAG,
                msgBody);

        tcpSession.writeAndFlush(Unpooled.copiedBuffer(encode));
    }

    protected static BigDecimal reduceMagnification(long value, int magnification) {
        return new BigDecimal(value).divide(new BigDecimal(magnification), 4, RoundingMode.HALF_UP);
    }

    protected static BigDecimal reduceMagnification(long value, int magnification, int scale) {
        return new BigDecimal(value).divide(new BigDecimal(magnification), scale, RoundingMode.HALF_UP);
    }

}