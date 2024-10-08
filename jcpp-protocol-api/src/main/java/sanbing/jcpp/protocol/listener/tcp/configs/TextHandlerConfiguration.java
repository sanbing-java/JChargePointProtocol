/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.listener.tcp.configs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import sanbing.jcpp.protocol.cfg.enums.TcpHandlerType;

import static sanbing.jcpp.protocol.cfg.enums.TcpHandlerType.TEXT;

@Data
@ToString
@EqualsAndHashCode
public class TextHandlerConfiguration implements HandlerConfiguration {
    public static final String SYSTEM_LINE_SEPARATOR = "SYSTEM_LINE_SEPARATOR";

    private int maxFrameLength;

    private boolean stripDelimiter;

    private String messageSeparator;

    private String charsetName;

    public TcpHandlerType getType() {
        return TEXT;
    }

}
