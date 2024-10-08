/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.queue.discovery.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class JCPPApplicationEventListener<T extends JCPPApplicationEvent> implements ApplicationListener<T> {

    private int lastProcessedSequenceNumber = Integer.MIN_VALUE;
    private final Lock seqNumberLock = new ReentrantLock();

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onApplicationEvent(T event) {
        if (!filterApplicationEvent(event)) {
            log.trace("Skipping event due to filter: {}", event);
            return;
        }
        boolean validUpdate = false;
        seqNumberLock.lock();
        try {
            if (event.getSequenceNumber() > lastProcessedSequenceNumber) {
                validUpdate = true;
                lastProcessedSequenceNumber = event.getSequenceNumber();
            }
        } finally {
            seqNumberLock.unlock();
        }
        if (validUpdate) {
            try {
                onJCPPApplicationEvent(event);
            } catch (Exception e) {
                log.error("Failed to handle partition change event: {}", event, e);
            }
        } else {
            log.info("Application event ignored due to invalid sequence number ({} > {}). Event: {}", lastProcessedSequenceNumber, event.getSequenceNumber(), event);
        }
    }

    protected abstract void onJCPPApplicationEvent(T event);

    protected boolean filterApplicationEvent(T event) {
        return true;
    }

}
