/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.app.service.cache.session;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author baigod
 */
@Getter
@EqualsAndHashCode
@Builder
public class PileSessionCacheKey implements Serializable {

    private final String pileCode;

    public PileSessionCacheKey(String pileCode) {
        this.pileCode = pileCode;
    }

    @Override
    public String toString() {
        return pileCode;
    }

}