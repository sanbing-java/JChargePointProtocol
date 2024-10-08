/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.proto;


import sanbing.jcpp.infrastructure.proto.model.PricingModel;
import sanbing.jcpp.infrastructure.proto.model.PricingModel.FlagPrice;
import sanbing.jcpp.infrastructure.proto.model.PricingModel.Period;
import sanbing.jcpp.proto.gen.ProtocolProto.*;

import java.util.Map;

/**
 * @author baigod
 */
public class ProtoConverter {

    public static PricingModelProto toPricingModel(PricingModel pricingModel) {
        // 创建 PricingModelProto 实例
        PricingModelProto.Builder builder = PricingModelProto.newBuilder();

        // 设置字段
        builder.setType(PricingModelType.valueOf(pricingModel.getType().name()));
        builder.setRule(PricingModelRule.valueOf(pricingModel.getRule().name()));
        builder.setStandardElec(pricingModel.getStandardElec());
        builder.setStandardServ(pricingModel.getStandardServ());

        // 转换 flagPriceList
        for (Map.Entry<PricingModelFlag, FlagPrice> entry : pricingModel.getFlagPriceList().entrySet()) {
            PricingModelFlag flag = entry.getKey();
            FlagPrice flagPrice = entry.getValue();

            FlagPriceProto flagPriceProto = FlagPriceProto.newBuilder()
                    .setFlag(PricingModelFlag.valueOf(flag.name())) // 枚举转换
                    .setElec(flagPrice.getElec())
                    .setServ(flagPrice.getServ())
                    .build();

            builder.putFlagPrice(flag.ordinal(), flagPriceProto); // 按 ordinal 值作为 key 存入
        }

        // 转换 PeriodsList
        for (Period period : pricingModel.getPeriodsList()) {
            PeriodProto periodProto = PeriodProto.newBuilder()
                    .setSn(period.getSn())
                    .setBegin(period.getBegin().toString()) // 假设 begin 是 LocalTime, 转换为字符串
                    .setEnd(period.getEnd().toString()) // 假设 end 是 LocalTime, 转换为字符串
                    .setFlag(PricingModelFlag.valueOf(period.getFlag().name()))
                    .build();
            builder.addPeriod(periodProto);
        }

        return builder.build();
    }
}