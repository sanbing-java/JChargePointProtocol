/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.proto.model;

import lombok.*;
import sanbing.jcpp.proto.gen.ProtocolProto.PricingModelFlag;
import sanbing.jcpp.proto.gen.ProtocolProto.PricingModelRule;
import sanbing.jcpp.proto.gen.ProtocolProto.PricingModelType;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class PricingModel {

    private UUID id;

    // 计数器，供充电桩协议使用
    private int sequenceNumber;

    private String pileCode;

    private PricingModelType type;

    private PricingModelRule rule;

    /**
     * 标准电价（单位分）
     */
    private int standardElec;

    /**
     * 标准服务费（单位分）
     */
    private int standardServ;

    /**
     * 分时电价
     */
    private Map<PricingModelFlag, FlagPrice> flagPriceList;

    /**
     * 分时时段
     */
    private List<Period> periodsList;

    @Setter
    @Getter
    public static class Period {
        private int sn;

        // 起始时间
        private LocalTime begin;

        // 结束时间
        private LocalTime end;

        // 尖峰平谷标识
        private PricingModelFlag flag;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FlagPrice {

        // 分时电价，单位分
        private int elec;

        // 分时服务费，单位分
        private int serv;
    }

}