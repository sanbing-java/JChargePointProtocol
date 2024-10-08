/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.yunkuaichong.v150;

import lombok.extern.slf4j.Slf4j;
import sanbing.jcpp.infrastructure.util.annotation.ProtocolComponent;
import sanbing.jcpp.protocol.ProtocolBootstrap;
import sanbing.jcpp.protocol.ProtocolMessageProcessor;

import static sanbing.jcpp.protocol.yunkuaichong.v150.YunkuaichongV150ProtocolBootstrap.PROTOCOL_NAME;

/**
 * @author baigod
 */

@ProtocolComponent(PROTOCOL_NAME)
@Slf4j
public class YunkuaichongV150ProtocolBootstrap extends ProtocolBootstrap {

    public static final String PROTOCOL_NAME = "yunkuaichongV150";

    @Override
    protected String getProtocolName() {
        return PROTOCOL_NAME;
    }

    @Override
    protected void _init() {
        // do nothing
    }

    @Override
    protected void _destroy() {
        // do nothing
    }

    @Override
    protected ProtocolMessageProcessor messageProcessor() {
        return new YunKuaiChongV15ProtocolMessageProcessor(forwarder, protocolContext);
    }


}