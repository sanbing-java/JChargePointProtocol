/**
 * 开源代码，仅供学习和交流研究使用，商用请联系三丙
 * 微信：mohan_88888
 * 抖音：程序员三丙
 * 付费课程知识星球：https://t.zsxq.com/aKtXo
 */
package sanbing.jcpp.app.service.cache.pile;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import sanbing.jcpp.app.dal.entity.Pile;
import sanbing.jcpp.infrastructure.cache.CacheConstants;
import sanbing.jcpp.infrastructure.cache.VersionedCaffeineCache;

@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "caffeine", matchIfMissing = true)
@Service("PileCache")
public class PileCaffeineCache extends VersionedCaffeineCache<PileCacheKey, Pile> {

    public PileCaffeineCache(CacheManager cacheManager) {
        super(cacheManager, CacheConstants.PILE_CACHE);
    }

}
