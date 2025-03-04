/**
 * 开源代码，仅供学习和交流研究使用，商用请联系三丙
 * 微信：mohan_88888
 * 抖音：程序员三丙
 * 付费课程知识星球：https://t.zsxq.com/aKtXo
 */
package sanbing.jcpp.protocol;

import lombok.extern.slf4j.Slf4j;
import sanbing.jcpp.infrastructure.stats.MessagesStats;
import sanbing.jcpp.infrastructure.util.exception.DownlinkException;
import sanbing.jcpp.infrastructure.util.trace.TracerRunnable;
import sanbing.jcpp.protocol.domain.ListenerToHandlerMsg;
import sanbing.jcpp.protocol.domain.SessionToHandlerMsg;
import sanbing.jcpp.protocol.forwarder.Forwarder;

import java.util.UUID;

/**
 * @author baigod
 */
@Slf4j
public abstract class ProtocolMessageProcessor {
    protected final Forwarder forwarder;
    protected final ProtocolContext protocolContext;

    protected ProtocolMessageProcessor(Forwarder forwarder, ProtocolContext protocolContext) {
        this.forwarder = forwarder;
        this.protocolContext = protocolContext;
    }

    public void uplinkHandleAsync(ListenerToHandlerMsg listenerToHandlerMsg, MessagesStats uplinkMsgStats) {

        UUID id = listenerToHandlerMsg.session().getId();

        protocolContext.getShardingThreadPool().execute(id, new TracerRunnable(() -> {
            try {

                listenerToHandlerMsg.session().setForwarder(forwarder);

                uplinkHandle(listenerToHandlerMsg);

            } catch (Exception e) {

                uplinkMsgStats.incrementFailed();

                log.error("{} 消息处理器处理报文异常", listenerToHandlerMsg.session(), e);
            }
        }));
    }

    protected abstract void uplinkHandle(ListenerToHandlerMsg listenerToHandlerMsg);

    public void downlinkHandle(SessionToHandlerMsg sessionToHandlerMsg, MessagesStats downlinkMsgStats) throws DownlinkException {
        try {

            downlinkHandle(sessionToHandlerMsg);

        } catch (Exception e) {

            downlinkMsgStats.incrementFailed();

            throw new DownlinkException(e.getMessage(), e);
        }
    }

    protected abstract void downlinkHandle(SessionToHandlerMsg sessionToHandlerMsg);
}