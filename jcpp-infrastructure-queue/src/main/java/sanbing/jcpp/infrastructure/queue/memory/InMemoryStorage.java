/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.queue.memory;


import sanbing.jcpp.infrastructure.queue.QueueMsg;

import java.util.List;

public interface InMemoryStorage {

    void printStats();

    int getLagTotal();

    int getLag(String topic);

    boolean put(String topic, QueueMsg msg);

    List<QueueMsg> get(String topic) throws InterruptedException;

}
