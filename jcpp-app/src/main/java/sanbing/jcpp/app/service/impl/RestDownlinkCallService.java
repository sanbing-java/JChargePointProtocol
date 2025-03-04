/**
 * 开源代码，仅供学习和交流研究使用，商用请联系三丙
 * 微信：mohan_88888
 * 抖音：程序员三丙
 * 付费课程知识星球：https://t.zsxq.com/aKtXo
 */
package sanbing.jcpp.app.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import sanbing.jcpp.app.data.PileSession;
import sanbing.jcpp.app.service.DownlinkCallService;
import sanbing.jcpp.infrastructure.util.trace.TracerContextUtil;
import sanbing.jcpp.proto.gen.ProtocolProto.DownlinkRequestMessage;

import static sanbing.jcpp.infrastructure.util.trace.TracerContextUtil.*;

/**
 * @author baigod
 */
@Service
@Slf4j
@ConditionalOnExpression("'${service.downlink.rpc.type:null}'=='rest'")
public class RestDownlinkCallService extends DownlinkCallService {

    @Resource
    RestTemplate downlinkRestTemplate;

    @Override
    protected void _sendDownlinkMessage(DownlinkRequestMessage downlinkMessage, PileSession pileSession) {
        try {

            invokeDownlinkRestApi(downlinkMessage, pileSession.getNodeIp(), pileSession.getNodeRestPort());

        } catch (RestClientException e) {
            log.error("下行消息发送异常", e);
        }
    }

    private void invokeDownlinkRestApi(DownlinkRequestMessage downlinkRequestMessage, String nodeWebapiIpPort, int nodeRestPort) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JCPP_TRACER_ID, TracerContextUtil.getCurrentTracer().getTraceId());
        headers.add(JCPP_TRACER_ORIGIN, TracerContextUtil.getCurrentTracer().getOrigin());
        headers.add(JCPP_TRACER_TS, String.valueOf(TracerContextUtil.getCurrentTracer().getTracerTs()));
        headers.setContentType(MediaType.parseMediaType("application/x-protobuf"));

        HttpEntity<DownlinkRequestMessage> entity = new HttpEntity<>(downlinkRequestMessage, headers);

        try {
            ResponseEntity<?> response = downlinkRestTemplate.postForEntity("http://" + nodeWebapiIpPort + ":" + nodeRestPort + "/api/onDownlink",
                    entity, ResponseEntity.class);
            log.debug("下行消息发送成功 {}", response);
        } catch (RestClientException e) {
            log.error("下行消息发送失败 {}", downlinkRequestMessage, e);
            throw new RuntimeException(e);
        }
    }
}