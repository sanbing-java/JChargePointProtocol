/**
 * 开源代码，仅供学习和交流研究使用，商用请联系三丙
 * 微信：mohan_88888
 * 抖音：程序员三丙
 * 付费课程知识星球：https://t.zsxq.com/aKtXo
 */
package sanbing.jcpp.infrastructure.util.exception;

/**
 * @author baigod
 */
public class DownlinkException extends RuntimeException {

    public DownlinkException(String message) {
        super(message);
    }

    public DownlinkException(String message, Throwable cause) {
        super(message, cause);
    }
}