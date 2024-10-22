/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.queue.common;

import static sanbing.jcpp.infrastructure.util.trace.TracerContextUtil.*;

/**
 * @author baigod
 */
public final class QueueConstants {

    public static final String MSG_MD_PREFIX = "jcpp_";

    public static final String MSG_MD_TRACER_ID = MSG_MD_PREFIX + JCPP_TRACER_ID;

    public static final String MSG_MD_TRACER_ORIGIN = MSG_MD_PREFIX + JCPP_TRACER_ORIGIN;

    public static final String MSG_MD_TRACER_TS = MSG_MD_PREFIX + JCPP_TRACER_TS;

}