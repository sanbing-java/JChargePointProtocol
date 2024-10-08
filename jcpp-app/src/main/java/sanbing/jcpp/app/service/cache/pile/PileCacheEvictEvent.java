/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.app.service.cache.pile;

import lombok.Data;

import java.util.UUID;

@Data
public class PileCacheEvictEvent {

    private UUID pileId;
    private String pileCode;

}
