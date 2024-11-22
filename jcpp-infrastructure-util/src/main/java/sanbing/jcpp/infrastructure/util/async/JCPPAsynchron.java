/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.util.async;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class JCPPAsynchron {

    public static <T> void withCallback(ListenableFuture<T> future, Consumer<T> onSuccess,
                                        Consumer<Throwable> onFailure) {
        withCallback(future, onSuccess, onFailure, null);
    }

    public static <T> void withCallback(ListenableFuture<T> future, Consumer<T> onSuccess,
                                        Consumer<Throwable> onFailure, Executor executor) {
        FutureCallback<T> callback = new FutureCallback<>() {
            @Override
            public void onSuccess(T result) {
                try {
                    onSuccess.accept(result);
                } catch (Throwable th) {
                    onFailure(th);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                onFailure.accept(t);
            }
        };
        Futures.addCallback(future, callback, Objects.requireNonNullElseGet(executor, MoreExecutors::directExecutor));
    }

    public static <T> ListenableFuture<T> submit(Callable<T> task, Consumer<T> onSuccess, Consumer<Throwable> onFailure, Executor executor) {
        return submit(task, onSuccess, onFailure, executor, null);
    }

    public static <T> ListenableFuture<T> submit(Callable<T> task, Consumer<T> onSuccess, Consumer<Throwable> onFailure, Executor executor, Executor callbackExecutor) {
        ListenableFuture<T> future = Futures.submit(task, executor);
        withCallback(future, onSuccess, onFailure, callbackExecutor);
        return future;
    }

}
