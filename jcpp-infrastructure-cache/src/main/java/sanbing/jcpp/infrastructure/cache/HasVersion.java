/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.cache;

public interface HasVersion {

    Integer getVersion();

    default void setVersion(Integer version) {
    }

}
