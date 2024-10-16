/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.util.async;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class JCPPVirtualThreadFactory implements ThreadFactory {
    private final String namePrefix;
    private final AtomicLong threadNumber = new AtomicLong(1);

    public JCPPVirtualThreadFactory(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        return Thread.ofVirtual().name(namePrefix + "-" + threadNumber.getAndIncrement()).unstarted(r);
    }
}