/**
 * 开源代码，仅供学习和交流研究使用，商用请联系三丙
 * 微信：mohan_88888
 * 抖音：程序员三丙
 * 付费课程知识星球：https://t.zsxq.com/aKtXo
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