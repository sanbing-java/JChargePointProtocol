/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.cache;

import java.io.Serializable;

public interface VersionedCacheKey extends Serializable {

    default boolean isVersioned() {
        return false;
    }

}
