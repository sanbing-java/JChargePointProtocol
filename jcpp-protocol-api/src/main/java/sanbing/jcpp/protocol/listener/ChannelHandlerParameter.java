/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.listener;

import io.micrometer.core.instrument.Timer;
import sanbing.jcpp.infrastructure.stats.DefaultCounter;
import sanbing.jcpp.infrastructure.stats.MessagesStats;
import sanbing.jcpp.protocol.ProtocolMessageProcessor;
import sanbing.jcpp.protocol.cfg.TcpHandlerCfg;

import java.util.concurrent.atomic.AtomicInteger;

public record ChannelHandlerParameter(String protocolName,
                                      TcpHandlerCfg handlerCfg,
                                      ProtocolMessageProcessor protocolMessageProcessor,
                                      AtomicInteger connectionsGauge,
                                      MessagesStats uplinkMsgStats,
                                      MessagesStats downlinkMsgStats,
                                      DefaultCounter uplinkTrafficCounter,
                                      DefaultCounter downlinkTrafficCounter,
                                      Timer downlinkTimer) {
}