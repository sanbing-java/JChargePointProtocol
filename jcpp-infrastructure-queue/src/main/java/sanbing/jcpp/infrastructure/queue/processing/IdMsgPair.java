/**
 * 开源代码，仅供学习和交流研究使用，商用请联系三丙
 * 微信：mohan_88888
 * 抖音：程序员三丙
 * 付费课程知识星球：https://t.zsxq.com/aKtXo
 */
package sanbing.jcpp.infrastructure.queue.processing;

import com.google.protobuf.GeneratedMessage;
import lombok.Getter;
import sanbing.jcpp.infrastructure.queue.ProtoQueueMsg;

import java.util.UUID;

public class IdMsgPair<T extends GeneratedMessage> {
    @Getter
    final UUID uuid;
    @Getter
    final ProtoQueueMsg<T> msg;

    public IdMsgPair(UUID uuid, ProtoQueueMsg<T> msg) {
        this.uuid = uuid;
        this.msg = msg;
    }
}
