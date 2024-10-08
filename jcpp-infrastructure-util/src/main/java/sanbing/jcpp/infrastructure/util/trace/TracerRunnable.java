/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.util.trace;


import sanbing.jcpp.infrastructure.util.mdc.MDCUtils;

public class TracerRunnable implements Runnable {

    private Tracer tracer;
    private final Runnable runnable;

    public TracerRunnable(Runnable runnable) {
        this.tracer = TracerContextUtil.getCurrentTracer();
        this.runnable = runnable;
    }

    @Override
    public void run() {
        try {
            if (this.tracer != null) {
                TracerContextUtil.newTracer(tracer.getTraceId(), tracer.getOrigin(), tracer.getTracerTs());

                MDCUtils.recordTracer();
            }

            this.runnable.run();
        } finally {
            TracerContextUtil.cleanTracer();

            MDCUtils.cleanTracer();

            this.tracer = null;
        }
    }
}
