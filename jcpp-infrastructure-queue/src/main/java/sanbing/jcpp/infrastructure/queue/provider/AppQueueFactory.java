/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.queue.provider;


import sanbing.jcpp.infrastructure.queue.ProtoQueueMsg;
import sanbing.jcpp.infrastructure.queue.QueueConsumer;
import sanbing.jcpp.infrastructure.queue.QueueProducer;
import sanbing.jcpp.proto.gen.ProtocolProto.UplinkQueueMessage;

public interface AppQueueFactory {

    QueueConsumer<ProtoQueueMsg<UplinkQueueMessage>> createProtocolUplinkMsgConsumer();

    QueueProducer<ProtoQueueMsg<UplinkQueueMessage>> createProtocolUplinkMsgProducer(String topic);
}
