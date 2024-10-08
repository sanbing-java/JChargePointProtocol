/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.queue;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class KafkaQueueMsg implements QueueMsg {
    private final String key;
    private final QueueMsgHeaders headers;
    private final byte[] data;

    public KafkaQueueMsg(ConsumerRecord<String, byte[]> record) {
        this.key = record.key();
        QueueMsgHeaders headers = new DefaultQueueMsgHeaders();
        record.headers().forEach(header -> {
            headers.put(header.key(), header.value());
        });
        this.headers = headers;
        this.data = record.value();
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public QueueMsgHeaders getHeaders() {
        return headers;
    }

    @Override
    public byte[] getData() {
        return data;
    }
}
