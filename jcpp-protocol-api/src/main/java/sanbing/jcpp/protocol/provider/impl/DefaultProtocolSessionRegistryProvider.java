/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.provider.impl;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sanbing.jcpp.infrastructure.util.async.JCPPThreadFactory;
import sanbing.jcpp.infrastructure.util.config.ThreadPoolConfiguration;
import sanbing.jcpp.protocol.domain.ProtocolSession;
import sanbing.jcpp.protocol.domain.SessionCloseReason;
import sanbing.jcpp.protocol.provider.ProtocolSessionRegistryProvider;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * @author baigod
 */
@Service
@Slf4j
public class DefaultProtocolSessionRegistryProvider implements ProtocolSessionRegistryProvider {
    private static final int INIT_CACHE_LIMIT = 100_000;

    @Value("${service.protocols.sessions.default-inactivity-timeout-in-sec}")
    private int defaultInactivityTimeoutInSec;

    @Value("${service.protocols.sessions.default-state-check-interval-in-sec}")
    private int defaultStateCheckIntervalInSec;

    @Getter
    private final AsyncCache<UUID, ProtocolSession> SESSION_CACHE = buildCache();

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(JCPPThreadFactory.forName("session-state-checker"));

    @PostConstruct
    public void init() {
        scheduledExecutorService.scheduleAtFixedRate(() ->
                SESSION_CACHE.asMap().forEach((id, sessionCompletableFuture) ->
                        sessionCompletableFuture.whenComplete((protocolSession, throwable) -> {
                            if (throwable == null && protocolSession != null) {
                                if (protocolSession.getLastActivityTime().isBefore(LocalDateTime.now().minusSeconds(defaultInactivityTimeoutInSec))) {
                                    protocolSession.close(SessionCloseReason.INACTIVE);
                                    unregister(protocolSession.getId());
                                }
                            }
                        })
                ), defaultStateCheckIntervalInSec, defaultStateCheckIntervalInSec, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void destroy() {
        scheduledExecutorService.shutdownNow();
    }

    @Override
    public void register(ProtocolSession protocolSession) {

        if (log.isDebugEnabled()) {
            log.debug("Registering session {}", protocolSession);
        }

        SESSION_CACHE.put(protocolSession.getId(), CompletableFuture.supplyAsync(() -> protocolSession, ThreadPoolConfiguration.JCPP_COMMON_THREAD_POOL));

    }

    @Override
    public void unregister(UUID sessionId) {

        log.info("Unregistering session {}", sessionId);

        SESSION_CACHE.synchronous().invalidate(sessionId);
    }

    @Override
    public CompletableFuture<ProtocolSession> get(UUID sessionId) {

        log.debug("Get session {}", sessionId);

        return SESSION_CACHE.get(sessionId, uuid -> null);

    }

    @Override
    public void activate(ProtocolSession protocolSession) {

        if (log.isDebugEnabled()) {
            log.debug("Activating session {}", protocolSession);
        }

        protocolSession.setLastActivityTime(LocalDateTime.now());

        SESSION_CACHE.put(protocolSession.getId(), CompletableFuture.supplyAsync(() -> protocolSession, ThreadPoolConfiguration.JCPP_COMMON_THREAD_POOL));
    }

    private AsyncCache<UUID, ProtocolSession> buildCache() {
        return Caffeine.newBuilder()
                .initialCapacity(INIT_CACHE_LIMIT)
                .maximumSize(INIT_CACHE_LIMIT * 10)
                .executor(ThreadPoolConfiguration.JCPP_COMMON_THREAD_POOL)
                .buildAsync();
    }
}