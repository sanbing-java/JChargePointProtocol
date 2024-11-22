/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.app.service.queue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import sanbing.jcpp.infrastructure.queue.common.QueueConfig;
import sanbing.jcpp.infrastructure.queue.common.TopicPartitionInfo;

import java.util.Set;

@Getter
@ToString
@AllArgsConstructor
public class QueueConsumerManagerTask {

    private final QueueEvent event;
    private QueueConfig config;
    private Set<TopicPartitionInfo> partitions;
    private boolean drainQueue;

    public static QueueConsumerManagerTask delete(boolean drainQueue) {
        return new QueueConsumerManagerTask(QueueEvent.DELETE, null, null, drainQueue);
    }

    public static QueueConsumerManagerTask configUpdate(QueueConfig config) {
        return new QueueConsumerManagerTask(QueueEvent.CONFIG_UPDATE, config, null, false);
    }

    public static QueueConsumerManagerTask partitionChange(Set<TopicPartitionInfo> partitions) {
        return new QueueConsumerManagerTask(QueueEvent.PARTITION_CHANGE, null, partitions, false);
    }

}
