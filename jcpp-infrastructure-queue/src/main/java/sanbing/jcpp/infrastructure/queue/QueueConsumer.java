/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.queue;



import sanbing.jcpp.infrastructure.queue.common.TopicPartitionInfo;

import java.util.List;
import java.util.Set;

public interface QueueConsumer<T extends QueueMsg> {

    String getTopic();

    void subscribe();

    void subscribe(Set<TopicPartitionInfo> partitions);

    void stop();

    void unsubscribe();

    List<T> poll(long durationInMillis);

    void commit();

    boolean isStopped();

    List<String> getFullTopicNames();

}
