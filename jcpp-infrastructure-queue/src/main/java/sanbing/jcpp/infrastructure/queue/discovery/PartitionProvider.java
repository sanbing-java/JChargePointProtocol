/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.queue.discovery;


import sanbing.jcpp.infrastructure.queue.common.TopicPartitionInfo;
import sanbing.jcpp.proto.gen.ClusterProto.ServiceInfo;

import java.util.List;
import java.util.UUID;

public interface PartitionProvider {

    TopicPartitionInfo resolve(ServiceType serviceType,String queueName, UUID entityId);

    TopicPartitionInfo resolve(ServiceType serviceType,String queueName, String pileCode);

    void recalculatePartitions(ServiceInfo currentService, List<ServiceInfo> otherServices);

}
