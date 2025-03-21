/**
 * 开源代码，仅供学习和交流研究使用，商用请联系三丙
 * 微信：mohan_88888
 * 抖音：程序员三丙
 * 付费课程知识星球：https://t.zsxq.com/aKtXo
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