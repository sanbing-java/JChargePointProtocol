/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.util.trace;

import org.apache.commons.lang3.StringUtils;

/**
 * Tracer上下文工具类
 */
public class TracerContextUtil {

    public static final String JCPP_TRACER_ID = "jcpp_tracer_id";
    public static final String JCPP_TRACER_ORIGIN = "jcpp_tracer_origin";
    public static final String JCPP_TRACER_TS = "jcpp_tracer_ts";

    private static final ThreadLocal<Tracer> TRACE_ID_CONTAINER = new ThreadLocal<>();

    public static Tracer newTracer(String traceId, String origin) {
        Tracer tracer;

        if (StringUtils.isEmpty(traceId)) {
            tracer = new Tracer(TraceIdGenerator.generate(), origin);
        } else {
            tracer = new Tracer(traceId, origin);
        }

        TRACE_ID_CONTAINER.set(tracer);

        return tracer;
    }

    public static Tracer newTracer(String traceId, String origin, long ts) {
        final Tracer tracer;

        if (StringUtils.isEmpty(traceId)) {
            tracer = new Tracer(TraceIdGenerator.generate(), origin, ts);
        } else {
            tracer = new Tracer(traceId, origin, ts);
        }


        TRACE_ID_CONTAINER.set(tracer);

        return tracer;
    }

    public static Tracer newTracer(String origin) {
        return newTracer(TraceIdGenerator.generate(), origin);
    }

    public static Tracer newTracer() {
        return newTracer(TraceIdGenerator.generate(), null);
    }

    public static Tracer getCurrentTracer() {
        Tracer tracer = TRACE_ID_CONTAINER.get();

        if (tracer == null) {
            return newTracer();
        }

        return tracer;
    }

    public static void cleanTracer() {
        TRACE_ID_CONTAINER.remove();
    }

}