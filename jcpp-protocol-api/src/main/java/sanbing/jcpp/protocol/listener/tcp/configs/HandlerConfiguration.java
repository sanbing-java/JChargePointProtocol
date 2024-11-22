/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.listener.tcp.configs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import sanbing.jcpp.protocol.cfg.enums.TcpHandlerType;

@JsonTypeInfo(
        use = Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @Type(
                value = TextHandlerConfiguration.class,
                name = "TEXT"
        ),
        @Type(
                value = BinaryHandlerConfiguration.class,
                name = "BINARY"
        ),
        @Type(
                value = JsonHandlerConfiguration.class,
                name = "JSON"
        )
})
@JsonIgnoreProperties(
        ignoreUnknown = true
)
public interface HandlerConfiguration {
    TcpHandlerType getType();
}
