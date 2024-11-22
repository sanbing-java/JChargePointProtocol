/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.cfg;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TcpCfg {

    private String bindAddress;

    @Max(65000)
    private int bindPort;

    @Min(1)
    private int bossGroupThreadCount;

    @Min(1)
    private int workerGroupThreadCount;

    private boolean soKeepAlive;

    @Min(1)
    @Max(65500)
    private int soBacklog;

    @Min(1)
    private int soRcvbuf;

    @Min(1)
    private int soSndbuf;

    private boolean nodelay;

    @Valid
    private TcpHandlerCfg handler;

}