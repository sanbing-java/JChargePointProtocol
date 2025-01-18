/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.listener;

import io.micrometer.core.instrument.Timer;
import sanbing.jcpp.infrastructure.stats.MessagesStats;
import sanbing.jcpp.protocol.ProtocolMessageProcessor;

import java.util.concurrent.atomic.AtomicInteger;

public record ChannelHandlerParameter(String protocolName,
                                      ProtocolMessageProcessor protocolMessageProcessor,
                                      AtomicInteger connectionsGauge,
                                      MessagesStats uplinkMsgStats,
                                      MessagesStats downlinkMsgStats,
                                      Timer downlinkTimer) {
}