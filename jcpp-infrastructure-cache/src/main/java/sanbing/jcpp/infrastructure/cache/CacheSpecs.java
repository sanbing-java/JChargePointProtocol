/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.cache;

import lombok.Data;

@Data
public class CacheSpecs {
    private Integer timeToLiveInMinutes;
    private Integer maxSize;
}
