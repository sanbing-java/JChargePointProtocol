/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.util.trace;

import lombok.Data;

import java.io.Serializable;

@Data
public class Tracer implements Serializable {

    private String traceId;

    private String origin;

    private final long tracerTs;

    public Tracer(String traceId, String origin) {
        this.traceId = traceId;
        this.origin = origin;
        this.tracerTs = System.currentTimeMillis();
    }

    public Tracer(String traceId, String origin, long tracerTs) {
        this.traceId = traceId;
        this.origin = origin;
        this.tracerTs = tracerTs;
    }

    public Tracer(String traceId, long tracerTs) {
        this.traceId = traceId;
        this.origin = "JCPP";
        this.tracerTs = tracerTs;

    }
}
