/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.adapter.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import sanbing.jcpp.infrastructure.util.mdc.MDCUtils;
import sanbing.jcpp.infrastructure.util.trace.TracerContextUtil;

import static sanbing.jcpp.infrastructure.util.trace.TracerContextUtil.*;

@Component
public class TracerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tracerId = request.getHeader(JCPP_TRACER_ID);
        String tracerOrigin = request.getHeader(JCPP_TRACER_ORIGIN);
        String tracerTsStr = request.getHeader(JCPP_TRACER_TS);

        long tracerTs;
        if (tracerTsStr != null) {
            try {
                tracerTs = Long.parseLong(tracerTsStr);
            } catch (NumberFormatException e) {
                tracerTs = System.currentTimeMillis();
            }
        } else {
            tracerTs = System.currentTimeMillis();
        }

        TracerContextUtil.newTracer(tracerId, tracerOrigin, tracerTs);
        MDCUtils.recordTracer();

        return true;
    }
}