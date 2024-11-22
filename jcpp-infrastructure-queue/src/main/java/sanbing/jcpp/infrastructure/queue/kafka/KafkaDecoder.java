/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.queue.kafka;


import sanbing.jcpp.infrastructure.queue.QueueMsg;

import java.io.IOException;

public interface KafkaDecoder<T> {

    T decode(QueueMsg msg) throws IOException;

}
