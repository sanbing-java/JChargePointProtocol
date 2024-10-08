/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.stats;

import io.micrometer.core.instrument.Timer;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

public class StatsTimer {

    @Getter
    private final String name;
    private final Timer timer;

    private int count;
    private long totalTime;

    public StatsTimer(String name, Timer micrometerTimer) {
        this.name = name;
        this.timer = micrometerTimer;
    }

    public  void record(long timeMs) {
        count++;
        totalTime += timeMs;
        timer.record(timeMs, TimeUnit.MILLISECONDS);
    }

    public double getAvg() {
        if (count == 0) {
            return 0.0;
        }
        return (double) totalTime / count;
    }

    public  void reset() {
        count = 0;
        totalTime = 0;
    }

}
