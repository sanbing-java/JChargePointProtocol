/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.stats;

import io.micrometer.core.instrument.Counter;

import java.util.concurrent.atomic.AtomicInteger;

public class StatsCounter extends DefaultCounter {
    private final String name;

    public StatsCounter(AtomicInteger aiCounter, Counter micrometerCounter, String name) {
        super(aiCounter, micrometerCounter);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
