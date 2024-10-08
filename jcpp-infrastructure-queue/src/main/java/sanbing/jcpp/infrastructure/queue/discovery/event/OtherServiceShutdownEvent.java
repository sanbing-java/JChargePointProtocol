/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.queue.discovery.event;

import lombok.Getter;

public class OtherServiceShutdownEvent extends JCPPApplicationEvent {

    @Getter
    private final String serviceId;

    public OtherServiceShutdownEvent(Object source, String serviceId) {
        super(source);
        this.serviceId = serviceId;
    }
}
