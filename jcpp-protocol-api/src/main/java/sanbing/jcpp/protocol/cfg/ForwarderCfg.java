/**
 * 开源代码，仅供学习和交流研究使用，商用请联系三丙
 * 微信：mohan_88888
 * 抖音：程序员三丙
 * 付费课程知识星球：https://t.zsxq.com/aKtXo
 */
package sanbing.jcpp.protocol.cfg;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import sanbing.jcpp.protocol.cfg.enums.ForwarderType;

@Setter
@Getter
public class ForwarderCfg {

    @NotNull
    private ForwarderType type;

    private MemoryCfg memory;

    @Valid
    private KafkaCfg kafka;
}