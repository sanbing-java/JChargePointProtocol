/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.cache;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.cache.Cache;

@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleCacheValueWrapper<T> implements CacheValueWrapper<T> {

    private final T value;

    @Override
    public T get() {
        return value;
    }

    public static <T> SimpleCacheValueWrapper<T> empty() {
        return new SimpleCacheValueWrapper<>(null);
    }

    public static <T> SimpleCacheValueWrapper<T> wrap(T value) {
        return new SimpleCacheValueWrapper<>(value);
    }

    @SuppressWarnings("unchecked")
    public static <T> SimpleCacheValueWrapper<T> wrap(Cache.ValueWrapper source) {
        return source == null ? null : new SimpleCacheValueWrapper<>((T) source.get());
    }
}
