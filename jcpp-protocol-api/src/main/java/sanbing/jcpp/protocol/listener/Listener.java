/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.listener;

import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import org.springframework.boot.actuate.health.Health;
import sanbing.jcpp.infrastructure.stats.DefaultCounter;
import sanbing.jcpp.infrastructure.stats.MessagesStats;
import sanbing.jcpp.infrastructure.stats.StatsFactory;
import sanbing.jcpp.protocol.ProtocolMessageProcessor;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author baigod
 */
public abstract class Listener {

    @Getter
    private final String protocolName;

    @Getter
    private final ProtocolMessageProcessor protocolMessageProcessor;

    protected AtomicInteger connectionsGauge = new AtomicInteger();
    protected MessagesStats uplinkMsgStats;
    protected MessagesStats downlinkMsgStats;
    protected DefaultCounter uplinkTrafficCounter;
    protected DefaultCounter downlinkTrafficCounter;
    protected Timer downlinkTimer;

    protected Listener(String protocolName, ProtocolMessageProcessor protocolMessageProcessor, StatsFactory statsFactory) {
        this.protocolName = protocolName;
        this.protocolMessageProcessor = protocolMessageProcessor;

        statsFactory.createGauge("openConnections", connectionsGauge, "protocol", protocolName);
        this.uplinkMsgStats = statsFactory.createMessagesStats("listenerUplinkMessage", "protocol", protocolName);
        this.downlinkMsgStats = statsFactory.createMessagesStats("listenerDownlinkMessage", "protocol", protocolName);
        this.uplinkTrafficCounter = statsFactory.createDefaultCounter("listenerUplinkTraffic", "protocol", protocolName);
        this.downlinkTrafficCounter = statsFactory.createDefaultCounter("listenerDownlinkTraffic", "protocol", protocolName);
        this.downlinkTimer = statsFactory.createTimer("listenerDownlink", "protocol", protocolName);
    }

    public abstract Health health();

    public abstract void destroy() throws InterruptedException;
}