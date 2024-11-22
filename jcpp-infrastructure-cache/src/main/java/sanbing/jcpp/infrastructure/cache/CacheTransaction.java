/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.cache;

public interface CacheTransaction<K, V> {

    void put(K key, V value);

    boolean commit();

    void rollback();

}
