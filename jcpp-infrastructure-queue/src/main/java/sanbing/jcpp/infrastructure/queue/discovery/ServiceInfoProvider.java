/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.queue.discovery;


import sanbing.jcpp.proto.gen.ClusterProto;

/**
 * @author baigod
 */
public interface ServiceInfoProvider {
    String getServiceId();

    String getServiceWebapiEndpoint();

    String getServiceType();

    boolean isMonolith();

    ClusterProto.ServiceInfo getServiceInfo();

    ClusterProto.ServiceInfo generateNewServiceInfoWithCurrentSystemInfo();

}