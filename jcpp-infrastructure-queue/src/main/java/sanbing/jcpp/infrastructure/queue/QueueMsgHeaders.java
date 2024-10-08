/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.queue;

import java.util.Map;

public interface QueueMsgHeaders {

    byte[] put(String key, byte[] value);

    byte[] get(String key);

    Map<String, byte[]> getData();
}
