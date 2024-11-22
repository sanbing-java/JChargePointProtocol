/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.listener.tcp.configs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import sanbing.jcpp.protocol.cfg.enums.TcpHandlerType;

import static sanbing.jcpp.protocol.cfg.enums.TcpHandlerType.JSON;

@Data
@ToString
@EqualsAndHashCode
public class JsonHandlerConfiguration implements HandlerConfiguration {
    public TcpHandlerType getType() {
        return JSON;
    }

}
