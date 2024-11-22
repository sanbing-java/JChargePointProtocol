/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.queue.kafka;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import sanbing.jcpp.infrastructure.util.property.PropertyUtils;

import java.util.Map;

@Component
@ConditionalOnProperty(prefix = "queue", value = "type", havingValue = "kafka")
public class KafkaTopicConfigs {
    public static final String NUM_PARTITIONS_SETTING = "partitions";

    @Value("${queue.kafka.topic-properties.app:}")
    private String appProperties;

    @Getter
    private Map<String, String> appConfigs;

    @PostConstruct
    private void init() {
        this.appConfigs = PropertyUtils.getProps(appProperties);
    }

}
