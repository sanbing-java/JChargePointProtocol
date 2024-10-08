/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.queue.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.kafka.clients.producer.RecordMetadata;
import sanbing.jcpp.infrastructure.queue.QueueMsgMetadata;

@Data
@AllArgsConstructor
public class KafkaQueueMsgMetadata implements QueueMsgMetadata {

    private RecordMetadata metadata;
}
