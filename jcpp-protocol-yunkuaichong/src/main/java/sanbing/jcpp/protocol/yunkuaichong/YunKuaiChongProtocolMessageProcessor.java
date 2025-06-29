/**
 * 开源代码，仅供学习和交流研究使用，商用请联系三丙
 * 微信：mohan_88888
 * 抖音：程序员三丙
 * 付费课程知识星球：https://t.zsxq.com/aKtXo
 */
package sanbing.jcpp.protocol.yunkuaichong;

import cn.hutool.core.util.ClassUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import sanbing.jcpp.infrastructure.util.JCPPPair;
import sanbing.jcpp.infrastructure.util.jackson.JacksonUtil;
import sanbing.jcpp.proto.gen.ProtocolProto.DownlinkRequestMessage;
import sanbing.jcpp.protocol.ProtocolContext;
import sanbing.jcpp.protocol.ProtocolMessageProcessor;
import sanbing.jcpp.protocol.domain.ListenerToHandlerMsg;
import sanbing.jcpp.protocol.domain.SessionToHandlerMsg;
import sanbing.jcpp.protocol.forwarder.Forwarder;
import sanbing.jcpp.protocol.listener.tcp.TcpSession;
import sanbing.jcpp.protocol.yunkuaichong.annotation.YunKuaiChongCmd;
import sanbing.jcpp.protocol.yunkuaichong.enums.YunKuaiChongDownlinkCmdEnum;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static sanbing.jcpp.infrastructure.util.codec.ByteUtil.checkCrcSum;

@Slf4j
public class YunKuaiChongProtocolMessageProcessor extends ProtocolMessageProcessor {
    private final Map<Integer, YunKuaiChongUplinkCmdExe> uplinkCmdExeMap = new ConcurrentHashMap<>();
    private final Map<Integer, YunKuaiChongDownlinkCmdExe> downlinkCmdExeMap = new ConcurrentHashMap<>();

