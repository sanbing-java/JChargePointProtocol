/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.queue.discovery.event;

import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.util.concurrent.atomic.AtomicInteger;

@ToString
public class JCPPApplicationEvent extends ApplicationEvent {

    private static final AtomicInteger sequence = new AtomicInteger();

    @Getter
    private final int sequenceNumber;

    public JCPPApplicationEvent(Object source) {
        super(source);
        sequenceNumber = sequence.incrementAndGet();
    }

}
