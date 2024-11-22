/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.queue;

public interface QueueMsg {

    String getKey();

    QueueMsgHeaders getHeaders();

    byte[] getData();
}
