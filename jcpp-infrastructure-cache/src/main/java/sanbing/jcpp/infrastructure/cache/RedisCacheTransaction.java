/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;

import java.io.Serializable;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class RedisCacheTransaction<K extends Serializable, V extends Serializable> implements CacheTransaction<K, V> {

    private final RedisTransactionalCache<K, V> cache;
    private final RedisConnection connection;

    @Override
    public void put(K key, V value) {
        cache.put(key, value, connection);
    }

    @Override
    public boolean commit() {
        try {
            var execResult = connection.exec();
            return execResult.stream().anyMatch(Objects::nonNull);
        } finally {
            connection.close();
        }
    }

    @Override
    public void rollback() {
        try {
            connection.discard();
        } finally {
            connection.close();
        }
    }

}
