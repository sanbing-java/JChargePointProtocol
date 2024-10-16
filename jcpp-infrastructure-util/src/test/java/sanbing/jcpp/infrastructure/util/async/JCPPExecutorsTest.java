/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.util.async;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import sanbing.jcpp.infrastructure.util.mdc.MDCUtils;
import sanbing.jcpp.infrastructure.util.trace.TracerContextUtil;
import sanbing.jcpp.infrastructure.util.trace.TracerRunnable;

import java.util.concurrent.ExecutorService;

class JCPPExecutorsTest {

    @Test
    void newVirtualThreadPool() {
        ExecutorService executorService = JCPPExecutors.newVirtualThreadPool("test-consumer-virtual");

        TracerContextUtil.newTracer();
        MDCUtils.recordTracer();

        System.out.println(MDC.get("TRACE_ID"));

        executorService.submit(new TracerRunnable(() -> {
            System.out.println(MDC.get("TRACE_ID"));
        }));
    }
}