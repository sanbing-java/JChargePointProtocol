/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.domain;

/**
 * @author baigod
 */
public enum SessionCloseReason {
    /**
     * 自然销毁
     */
    DESTRUCTION,

    /**
     * 失活
     */
    INACTIVE,

    /**
     * 手动销毁
     */
    MANUALLY
}