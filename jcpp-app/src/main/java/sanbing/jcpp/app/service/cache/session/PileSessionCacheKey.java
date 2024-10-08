/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.app.service.cache.session;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

/**
 * @author baigod
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
@Builder
public class PileSessionCacheKey implements Serializable {

    private final UUID pileId;
    private final String pileCode;

    public PileSessionCacheKey(UUID pileId) {
        this(pileId, null);
    }

    public PileSessionCacheKey(String pileCode) {
        this(null, pileCode);
    }

    @Override
    public String toString() {
        return Optional.ofNullable(pileId).map(UUID::toString).orElse(pileCode);
    }

}