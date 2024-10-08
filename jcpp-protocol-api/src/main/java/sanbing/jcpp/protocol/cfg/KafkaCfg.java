/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.cfg;

import lombok.Getter;
import lombok.Setter;
import sanbing.jcpp.infrastructure.util.property.PropertyUtils;

import java.util.Map;

@Getter
@Setter
public class KafkaCfg {

    private String topic;

    private boolean jcppPartition;

    private String bootstrapServers;

    private String acks;

    private EncoderType encoder;

    private int retries;

    private String compressionType; // none, gzip, snappy, lz4, zstd

    private int batchSize;

    private int lingerMs;

    private long bufferMemory;

    private Map<String, String> otherProperties; // Other inline properties if necessary

    private String topicProperties;

    public void setOtherProperties(String otherProperties) {
        this.otherProperties = PropertyUtils.getProps(otherProperties);
    }

    public enum EncoderType {
        protobuf,
        json
    }
}