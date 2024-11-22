/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.cache;

import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.lang.Nullable;

public interface JCPPRedisSerializer<K, T> {

    @Nullable
    byte[] serialize(@Nullable T t) throws SerializationException;

    @Nullable
    T deserialize(K key, @Nullable byte[] bytes) throws SerializationException;

}
