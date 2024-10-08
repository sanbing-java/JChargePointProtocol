/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.adapter;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import sanbing.jcpp.proto.gen.ProtocolProto.DownlinkRestMessage;
import sanbing.jcpp.protocol.domain.ProtocolSession;
import sanbing.jcpp.protocol.provider.ProtocolSessionRegistryProvider;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author baigod
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class DownlinkController {

    @Value("${api.timeout.onDownlink:3000}")
    public long onDownlinkTimeout;

    @Resource
    ProtocolSessionRegistryProvider protocolSessionRegistryProvider;

    @PostMapping(value = "/onDownlink", consumes = "application/x-protobuf", produces = "application/x-protobuf")
    public DeferredResult<ResponseEntity<String>> onDownlink(@RequestBody DownlinkRestMessage downlinkMsg) {
        log.info("收到REST下行请求 {}", downlinkMsg);

        final DeferredResult<ResponseEntity<String>> response = new DeferredResult<>(onDownlinkTimeout,
                ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build());

        UUID protocolSessionId = new UUID(downlinkMsg.getSessionIdMSB(),downlinkMsg.getSessionIdLSB()) ;

        CompletableFuture<ProtocolSession> protocolSessionCompletableFuture = protocolSessionRegistryProvider.get(protocolSessionId);

        protocolSessionCompletableFuture.thenAccept(session -> {
            if (session != null) {

                session.onDownlink(downlinkMsg);

                response.setResult(ResponseEntity.status(HttpStatus.OK).build());
            } else {

                log.warn("下发报文时Session未找到 sessionId: {}", protocolSessionId);

                response.setResult(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Protocol Session not found for ID:" + protocolSessionId));
            }
        }).whenComplete((unused, throwable) -> {
            if (throwable != null) {

                log.warn("下发报文时处理失败 sessionId: {}", protocolSessionId, throwable);

                if (!response.isSetOrExpired()) {

                    response.setResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(throwable.getMessage()));
                }
            }
        });

        return response;
    }
}