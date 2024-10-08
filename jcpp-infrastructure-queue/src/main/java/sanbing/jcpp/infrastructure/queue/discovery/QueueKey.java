/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.queue.discovery;

import lombok.Data;
import lombok.With;

@Data
public class QueueKey {
    public static final String MAIN_QUEUE_NAME = "Main";

    private final ServiceType type;
    @With
    private final String queueName;

    public QueueKey(ServiceType type, String queueName) {
        this.type = type;
        this.queueName = queueName;
    }

    public QueueKey(ServiceType type) {
        this.type = type;
        this.queueName = MAIN_QUEUE_NAME;
    }

    @Override
    public String toString() {
        return "QK(" + queueName + "," + type + ")";
    }

}
