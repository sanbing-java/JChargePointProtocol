/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.queue.processing;

import lombok.Getter;
import sanbing.jcpp.infrastructure.queue.ProtoQueueMsg;

import java.util.UUID;

public class IdMsgPair<T extends com.google.protobuf.GeneratedMessageV3> {
    @Getter
    final UUID uuid;
    @Getter
    final ProtoQueueMsg<T> msg;

    public IdMsgPair(UUID uuid, ProtoQueueMsg<T> msg) {
        this.uuid = uuid;
        this.msg = msg;
    }
}
