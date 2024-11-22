/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import sanbing.jcpp.infrastructure.util.JCPPPair;

import java.io.Serializable;

public abstract class VersionedCaffeineCache<K extends VersionedCacheKey, V extends Serializable & HasVersion> extends CaffeineTransactionalCache<K, V> implements VersionedCache<K, V> {

    protected VersionedCaffeineCache(CacheManager cacheManager, String cacheName) {
        super(cacheManager, cacheName);
    }

    @Override
    public CacheValueWrapper<V> get(K key) {
        JCPPPair<Long, V> versionValuePair = doGet(key);
        if (versionValuePair != null) {
            return SimpleCacheValueWrapper.wrap(versionValuePair.getSecond());
        }
        return null;
    }

    @Override
    public void put(K key, V value) {
        Integer version = getVersion(value);
        if (version == null) {
            return;
        }
        doPut(key, value, version);
    }

    private void doPut(K key, V value, Integer version) {
        lock.lock();
        try {
            JCPPPair<Long, V> versionValuePair = doGet(key);
            if (versionValuePair == null || version > versionValuePair.getFirst()) {
                failAllTransactionsByKey(key);
                cache.put(key, wrapValue(value, version));
            }
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    private JCPPPair<Long, V> doGet(K key) {
        Cache.ValueWrapper source = cache.get(key);
        if (source != null && source.get() instanceof JCPPPair<?, ?> pair) {
            return (JCPPPair<Long, V>) pair;
        }
        return null;
    }

    @Override
    public void evict(K key) {
        lock.lock();
        try {
            failAllTransactionsByKey(key);
            cache.evict(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void evict(K key, Integer version) {
        if (version == null) {
            return;
        }
        doPut(key, null, version);
    }

    @Override
    void doPutIfAbsent(K key, V value) {
        cache.putIfAbsent(key, wrapValue(value, getVersion(value)));
    }

    private JCPPPair<Integer, V> wrapValue(V value, Integer version) {
        return JCPPPair.of(version, value);
    }

}
