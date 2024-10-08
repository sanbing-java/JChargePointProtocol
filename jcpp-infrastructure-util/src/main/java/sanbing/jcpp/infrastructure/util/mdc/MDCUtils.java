/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.util.mdc;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import sanbing.jcpp.infrastructure.util.trace.Tracer;
import sanbing.jcpp.infrastructure.util.trace.TracerContextUtil;


public class MDCUtils {

    private static final String TRACE_ID = "TRACE_ID";

    public static String putIfAbsentTracer() {
        String traceId = MDC.get(TRACE_ID);

        if (StringUtils.isEmpty(traceId)) {
            return recordTracer();
        }

        return traceId;
    }

    public static String recordTracer() {
        Tracer tracer = TracerContextUtil.getCurrentTracer();

        if (!StringUtils.isEmpty(tracer.getTraceId())) {
            MDC.put(TRACE_ID, tracer.getTraceId());
        } else {
            MDC.remove(TRACE_ID);
        }

        return tracer.getTraceId();

    }

    public static void cleanTracer() {
        MDC.remove(TRACE_ID);
    }
}
