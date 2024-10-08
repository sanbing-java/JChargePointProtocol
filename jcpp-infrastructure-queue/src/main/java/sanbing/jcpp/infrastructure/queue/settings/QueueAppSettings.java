/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.queue.settings;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Data
@Component
public class QueueAppSettings {

    @Value("${queue.app.topic}")
    private String topic;

    @Value("${queue.app.partitions}")
    private int partitions;

    @Value("${queue.app.decoder:protobuf}")
    private DecoderType decoder;

    public enum DecoderType {
        protobuf,
        json
    }
}
