/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.app.repository;

import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.Serializable;

public abstract class AbstractCachedEntityRepository<K extends Serializable, V extends Serializable, E> extends AbstractEntityRepository {

    protected void publishEvictEvent(E event) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            eventPublisher.publishEvent(event);
        } else {
            handleEvictEvent(event);
        }
    }

    public abstract void handleEvictEvent(E event);

}
