/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.queue;

public interface QueueAdmin {

    default void createTopicIfNotExists(String topic) {
        createTopicIfNotExists(topic, null);
    }

    void createTopicIfNotExists(String topic, String properties);

    void destroy();

}
