/**
 * 开源代码，仅供学习和交流研究使用，商用请联系三丙
 * 微信：mohan_88888
 * 抖音：程序员三丙
 * 付费课程知识星球：https://t.zsxq.com/aKtXo
 */
package sanbing.jcpp.infrastructure.queue.discovery;

import com.google.common.hash.HashFunction;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import sanbing.jcpp.infrastructure.queue.common.TopicPartitionInfo;
import sanbing.jcpp.infrastructure.queue.discovery.event.PartitionChangeEvent;
import sanbing.jcpp.proto.gen.ClusterProto.ServiceInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static sanbing.jcpp.infrastructure.util.JCPPHashUtil.forName;
import static sanbing.jcpp.infrastructure.util.JCPPHashUtil.hash;

/**
 * @author baigod
 */
@Component
@Slf4j
@ConfigurationProperties("queue.partitions")
public class HashPartitionProvider implements PartitionProvider {

    @Value("${queue.app.topic}")
    private String appTopic;
    @Value("${queue.app.partitions:10}")
    private Integer appPartitions;
    @Value("${queue.partitions.hash_function_name:murmur3_128}")
    private String hashFunctionName;

    private final Map<QueueKey, String> partitionTopicsMap = new ConcurrentHashMap<>();
    private final Map<QueueKey, Integer> partitionSizesMap = new ConcurrentHashMap<>();

    private HashFunction hashFunction;

    protected Map<QueueKey, List<Integer>> myPartitions = new ConcurrentHashMap<>();

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @PostConstruct
    public void init() {
        this.hashFunction = forName(hashFunctionName);

        QueueKey appKey = new QueueKey(ServiceType.APP);
        partitionTopicsMap.put(appKey, appTopic);
        partitionSizesMap.put(appKey, appPartitions);
    }

    private TopicPartitionInfo resolve(QueueKey queueKey, int hash) {
        Integer partitionSize = partitionSizesMap.get(queueKey);
        if (partitionSize == null) {
            throw new IllegalStateException("Partitions info for queue " + queueKey + " is missing");
        }

        int partition = Math.abs(hash % partitionSize);

        return buildTopicPartitionInfo(queueKey, partition);
    }

    @Override
    public TopicPartitionInfo resolve(ServiceType serviceType, String queueName, UUID entityId) {
        QueueKey queueKey = getQueueKey(serviceType, queueName);
        return resolve(queueKey, hash(hashFunction, entityId));
    }

    @Override
    public TopicPartitionInfo resolve(ServiceType serviceType, String queueName, String pileCode) {
        QueueKey queueKey = getQueueKey(serviceType, queueName);
        return resolve(queueKey, hash(hashFunction, pileCode));
    }

    private QueueKey getQueueKey(ServiceType serviceType, String queueName) {
        QueueKey queueKey = new QueueKey(serviceType, queueName);
        if (!partitionSizesMap.containsKey(queueKey)) {
            queueKey = new QueueKey(serviceType);
        }
        return queueKey;
    }

