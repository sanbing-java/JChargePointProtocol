/**
 * 开源代码，仅供学习和交流研究使用，商用请联系三丙
 * 微信：mohan_88888
 * 抖音：程序员三丙
 * 付费课程知识星球：https://t.zsxq.com/aKtXo
 */
package sanbing.jcpp.app.service.cache.pile;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sanbing.jcpp.infrastructure.cache.VersionedCacheKey;

import java.io.Serial;
import java.util.Optional;
import java.util.UUID;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
@Builder
public class PileCacheKey implements VersionedCacheKey {

    @Serial
    private static final long serialVersionUID = 6366389552842340207L;

    private final UUID pileId;
    private final String pileCode;

    public PileCacheKey(UUID pileId) {
        this(pileId, null);
    }

    public PileCacheKey(String pileCode) {
        this(null, pileCode);
    }

    @Override
    public String toString() {
        return Optional.ofNullable(pileId).map(UUID::toString).orElse(pileCode);
    }

    @Override
    public boolean isVersioned() {
        return pileId != null;
    }

}
