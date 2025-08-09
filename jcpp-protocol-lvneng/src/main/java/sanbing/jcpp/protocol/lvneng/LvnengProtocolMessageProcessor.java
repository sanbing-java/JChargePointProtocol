/**
 * 开源代码，仅供学习和交流研究使用，商用请联系三丙
 * 微信：mohan_88888
 * 抖音：程序员三丙
 * 付费课程知识星球：https://t.zsxq.com/aKtXo
 */
package sanbing.jcpp.protocol.lvneng;

import cn.hutool.core.util.ClassUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import sanbing.jcpp.infrastructure.util.JCPPPair;
import sanbing.jcpp.infrastructure.util.codec.ByteUtil;
import sanbing.jcpp.infrastructure.util.jackson.JacksonUtil;
import sanbing.jcpp.proto.gen.ProtocolProto;
import sanbing.jcpp.protocol.ProtocolContext;
import sanbing.jcpp.protocol.ProtocolMessageProcessor;
import sanbing.jcpp.protocol.domain.ListenerToHandlerMsg;
import sanbing.jcpp.protocol.domain.SessionToHandlerMsg;
import sanbing.jcpp.protocol.forwarder.Forwarder;
import sanbing.jcpp.protocol.listener.tcp.TcpSession;
import sanbing.jcpp.protocol.lvneng.annotation.LvnengCmd;
import sanbing.jcpp.protocol.lvneng.enums.LvnengDownlinkCmdEnum;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class LvnengProtocolMessageProcessor extends ProtocolMessageProcessor {
    // 协议常量定义
    private static final int HEADER_SIZE = 9;  // 帧头(2) + 长度(2) + 加密标识(1) + 序号(1) + 命令字(2) + 校验和(1)
    private static final int FRAME_MIN_LENGTH = 9;  // 最小帧长度（无数据域的情况）

    private final Map<Integer, LvnengUplinkCmdExe> uplinkCmdExeMap = new ConcurrentHashMap<>();
    private final Map<Integer, LvnengDownlinkCmdExe> downlinkCmdExeMap = new ConcurrentHashMap<>();

    public LvnengProtocolMessageProcessor(Forwarder forwarder, ProtocolContext protocolContext) {
        super(forwarder, protocolContext);

        Set<Class<?>> cmdClasses = ClassUtil.scanPackageByAnnotation(ClassUtil.getPackage(this.getClass()), LvnengCmd.class);
        cmdClasses.stream().filter(LvnengUplinkCmdExe.class::isAssignableFrom)
                .forEach(clazz -> {
                    int cmd = clazz.getAnnotation(LvnengCmd.class).value();
                    try {
                        LvnengUplinkCmdExe lvnengUplinkCmdExe = (LvnengUplinkCmdExe) clazz.getDeclaredConstructor().newInstance();
                        uplinkCmdExeMap.put(cmd, lvnengUplinkCmdExe);
                    } catch (InstantiationException |
                             IllegalAccessException |
                             InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                });

        cmdClasses.stream().filter(LvnengDownlinkCmdExe.class::isAssignableFrom)
                .forEach(clazz -> {
                    int cmd = clazz.getAnnotation(LvnengCmd.class).value();
                    try {
                        LvnengDownlinkCmdExe lvnengDownlinkCmdExe = (LvnengDownlinkCmdExe) clazz.getDeclaredConstructor().newInstance();
                        downlinkCmdExeMap.put(cmd, lvnengDownlinkCmdExe);
                    } catch (InstantiationException |
                             IllegalAccessException |
                             InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    protected void uplinkHandle(ListenerToHandlerMsg listenerToHandlerMsg) {
        final UUID msgId = listenerToHandlerMsg.id();
        final byte[] msg = listenerToHandlerMsg.msg();
        final TcpSession session = (TcpSession) listenerToHandlerMsg.session();

        ByteBuf in = Unpooled.wrappedBuffer(msg);
        try {
            // 1. 解析帧头信息
            final int startFlag = in.readUnsignedShort();
            final int dataLength = in.readUnsignedShortLE();
            final int encryptFlag = in.readUnsignedByte();
            final int seqNo = in.readUnsignedByte();
            final int frameType = in.readUnsignedShortLE();

            // 2. 计算并检查消息体长度
            if (dataLength < FRAME_MIN_LENGTH) {
                log.warn("{} 绿能协议帧长度异常，期望最小长度:{} 实际长度:{}", 
                        session, FRAME_MIN_LENGTH, dataLength);
                return;
            }
            final int msgBodyLength = dataLength - HEADER_SIZE;

            // 3. 读取消息体数据
            byte[] msgBody = new byte[msgBodyLength];
            in.readBytes(msgBody);
            
            // 4. 校验和验证
            byte receivedCheckSum = in.readByte();
            
            // 准备校验和数据（命令字 + 数据域）
            byte[] sumData = new byte[2 + msgBody.length];  // 2字节命令字 + 数据域
            sumData[0] = (byte) (frameType & 0xFF);
            sumData[1] = (byte) ((frameType >> 8) & 0xFF);
            System.arraycopy(msgBody, 0, sumData, 2, msgBody.length);
            
            // 验证校验和
            JCPPPair<Boolean, Byte> checkResult = ByteUtil.verifySum(sumData, receivedCheckSum);
            if (!checkResult.getFirst()) {
                log.warn("{} 绿能校验和验证失败 CMD:0x{} 接收校验和:0x{} 期望校验和:0x{}", 
                        session, Integer.toHexString(frameType), 
                        String.format("%02x", receivedCheckSum & 0xFF), 
                        String.format("%02x", checkResult.getSecond() & 0xFF));
                return;
            }

            // 5. 构建上行消息对象并执行
            LvnengUplinkMessage uplinkMessage = new LvnengUplinkMessage(msgId)
                    .setHead(startFlag)
                    .setDataLength(dataLength)
                    .setSequenceNumber(seqNo)
                    .setEncryptionFlag(encryptFlag)
                    .setCmd(frameType)
                    .setMsgBody(msgBody)
                    .setCheckSum(checkResult.getSecond())
                    .setRawFrame(msg);
                    
            exeCmd(uplinkMessage, session);
            
        } catch (Exception e) {
            log.error("{} 处理绿能协议上行消息时发生异常", session, e);
        } finally {
            in.release();
        }
    }

    @Override
    protected void downlinkHandle(SessionToHandlerMsg sessionToHandlerMsg) {
        TcpSession session = (TcpSession) sessionToHandlerMsg.session();

        ProtocolProto.DownlinkRequestMessage protocolDownlinkMsg = sessionToHandlerMsg.downlinkMsg();

        int cmd = LvnengDownlinkCmdEnum.valueOf(protocolDownlinkMsg.getDownlinkCmd()).getCmd();

        LvnengDwonlinkMessage message = new LvnengDwonlinkMessage();
        message.setId(new UUID(protocolDownlinkMsg.getMessageIdMSB(), protocolDownlinkMsg.getMessageIdLSB()));
        message.setCmd(cmd);
        message.setMsg(protocolDownlinkMsg);

        if (protocolDownlinkMsg.hasRequestIdMSB() && protocolDownlinkMsg.hasRequestIdLSB()) {
            message.setRequestId(new UUID(protocolDownlinkMsg.getRequestIdMSB(), protocolDownlinkMsg.getRequestIdLSB()));
        }

        if (protocolDownlinkMsg.hasRequestData()) {
            message.setRequestData(JacksonUtil.fromBytes(protocolDownlinkMsg.getRequestData().toByteArray(), LvnengUplinkMessage.class));
        }

        exeCmd(message, session);
    }


    private void exeCmd(LvnengUplinkMessage message, TcpSession session) {
        LvnengUplinkCmdExe uplinkCmdExe = uplinkCmdExeMap.get(message.getCmd());

        if (uplinkCmdExe == null) {

            log.info("{} 绿能协议接收到未知的上行指令 0x{}", session, Integer.toHexString(message.getCmd()));

            return;
        }

        uplinkCmdExe.execute(session, message, protocolContext);
    }

    private void exeCmd(LvnengDwonlinkMessage message, TcpSession session) {
        LvnengDownlinkCmdExe downlinkCmdExe = downlinkCmdExeMap.get(message.getCmd());

        if (downlinkCmdExe == null) {

            log.info("{} 绿能协议接收到未知的下行指令 0x{}", session, Integer.toHexString(message.getCmd()));

            return;
        }

        downlinkCmdExe.execute(session, message, protocolContext);
    }

}
