/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.queue.memory;

import lombok.Data;
import sanbing.jcpp.infrastructure.queue.QueueCallback;
import sanbing.jcpp.infrastructure.queue.QueueMsg;
import sanbing.jcpp.infrastructure.queue.QueueProducer;
import sanbing.jcpp.infrastructure.queue.common.TopicPartitionInfo;

@Data
public class InMemoryQueueProducer<T extends QueueMsg> implements QueueProducer<T> {
    private final InMemoryStorage storage;

    private final String topic;

    public InMemoryQueueProducer(InMemoryStorage storage, String topic) {
        this.storage = storage;
        this.topic = topic;
    }

    @Override
    public void init() {

    }

    @Override
    public void send(TopicPartitionInfo tpi, T msg, QueueCallback callback) {
        boolean result = storage.put(tpi.getFullTopicName(), msg);
        if (result) {
            if (callback != null) {
                callback.onSuccess(null);
            }
        } else {
            if (callback != null) {
                callback.onFailure(new RuntimeException("Failure add msg to InMemoryQueue"));
            }
        }
    }

    @Override
    public void stop() {

    }

}
