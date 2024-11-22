/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.app.service.cache.session;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import sanbing.jcpp.app.data.PileSession;
import sanbing.jcpp.infrastructure.cache.CacheConstants;
import sanbing.jcpp.infrastructure.cache.CaffeineTransactionalCache;

/**
 * @author baigod
 */
@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "caffeine", matchIfMissing = true)
@Service("PileSessionCache")
public class PileSessionCaffeineCache  extends CaffeineTransactionalCache<PileSessionCacheKey, PileSession> {

    public PileSessionCaffeineCache(CacheManager cacheManager) {
        super(cacheManager, CacheConstants.PILE_SESSION_CACHE);
    }
}