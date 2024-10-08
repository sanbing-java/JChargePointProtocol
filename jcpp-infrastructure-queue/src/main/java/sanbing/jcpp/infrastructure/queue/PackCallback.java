/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.queue;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class PackCallback<T> implements Callback {
    private final PackProcessingContext<T> ctx;
    private final UUID id;

    public PackCallback(UUID id, PackProcessingContext<T> ctx) {
        this.id = id;
        this.ctx = ctx;
    }

    @Override
    public void onSuccess() {
        log.trace("[{}] ON SUCCESS", id);
        ctx.onSuccess(id);
    }

    @Override
    public void onFailure(Throwable t) {
        log.trace("[{}] ON FAILURE", id, t);
        ctx.onFailure(id, t);
    }
}
