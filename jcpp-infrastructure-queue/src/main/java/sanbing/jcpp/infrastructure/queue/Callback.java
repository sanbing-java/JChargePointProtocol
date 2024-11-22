/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.queue;

public interface Callback {

    Callback EMPTY = new Callback() {

        @Override
        public void onSuccess() {

        }

        @Override
        public void onFailure(Throwable t) {

        }
    };

    void onSuccess();

    void onFailure(Throwable t);

}