    public YunKuaiChongProtocolMessageProcessor(Forwarder forwarder, ProtocolContext protocolContext) {
        super(forwarder, protocolContext);

        Set<Class<?>> cmdClasses = ClassUtil.scanPackageByAnnotation(ClassUtil.getPackage(this.getClass()), YunKuaiChongCmd.class);
        cmdClasses.stream().filter(YunKuaiChongUplinkCmdExe.class::isAssignableFrom)
                .forEach(clazz -> {
                    int cmd = clazz.getAnnotation(YunKuaiChongCmd.class).value();
                    try {
                        YunKuaiChongUplinkCmdExe yunKuaiChongUplinkCmdExe = (YunKuaiChongUplinkCmdExe) clazz.getDeclaredConstructor().newInstance();
                        uplinkCmdExeMap.put(cmd, yunKuaiChongUplinkCmdExe);
                    } catch (InstantiationException |
                             IllegalAccessException |
                             InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                });

        cmdClasses.stream().filter(YunKuaiChongDownlinkCmdExe.class::isAssignableFrom)
                .forEach(clazz -> {
                    int cmd = clazz.getAnnotation(YunKuaiChongCmd.class).value();
                    try {
                        YunKuaiChongDownlinkCmdExe yunKuaiChongDownlinkCmdExe = (YunKuaiChongDownlinkCmdExe) clazz.getDeclaredConstructor().newInstance();
                        downlinkCmdExeMap.put(cmd, yunKuaiChongDownlinkCmdExe);
                    } catch (InstantiationException |
                             IllegalAccessException |
                             InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public void uplinkHandle(ListenerToHandlerMsg listenerToHandlerMsg) {
        final UUID msgId = listenerToHandlerMsg.id();
        final byte[] msg = listenerToHandlerMsg.msg();
        final TcpSession session = (TcpSession) listenerToHandlerMsg.session();

        // ================== 前置快速失败检查 ==================
        if (msg.length < 8 || msg[0] != 0x68) {
            return;
        }

        ByteBuf in = Unpooled.wrappedBuffer(msg);
        try {
            // ================== 协议头解析 ==================
            final int dataLength = in.getUnsignedByte(1);
            final int bodyLength = dataLength - 4;
            final int checksumPos = 6 + bodyLength;

            // ================== 组合边界检查 ==================
            if (dataLength < 4 || in.readableBytes() < checksumPos + 2) {
                return;
            }

            // ================== 字段快速解析 ==================
            final int seqNo = in.getUnsignedShort(2);
            final int encryptFlag = in.getUnsignedByte(4);
            final int frameType = in.getUnsignedByte(5);

            // ================== 校验和双模式处理 ==================
            final int checkSumLE = in.getUnsignedShortLE(checksumPos);
            final int checkSumBE = in.getUnsignedShort(checksumPos);

            // ================== 校验数据智能拷贝 ==================
            final byte[] checkData = Arrays.copyOfRange(msg, 2, 2 + dataLength);

            // ================== 短路校验流程 ==================
            JCPPPair<Boolean, Integer> checkResult = checkCrcSum(checkData, checkSumLE);
            if (!checkResult.getFirst()) {
                checkResult = checkCrcSum(checkData, checkSumBE);
                if (log.isDebugEnabled()) { // 日志惰性计算
                    log.debug("{} 云快充校验域一次校验失败 CMD:{} 校验和：0x{} 期望校验和:0x{}",
                            session, frameType, Integer.toHexString(checkSumBE), Integer.toHexString(checkResult.getSecond()));
                }
            }

            // ================== 最终校验失败处理 ==================
            if (!checkResult.getFirst()) {
                log.info("{} 云快充校验域二次校验失败 CMD:{} 校验和：0x{} 期望校验和:0x{}",
                        session, frameType, Integer.toHexString(checkSumBE), Integer.toHexString(checkResult.getSecond()));
                return;
            }

            // ================== 消息对象智能构建 ==================
            ByteBuf slicedBuf = in.slice(6, bodyLength);

            if (slicedBuf.readableBytes() != bodyLength) {
                log.error("协议体长度异常: expected={}, actual={}",
                        bodyLength, slicedBuf.readableBytes());
                return;
            }

            byte[] msgBody = new byte[bodyLength];
            slicedBuf.readBytes(msgBody);

            exeCmd(new YunKuaiChongUplinkMessage(msgId)
                            .setHead(0x68)
                            .setDataLength(dataLength)
                            .setSequenceNumber(seqNo)
                            .setEncryptionFlag(encryptFlag)
                            .setCmd(frameType)
                            .setMsgBody(msgBody)  // 使用正确长度的数组
                            .setCheckSum(checkResult.getSecond())
                            .setRawFrame(msg),
                    session);
        } finally {
            in.release();
        }
    }

    @Override
    public void downlinkHandle(SessionToHandlerMsg sessionToHandlerMsg) {
        TcpSession session = (TcpSession) sessionToHandlerMsg.session();

        DownlinkRequestMessage protocolDownlinkMsg = sessionToHandlerMsg.downlinkMsg();

        int cmd = YunKuaiChongDownlinkCmdEnum.valueOf(protocolDownlinkMsg.getDownlinkCmd()).getCmd();

        YunKuaiChongDwonlinkMessage message = new YunKuaiChongDwonlinkMessage();
        message.setId(new UUID(protocolDownlinkMsg.getMessageIdMSB(), protocolDownlinkMsg.getMessageIdLSB()));
        message.setCmd(cmd);
        message.setMsg(protocolDownlinkMsg);

        if (protocolDownlinkMsg.hasRequestIdMSB() && protocolDownlinkMsg.hasRequestIdLSB()) {
            message.setRequestId(new UUID(protocolDownlinkMsg.getRequestIdMSB(), protocolDownlinkMsg.getRequestIdLSB()));
        }

        if (protocolDownlinkMsg.hasRequestData()) {
            message.setRequestData(JacksonUtil.fromBytes(protocolDownlinkMsg.getRequestData().toByteArray(), YunKuaiChongUplinkMessage.class));
        }

        exeCmd(message, session);
    }

    private void exeCmd(YunKuaiChongUplinkMessage message, TcpSession session) {
        YunKuaiChongUplinkCmdExe uplinkCmdExe = uplinkCmdExeMap.get(message.getCmd());

        if (uplinkCmdExe == null) {

            log.info("{} 云快充协议接收到未知的上行指令 0x{}", session, Integer.toHexString(message.getCmd()));

            return;
        }

        uplinkCmdExe.execute(session, message, protocolContext);
    }

    private void exeCmd(YunKuaiChongDwonlinkMessage message, TcpSession session) {
        YunKuaiChongDownlinkCmdExe downlinkCmdExe = downlinkCmdExeMap.get(message.getCmd());

        if (downlinkCmdExe == null) {

            log.info("{} 云快充协议接收到未知的下行指令 0x{}", session, Integer.toHexString(message.getCmd()));

            return;
        }

        downlinkCmdExe.execute(session, message, protocolContext);
    }


}
