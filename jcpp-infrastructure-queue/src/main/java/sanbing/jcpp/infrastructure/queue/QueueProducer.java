/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.queue;


import sanbing.jcpp.infrastructure.queue.common.TopicPartitionInfo;

public interface QueueProducer<T extends QueueMsg> {

    void init();

    String getTopic();

    void send(TopicPartitionInfo tpi, T msg, QueueCallback callback);

    void stop();
}
