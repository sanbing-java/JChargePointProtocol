/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.provider;

import sanbing.jcpp.protocol.domain.ProtocolSession;

import java.util.UUID;

/**
 * @author baigod
 */
public interface ProtocolSessionRegistryProvider {

    /**
     * 注册会话
     */
    void register(ProtocolSession protocolSession);

    void unregister(UUID sessionId);

    ProtocolSession get(UUID sessionId);

    /**
     * 活跃会话
     */
    void activate(ProtocolSession protocolSession);
}