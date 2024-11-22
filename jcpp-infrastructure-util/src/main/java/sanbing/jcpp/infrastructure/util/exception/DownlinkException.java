/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
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