/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.stats;

import io.micrometer.core.instrument.Counter;

import java.util.concurrent.atomic.AtomicInteger;

public class DefaultCounter {
    private final AtomicInteger aiCounter;
    private final Counter micrometerCounter;

    public DefaultCounter(AtomicInteger aiCounter, Counter micrometerCounter) {
        this.aiCounter = aiCounter;
        this.micrometerCounter = micrometerCounter;
    }

    public void increment() {
        aiCounter.incrementAndGet();
        micrometerCounter.increment();
    }

    public void clear() {
        aiCounter.set(0);
    }

    public int get() {
        return aiCounter.get();
    }

    public void add(int delta){
        aiCounter.addAndGet(delta);
        micrometerCounter.increment(delta);
    }
}