    @Override
    public synchronized void recalculatePartitions(ServiceInfo currentService, List<ServiceInfo> otherServices) {
        log.info("Recalculating partitions");
        logServiceInfo(currentService);
        otherServices.forEach(this::logServiceInfo);

        Map<QueueKey, List<ServiceInfo>> queueServicesMap = new HashMap<>();
        addNode(currentService, queueServicesMap);
        for (ServiceInfo other : otherServices) {
            addNode(other, queueServicesMap);
        }
        queueServicesMap.values().forEach(list -> list.sort(Comparator.comparing(ServiceInfo::getServiceId)));

        final ConcurrentMap<QueueKey, List<Integer>> newPartitions = new ConcurrentHashMap<>();
        partitionSizesMap.forEach((queueKey, size) -> {
            for (int i = 0; i < size; i++) {
                try {
                    List<ServiceInfo> servers = queueServicesMap.get(queueKey);
                    ServiceInfo serviceInfo = servers == null || servers.isEmpty() ? null : servers.get(i % servers.size());
                    log.info("Server responsible for {}[{}] - {}", queueKey, i, serviceInfo != null ? serviceInfo.getServiceId() : "none");
                    if (currentService.equals(serviceInfo)) {
                        newPartitions.computeIfAbsent(queueKey, key -> new ArrayList<>()).add(i);
                    }
                } catch (Exception e) {
                    log.warn("Failed to resolve server responsible for {}[{}]", queueKey, i, e);
                }
            }
        });

        final Map<QueueKey, List<Integer>> oldPartitions = myPartitions;
        myPartitions = newPartitions;

        log.info("Current Server responsible partitions: {}", myPartitions);

        Map<QueueKey, Set<TopicPartitionInfo>> changedPartitionsMap = new HashMap<>();

        Set<QueueKey> removed = new HashSet<>();
        oldPartitions.forEach((queueKey, partitions) -> {
            if (!newPartitions.containsKey(queueKey)) {
                removed.add(queueKey);
            }
        });

        removed.forEach(queueKey -> {
            changedPartitionsMap.put(queueKey, Collections.emptySet());
        });

        myPartitions.forEach((queueKey, partitions) -> {
            if (!partitions.equals(oldPartitions.get(queueKey))) {
                Set<TopicPartitionInfo> tpiList = partitions.stream()
                        .map(partition -> buildTopicPartitionInfo(queueKey, partition))
                        .collect(Collectors.toSet());
                changedPartitionsMap.put(queueKey, tpiList);
            }
        });

        if (!changedPartitionsMap.isEmpty()) {
            Map<ServiceType, Map<QueueKey, Set<TopicPartitionInfo>>> partitionsByServiceType = new HashMap<>();
            changedPartitionsMap.forEach((queueKey, partitions) -> {
                partitionsByServiceType.computeIfAbsent(queueKey.getType(), serviceType -> new HashMap<>())
                        .put(queueKey, partitions);
            });
            partitionsByServiceType.forEach(this::publishPartitionChangeEvent);
        }

    }

    private void publishPartitionChangeEvent(ServiceType serviceType, Map<QueueKey, Set<TopicPartitionInfo>> partitionsMap) {
        log.info("Partitions changed: {}", System.lineSeparator() + partitionsMap.entrySet().stream()
                .map(entry -> "[" + entry.getKey() + "] - [" + entry.getValue().stream()
                        .map(tpi -> tpi.getPartition().orElse(-1).toString()).sorted()
                        .collect(Collectors.joining(", ")) + "]")
                .collect(Collectors.joining(System.lineSeparator())));
        PartitionChangeEvent event = new PartitionChangeEvent(this, serviceType, partitionsMap);
        try {
            applicationEventPublisher.publishEvent(event);
        } catch (Exception e) {
            log.error("Failed to publish partition change event {}", event, e);
        }
    }

    private void logServiceInfo(ServiceInfo server) {
        log.info("Found server: {}", server.getServiceId());
    }

    private void addNode(ServiceInfo instance, Map<QueueKey, List<ServiceInfo>> queueServiceList) {
        for (String serviceTypeStr : instance.getServiceTypesList()) {
            ServiceType serviceType = ServiceType.of(serviceTypeStr);
            if (ServiceType.APP.equals(serviceType)) {
                queueServiceList.computeIfAbsent(new QueueKey(serviceType), key -> new ArrayList<>()).add(instance);
            }
        }
    }

    private TopicPartitionInfo buildTopicPartitionInfo(QueueKey queueKey, int partition) {
        List<Integer> partitions = myPartitions.get(queueKey);
        return buildTopicPartitionInfo(queueKey, partition, partitions != null && partitions.contains(partition));
    }

    private TopicPartitionInfo buildTopicPartitionInfo(QueueKey queueKey, int partition, boolean myPartition) {
        return TopicPartitionInfo.builder()
                .topic(partitionTopicsMap.get(queueKey))
                .partition(partition)
                .myPartition(myPartition)
                .build();
    }

}