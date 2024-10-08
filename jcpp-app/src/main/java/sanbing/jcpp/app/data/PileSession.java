/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.app.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author baigod
 */
@Data
public class PileSession implements Serializable {

    private final UUID pileId;

    private final String pileCode;

    private final String protocolName;

    private UUID protocolSessionId;

    private String remoteAddress;

    private String nodeId;

    private String nodeWebapiIpPort;

    public PileSession(UUID pileId, String pileCode, String protocolName) {
        this.pileId = pileId;
        this.pileCode = pileCode;
        this.protocolName = protocolName;
    }

    @JsonCreator
    public PileSession(
            @JsonProperty("pileId") UUID pileId,
            @JsonProperty("pileCode") String pileCode,
            @JsonProperty("protocolName") String protocolName,
            @JsonProperty("protocolSessionId") UUID protocolSessionId,
            @JsonProperty("remoteAddress") String remoteAddress,
            @JsonProperty("nodeId") String nodeId,
            @JsonProperty("nodeWebapiIpPort") String nodeWebapiIpPort) {
        this.pileId = pileId;
        this.pileCode = pileCode;
        this.protocolName = protocolName;
        this.protocolSessionId = protocolSessionId;
        this.remoteAddress = remoteAddress;
        this.nodeId = nodeId;
        this.nodeWebapiIpPort = nodeWebapiIpPort;
    }
}