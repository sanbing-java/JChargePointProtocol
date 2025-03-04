/**
 * 开源代码，仅供学习和交流研究使用，商用请联系三丙
 * 微信：mohan_88888
 * 抖音：程序员三丙
 * 付费课程知识星球：https://t.zsxq.com/aKtXo
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
