/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.app.service;

import sanbing.jcpp.infrastructure.queue.Callback;
import sanbing.jcpp.proto.gen.ProtocolProto.UplinkQueueMessage;

/**
 * @author baigod
 */
public interface PileProtocolService {
    /**
     * 桩登录
     */
    void pileLogin(UplinkQueueMessage uplinkQueueMessage, Callback callback);

    /**
     * 充电桩心跳
     */
    void heartBeat(UplinkQueueMessage uplinkQueueMessage, Callback callback);

    /**
     * 校验计费模型
     */
    void verifyPricing(UplinkQueueMessage uplinkQueueMessage, Callback callback);

    /**
     * 查询计费策略
     */
    void queryPricing(UplinkQueueMessage uplinkQueueMessage, Callback callback);

    /**
     * 上报电桩运行状态
     */
    void postGunRunStatus(UplinkQueueMessage uplinkQueueMessage, Callback callback);

    /**
     * 上报充电进度
     */
    void postChargingProgress(UplinkQueueMessage uplinkQueueMessage, Callback callback);

    /**
     * 费率下发反馈
     */
    void onSetPricingResponse(UplinkQueueMessage uplinkQueueMessage, Callback callback);

    /**
     * 远程启动反馈
     *
     * @param uplinkQueueMessage
     * @param callback
     */
    void onRemoteStartChargingResponse(UplinkQueueMessage uplinkQueueMessage, Callback callback);

    /**
     * 远程停止反馈
     */
    void onRemoteStopChargingResponse(UplinkQueueMessage uplinkQueueMessage, Callback callback);

    /**
     * 交易记录上报
     */
    void onTransactionRecord(UplinkQueueMessage uplinkQueueMessage, Callback callback);
}