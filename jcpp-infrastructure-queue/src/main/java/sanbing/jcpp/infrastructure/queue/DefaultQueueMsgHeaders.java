/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.queue;


import java.util.HashMap;
import java.util.Map;

public class DefaultQueueMsgHeaders implements QueueMsgHeaders {

    protected final Map<String, byte[]> data = new HashMap<>();

    @Override
    public byte[] put(String key, byte[] value) {
        return data.put(key, value);
    }

    @Override
    public byte[] get(String key) {
        return data.get(key);
    }

    @Override
    public Map<String, byte[]> getData() {
        return data;
    }
}
