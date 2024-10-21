/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.app.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import sanbing.jcpp.app.data.PileSession;
import sanbing.jcpp.app.service.DownlinkCallService;
import sanbing.jcpp.app.service.cache.session.PileSessionCacheKey;
import sanbing.jcpp.infrastructure.cache.CacheValueWrapper;
import sanbing.jcpp.infrastructure.cache.TransactionalCache;
import sanbing.jcpp.infrastructure.queue.discovery.ServiceInfoProvider;
import sanbing.jcpp.infrastructure.util.trace.TracerContextUtil;
import sanbing.jcpp.proto.gen.ProtocolProto.DownlinkRestMessage;
import sanbing.jcpp.protocol.adapter.DownlinkController;

import static sanbing.jcpp.infrastructure.util.trace.TracerContextUtil.*;

/**
 * @author baigod
 */
@Service
@Slf4j
public class DefaultDownlinkCallService implements DownlinkCallService {

    @Resource
    RestTemplate downlinkRestTemplate;

    @Resource
    ServiceInfoProvider serviceInfoProvider;

    @Resource
    DownlinkController downlinkController;

    @Resource
    TransactionalCache<PileSessionCacheKey, PileSession> pileSessionCache;

    @Value("${cache.type}")
    private String cacheType;

    @Override
    public void sendDownlinkMessage(DownlinkRestMessage.Builder downlinkMessageBuilder, String pileCode) {
        if (serviceInfoProvider.isMonolith() && "caffeine".equalsIgnoreCase(cacheType)) {

            downlinkController.onDownlink(downlinkMessageBuilder.build())
                    .setResultHandler(result -> log.debug("下行消息发送完成"));

        } else {
            try {
                CacheValueWrapper<PileSession> pileSessionCacheValueWrapper = pileSessionCache.get(new PileSessionCacheKey(pileCode));

                if (pileSessionCacheValueWrapper == null) {
                    log.warn("充电桩会话不存在 {}", pileCode);
                    return;
                }

                PileSession pileSession = pileSessionCacheValueWrapper.get();

                invokeDownlinkRestApi(downlinkMessageBuilder.build(), pileSession.getNodeWebapiIpPort());


            } catch (RestClientException e) {
                log.error("下行消息发送异常", e);
            }
        }
    }

    private void invokeDownlinkRestApi(DownlinkRestMessage downlinkRestMessage, String nodeWebapiIpPort) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JCPP_TRACER_ID, TracerContextUtil.getCurrentTracer().getTraceId());
        headers.add(JCPP_TRACER_ORIGIN, TracerContextUtil.getCurrentTracer().getOrigin());
        headers.add(JCPP_TRACER_TS, String.valueOf(TracerContextUtil.getCurrentTracer().getTracerTs()));
        headers.setContentType(MediaType.parseMediaType("application/x-protobuf"));

        HttpEntity<DownlinkRestMessage> entity = new HttpEntity<>(downlinkRestMessage, headers);

        try {
            ResponseEntity<?> response = downlinkRestTemplate.postForEntity("http://" + nodeWebapiIpPort + "/api/onDownlink",
                    entity, ResponseEntity.class);
            log.info("下行消息发送成功 {}", response);
        } catch (RestClientException e) {
            log.error("下行消息发送失败 {}", downlinkRestMessage, e);
            throw new RuntimeException(e);
        }

    }
}