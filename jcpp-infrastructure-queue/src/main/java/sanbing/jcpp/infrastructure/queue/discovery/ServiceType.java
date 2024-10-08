/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.queue.discovery;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ServiceType {

    APP("app"),
    PROTOCOL("protocol");

    private final String label;

    public static ServiceType of(String serviceType) {
        return ServiceType.valueOf(serviceType.toUpperCase());
    }

}
