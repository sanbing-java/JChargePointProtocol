/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.provider;

import sanbing.jcpp.protocol.cfg.ProtocolCfg;

/**
 * @author baigod
 */
public interface ProtocolsConfigProvider {

    ProtocolCfg loadConfig(String protocol);
}