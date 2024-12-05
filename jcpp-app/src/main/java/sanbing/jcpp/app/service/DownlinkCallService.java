/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.app.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import sanbing.jcpp.app.data.PileSession;
import sanbing.jcpp.app.service.cache.session.PileSessionCacheKey;
import sanbing.jcpp.infrastructure.cache.CacheValueWrapper;
import sanbing.jcpp.infrastructure.cache.TransactionalCache;
import sanbing.jcpp.infrastructure.queue.discovery.ServiceInfoProvider;
import sanbing.jcpp.proto.gen.ProtocolProto.DownlinkRequestMessage;
import sanbing.jcpp.protocol.adapter.DownlinkController;

import java.util.UUID;

/**
 * @author baigod
 */
@Slf4j
public abstract class DownlinkCallService {

    @Resource
    protected ServiceInfoProvider serviceInfoProvider;

    @Resource
    protected DownlinkController downlinkController;

    @Resource
    protected TransactionalCache<PileSessionCacheKey, PileSession> pileSessionCache;

    @Value("${cache.type}")
    protected String cacheType;

    public void sendDownlinkMessage(DownlinkRequestMessage.Builder downlinkMessageBuilder, String pileCode) {
        CacheValueWrapper<PileSession> pileSessionCacheValueWrapper = pileSessionCache.get(new PileSessionCacheKey(pileCode));

        if (pileSessionCacheValueWrapper == null) {
            log.warn("充电桩会话不存在 {}", pileCode);
            return;
        }

        PileSession pileSession = pileSessionCacheValueWrapper.get();

        UUID protocolSessionId = pileSession.getProtocolSessionId();

        if (downlinkMessageBuilder.getSessionIdMSB() == 0) {
            downlinkMessageBuilder.setSessionIdMSB(protocolSessionId.getMostSignificantBits());
        }
        if (downlinkMessageBuilder.getSessionIdLSB() == 0) {
            downlinkMessageBuilder.setSessionIdLSB(protocolSessionId.getLeastSignificantBits());
        }
        if(downlinkMessageBuilder.getProtocolName() == null){
            downlinkMessageBuilder.setProtocolName(pileSession.getProtocolName());
        }

        if (serviceInfoProvider.isMonolith() &&
                ("caffeine".equalsIgnoreCase(cacheType) || serviceInfoProvider.getServiceId().equalsIgnoreCase(pileSession.getNodeId()))) {

            downlinkController.onDownlink(downlinkMessageBuilder.build())
                    .setResultHandler(result -> log.debug("下行消息发送完成"));

        } else {

            _sendDownlinkMessage(downlinkMessageBuilder.build(), pileSession);
        }
    }


    protected abstract void _sendDownlinkMessage(DownlinkRequestMessage downlinkMessage, PileSession pileSession);
}