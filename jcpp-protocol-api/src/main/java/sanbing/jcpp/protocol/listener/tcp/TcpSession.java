/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.listener.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import sanbing.jcpp.proto.gen.ProtocolProto.DownlinkRestMessage;
import sanbing.jcpp.protocol.domain.ProtocolSession;
import sanbing.jcpp.protocol.domain.SessionCloseReason;
import sanbing.jcpp.protocol.listener.tcp.enums.SequenceNumberLength;

import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 设备会话
 *
 * @author baigod
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class TcpSession extends ProtocolSession {

    private SocketAddress address;

    private ChannelHandlerContext ctx;

    private final Consumer<DownlinkRestMessage> sendDownlinkConsumer;

    private final Consumer<ByteBuf> writeAndFlushConsumer;

    private final AtomicInteger sequenceNumber = new AtomicInteger(0);

    public int nextSeqNo(SequenceNumberLength sequenceNumberLength) {
        synchronized (sequenceNumber) {
            int result = sequenceNumber.incrementAndGet();
            switch (sequenceNumberLength) {
                case BYTE -> {
                    if (result == 0xFF) {
                        sequenceNumber.set(0);
                    }
                }
                case SHORT -> {
                    if (result == Short.MAX_VALUE) {
                        sequenceNumber.set(0);
                    }
                }
                default -> {
                    if (result == Integer.MAX_VALUE) {
                        sequenceNumber.set(0);
                    }
                }
            }

            return result;
        }
    }

    public TcpSession(String protocolName,
                      Consumer<DownlinkRestMessage> sendDownlinkConsumer,
                      Consumer<ByteBuf> writeAndFlushConsumer) {
        super(protocolName);
        this.sendDownlinkConsumer = sendDownlinkConsumer;
        this.writeAndFlushConsumer = writeAndFlushConsumer;
    }

    @Override
    public void onDownlink(DownlinkRestMessage downlinkMsg) {
        sendDownlinkConsumer.accept(downlinkMsg);
    }

    @Override
    public void close(SessionCloseReason reason) {
        super.close(reason);

        ctx.flush();
        ctx.close();
    }

    public void writeAndFlush(ByteBuf byteBuf) {
        writeAndFlushConsumer.accept(byteBuf);
    }
}
