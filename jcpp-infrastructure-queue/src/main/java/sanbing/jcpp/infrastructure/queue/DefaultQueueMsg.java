/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.queue;

import lombok.Data;

@Data
public class DefaultQueueMsg implements QueueMsg {
    private final String key;
    private final byte[] data;
    private final DefaultQueueMsgHeaders headers;

    public DefaultQueueMsg(QueueMsg msg) {
        this.key = msg.getKey();
        this.data = msg.getData();
        DefaultQueueMsgHeaders headers = new DefaultQueueMsgHeaders();
        msg.getHeaders().getData().forEach(headers::put);
        this.headers = headers;
    }

}
