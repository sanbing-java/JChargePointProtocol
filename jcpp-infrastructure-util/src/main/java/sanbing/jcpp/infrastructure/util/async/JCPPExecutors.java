/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.util.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public class JCPPExecutors {

    public static ExecutorService newWorkStealingPool(int parallelism, String namePrefix) {
        return new ForkJoinPool(parallelism,
                new JCPPForkJoinWorkerThreadFactory(namePrefix),
                null, true);
    }

    public static ExecutorService newWorkStealingPool(int parallelism, Class<?> clazz) {
        return newWorkStealingPool(parallelism, clazz.getSimpleName());
    }

    public static ExecutorService newVirtualThreadPool(String namePrefix) {
        return Executors.newThreadPerTaskExecutor(new JCPPVirtualThreadFactory(namePrefix));
    }

}
