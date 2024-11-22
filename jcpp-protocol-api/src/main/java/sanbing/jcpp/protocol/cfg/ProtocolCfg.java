/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.cfg;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProtocolCfg {

    private boolean enabled;

    @NotNull
    @Valid
    private ListenerCfg listener;

    @NotNull
    @Valid
    private ForwarderCfg forwarder;
}