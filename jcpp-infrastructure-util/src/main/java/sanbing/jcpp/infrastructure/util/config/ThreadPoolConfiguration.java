/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.util.config;

import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;
import sanbing.jcpp.infrastructure.util.async.JCPPExecutors;
import sanbing.jcpp.infrastructure.util.async.JCPPThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author baigod
 */
@Configuration
public class ThreadPoolConfiguration {

    public static final ExecutorService JCPP_COMMON_THREAD_POOL = JCPPExecutors.newVirtualThreadPool("jcpp-common-virtual");

    public static final ScheduledExecutorService PROTOCOL_SESSION_SCHEDULED = Executors.newSingleThreadScheduledExecutor(JCPPThreadFactory.forName("protocol-session-schedule"));

    @PreDestroy
    public void destroy() {
        PROTOCOL_SESSION_SCHEDULED.shutdownNow();

        JCPP_COMMON_THREAD_POOL.shutdown();

        try {
            if (!JCPP_COMMON_THREAD_POOL.awaitTermination(5, TimeUnit.SECONDS)) {
                JCPP_COMMON_THREAD_POOL.shutdownNow();
            }
        } catch (InterruptedException e) {
            JCPP_COMMON_THREAD_POOL.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

}