/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.listener.tcp.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import sanbing.jcpp.infrastructure.util.mdc.MDCUtils;
import sanbing.jcpp.infrastructure.util.trace.TracerContextUtil;

/**
 * @author baigod
 */
public class TracerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        TracerContextUtil.newTracer("jcpp-protocol");

        MDCUtils.recordTracer();

        super.channelRead(ctx, msg);
    }
}