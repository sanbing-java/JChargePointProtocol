/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.app.repository;

import jakarta.annotation.Resource;
import sanbing.jcpp.infrastructure.cache.HasVersion;
import sanbing.jcpp.infrastructure.cache.VersionedCache;
import sanbing.jcpp.infrastructure.cache.VersionedCacheKey;

import java.io.Serializable;

public abstract class CachedVersionedEntityRepository<K extends VersionedCacheKey, V extends Serializable & HasVersion, E> extends AbstractCachedEntityRepository<K, V, E> {

    @Resource
    protected VersionedCache<K, V> cache;

}
