/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.util.config;

import com.google.common.hash.HashFunction;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sanbing.jcpp.infrastructure.util.async.JCPPThreadFactory;
import sanbing.jcpp.infrastructure.util.trace.TracerRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static sanbing.jcpp.infrastructure.util.JCPPHashUtil.forName;
import static sanbing.jcpp.infrastructure.util.JCPPHashUtil.hash;

/**
 * @author baigod
 */
@Component
@Slf4j
public class ShardingThreadPool {
    @Value("${thread-pool.sharding.hash_function_name:murmur3_128}")
    private String hashFunctionName;

    @Value("${thread-pool.sharding.parallelism:8}")
    private int parallelism;

    private HashFunction hashFunction;

    private final Map<Integer, ExecutorService> EXECUTOR_SERVICE_MAP = new ConcurrentHashMap<>(8);

    @PostConstruct
    public void init() {
        this.hashFunction = forName(hashFunctionName);
    }

    @PreDestroy
    public void destroy() {
        for (ExecutorService executorService : EXECUTOR_SERVICE_MAP.values()) {
            executorService.shutdownNow();
            log.info("Sharding Thread [{}] Shutdown completed.", executorService);
        }
    }

    @Scheduled(fixedDelayString = "${thread-pool.sharding.stats-print-interval-ms:10000}")
    public void printStats() {
        EXECUTOR_SERVICE_MAP.forEach((k, v) -> {

            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) v;

            int size = threadPoolExecutor.getQueue().size();

            if (size > 1) {
                log.info("分区 {} 的线程池中剩余 {} 条待执行任务,当前正在执行的线程数 {}, 已完成任务 {} / {}",
                        k,
                        size,
                        threadPoolExecutor.getActiveCount(),
                        threadPoolExecutor.getCompletedTaskCount(),
                        threadPoolExecutor.getTaskCount());
            }
        });
    }

    /**
     * 提交分片任务
     */
    public void execute(UUID hashKey, TracerRunnable runnable) {
        int partition = hash(hashFunction, hashKey);

        EXECUTOR_SERVICE_MAP.computeIfAbsent(partition % parallelism,
                        p -> Executors.newFixedThreadPool(1, JCPPThreadFactory.forName("sharding-threads" + p)))
                .execute(runnable);
    }
}