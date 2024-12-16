/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.queue.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sanbing.jcpp.infrastructure.queue.QueueMsg;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@Slf4j
public final class DefaultInMemoryStorage implements InMemoryStorage {
    private final ConcurrentHashMap<String, BlockingQueue<QueueMsg>> storage = new ConcurrentHashMap<>();

    @Value("${queue.memory.queue-capacity:100000}")
    private int queueCapacity;
    @Value("${queue.memory.max-pool-size:999}")
    private int maxPoolSize;

    @Override
    public void printStats() {
        storage.forEach((topic, queue) -> {
            if (!queue.isEmpty()) {
                log.info("[{}] Memory Queue Size [{}]", topic, queue.size());
            }
        });
    }

    @Override
    public int getLagTotal() {
        return storage.values().stream().map(BlockingQueue::size).reduce(0, Integer::sum);
    }

    @Override
    public int getLag(String topic) {
        return Optional.ofNullable(storage.get(topic)).map(Collection::size).orElse(0);
    }

    @Override
    public boolean put(String topic, QueueMsg msg) {
        return storage.computeIfAbsent(topic, t -> new LinkedBlockingQueue<>(queueCapacity)).add(msg);
    }

    @Override
    public List<QueueMsg> get(String topic) throws InterruptedException {
        final BlockingQueue<QueueMsg> queue = storage.get(topic);
        if (queue != null) {
            final QueueMsg firstMsg = queue.poll();
            if (firstMsg != null) {
                final int queueSize = queue.size();
                if (queueSize > 0) {
                    final List<QueueMsg> entities = new ArrayList<>(Math.min(queueSize, maxPoolSize) + 1);
                    entities.add(firstMsg);
                    queue.drainTo(entities, maxPoolSize);
                    return entities;
                }
                return Collections.singletonList(firstMsg);
            }
        }
        return Collections.emptyList();
    }

}
