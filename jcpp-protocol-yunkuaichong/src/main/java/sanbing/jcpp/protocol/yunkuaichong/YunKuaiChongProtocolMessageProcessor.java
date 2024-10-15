/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.yunkuaichong;

import cn.hutool.core.util.ClassUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import sanbing.jcpp.infrastructure.util.JCPPPair;
import sanbing.jcpp.infrastructure.util.jackson.JacksonUtil;
import sanbing.jcpp.proto.gen.ProtocolProto.DownlinkRestMessage;
import sanbing.jcpp.protocol.ProtocolContext;
import sanbing.jcpp.protocol.ProtocolMessageProcessor;
import sanbing.jcpp.protocol.domain.ListenerToHandlerMsg;
import sanbing.jcpp.protocol.domain.SessionToHandlerMsg;
import sanbing.jcpp.protocol.forwarder.Forwarder;
import sanbing.jcpp.protocol.listener.tcp.TcpSession;
import sanbing.jcpp.protocol.yunkuaichong.annotation.YunKuaiChongCmd;
import sanbing.jcpp.protocol.yunkuaichong.enums.YunKuaiChongDownlinkCmdEnum;

import java.lang.reflect.InvocationTargetException;
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
        UUID msgId = listenerToHandlerMsg.id();
        byte[] msg = listenerToHandlerMsg.msg();
        TcpSession session = (TcpSession) listenerToHandlerMsg.session();

        ByteBuf in = Unpooled.copiedBuffer(msg);

        in.markReaderIndex();

        findStartFlag(in);

        // 判断是否可以读取报头，8个字节
        if (in.readableBytes() < 6) {
            in.resetReaderIndex();
            return;
        }

        // 起始标识, 固定为0x68
        int startFlag = in.readUnsignedByte();
        if (startFlag != 0x68) {
            in.resetReaderIndex();
            return;
        }

        // 数据长度 = 序列号域+加密标志+帧类型标志+消息体
        int dataLength = in.readUnsignedByte();

        // 报文的流水号
        int seqNo = in.readUnsignedShortLE();

        // 加密标志
        int encryptFlag = in.readUnsignedByte();

        // 帧类型标志
        int frameType = in.readUnsignedByte();

        // 判断是否可以读取消息体，N-4个字节
        int msgBodyLength = dataLength - 4;
        if (in.readableBytes() < msgBodyLength) {
            in.resetReaderIndex();
            return;
        }

        // 消息体
        byte[] msgBody = new byte[msgBodyLength];
        in.readBytes(msgBody);

        // 判断是否可以读取校验和， 2个字节
        if (in.readableBytes() < 2) {
            in.resetReaderIndex();
            return;
        }

        byte[] byCheckSum = new byte[2];
        in.readBytes(byCheckSum);
        ByteBuf csTemp = Unpooled.buffer();
        csTemp.writeBytes(byCheckSum);

        // 校验校验和
        int checkSum = csTemp.readUnsignedShort();

        byte[] checkData = new byte[dataLength];

        System.arraycopy(msg, 2, checkData, 0, dataLength);

        JCPPPair<Boolean, Integer> checkResult = checkCrcSum(checkData, checkSum);

        if (Boolean.FALSE.equals(checkResult.getFirst())) {
            csTemp.writeBytes(byCheckSum);
            checkSum = csTemp.readUnsignedShortLE();
            checkResult = checkCrcSum(checkData, checkSum);
            log.info("云快充检验和 第二次检查: checkResult:{}, checkSum:{}", checkResult, checkSum);
        }

        if (Boolean.FALSE.equals(checkResult.getFirst())) {
            log.info("云快充检验和不一致两次不通过 不处理! CMD：{},校验域:{}，正确校验和:{}", frameType, checkSum, checkResult.getSecond());
            return;
        }

        YunKuaiChongUplinkMessage message = new YunKuaiChongUplinkMessage(msgId);
        message.setHead(startFlag);
        message.setDataLength(dataLength);
        message.setSequenceNumber(seqNo);
        message.setEncryptionFlag(encryptFlag);
        message.setCmd(frameType);
        message.setMsgBody(msgBody);
        message.setCheckSum(checkSum);
        message.setRawFrame(msg);

        exeCmd(message, session);
    }

    @Override
    public void downlinkHandle(SessionToHandlerMsg sessionToHandlerMsg) {
        TcpSession session = (TcpSession) sessionToHandlerMsg.session();

        DownlinkRestMessage protocolDownlinkMsg = sessionToHandlerMsg.downlinkMsg();

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

            log.info("[{}] 云快充协议接收到未知的上行指令 {}", session, message.getCmd());

            return;
        }

        uplinkCmdExe.execute(session, message, protocolContext);
    }

    private void exeCmd(YunKuaiChongDwonlinkMessage message, TcpSession session) {
        YunKuaiChongDownlinkCmdExe downlinkCmdExe = downlinkCmdExeMap.get(message.getCmd());

        if (downlinkCmdExe == null) {

            log.info("[{}] 云快充协议接收到未知的下行指令 {}", session, message.getCmd());

            return;
        }

        downlinkCmdExe.execute(session, message, protocolContext);
    }

    private static void findStartFlag(ByteBuf buf) {
        int count = buf.readableBytes();
        for (int index = buf.readerIndex(); index < count - 1; index++) {
            if (buf.getByte(index) == (byte) 0x68) {
                buf.readerIndex(index);
                return;
            }
        }
        buf.resetReaderIndex();
    }


}
