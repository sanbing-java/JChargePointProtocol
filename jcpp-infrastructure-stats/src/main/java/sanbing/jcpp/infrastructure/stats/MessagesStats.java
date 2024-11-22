/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.stats;

public interface MessagesStats {
    default void incrementTotal() {
        incrementTotal(1);
    }

    void incrementTotal(int amount);

    default void incrementSuccessful() {
        incrementSuccessful(1);
    }

    void incrementSuccessful(int amount);

    default void incrementFailed() {
        incrementFailed(1);
    }

    void incrementFailed(int amount);

    int getTotal();

    int getSuccessful();

    int getFailed();

    void reset();
}
