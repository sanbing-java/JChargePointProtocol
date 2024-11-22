/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.app.repository;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.event.TransactionalEventListener;
import sanbing.jcpp.app.dal.entity.Pile;
import sanbing.jcpp.app.dal.mapper.PileMapper;
import sanbing.jcpp.app.service.cache.pile.PileCacheEvictEvent;
import sanbing.jcpp.app.service.cache.pile.PileCacheKey;

import java.util.ArrayList;
import java.util.List;

import static sanbing.jcpp.infrastructure.util.validation.Validator.validateString;

/**
 * @author baigod
 */
@Repository
@Slf4j
public class PileRepositoryImpl extends CachedVersionedEntityRepository<PileCacheKey, Pile, PileCacheEvictEvent> implements PileRepository {

    @Resource
    PileMapper pileMapper;

    @TransactionalEventListener(classes = PileCacheEvictEvent.class)
    @Override
    public void handleEvictEvent(PileCacheEvictEvent event) {
        // 如果修改或删除充电桩，需要在这里消费删除事件
        List<PileCacheKey> toEvict = new ArrayList<>(3);
        toEvict.add(new PileCacheKey(event.getPileId()));
        toEvict.add(new PileCacheKey(event.getPileCode()));
        cache.evict(toEvict);
    }

    @Override
    public Pile findPileByCode(String pileCode) {
        validateString(pileCode, code -> "无效的桩编号" + pileCode);
        return cache.get(new PileCacheKey(pileCode),
                () -> pileMapper.selectByCode(pileCode));
    }
}