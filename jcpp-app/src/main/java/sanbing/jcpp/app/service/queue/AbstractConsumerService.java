/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.app.service.queue;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import sanbing.jcpp.infrastructure.queue.discovery.PartitionProvider;
import sanbing.jcpp.infrastructure.queue.discovery.event.JCPPApplicationEventListener;
import sanbing.jcpp.infrastructure.queue.discovery.event.PartitionChangeEvent;
import sanbing.jcpp.infrastructure.util.annotation.AfterStartUp;
import sanbing.jcpp.infrastructure.util.async.JCPPExecutors;
import sanbing.jcpp.infrastructure.util.async.JCPPThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractConsumerService extends JCPPApplicationEventListener<PartitionChangeEvent> {

    protected final PartitionProvider partitionProvider;
    protected final ApplicationEventPublisher eventPublisher;

    protected ExecutorService consumersExecutor;
    protected ExecutorService mgmtExecutor;
    protected ScheduledExecutorService scheduler;

    public void init(String prefix) {
        this.consumersExecutor = Executors.newCachedThreadPool(JCPPThreadFactory.forName(prefix + "-consumer"));
        this.mgmtExecutor = JCPPExecutors.newWorkStealingPool(getMgmtThreadPoolSize(), prefix + "-mgmt");
        this.scheduler = Executors.newSingleThreadScheduledExecutor(JCPPThreadFactory.forName(prefix + "-consumer-scheduler"));
    }

    @AfterStartUp(order = AfterStartUp.REGULAR_SERVICE)
    public void afterStartUp() {
        startConsumers();
    }

    protected void startConsumers() {
    }

    protected void stopConsumers() {
    }

    protected abstract int getMgmtThreadPoolSize();

    @PreDestroy
    public void destroy() {
        stopConsumers();
        if (consumersExecutor != null) {
            consumersExecutor.shutdownNow();
        }
        if (mgmtExecutor != null) {
            mgmtExecutor.shutdownNow();
        }
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }

}
