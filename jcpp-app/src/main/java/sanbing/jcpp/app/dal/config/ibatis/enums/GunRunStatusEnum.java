/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.app.dal.config.ibatis.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * @author baigod
 */
public enum GunRunStatusEnum implements IEnum<String> {
    IDLE,              // 空闲
    INSERTED,       // 已插枪
    CHARGING,         // 充电中
    CHARGE_COMPLETE,   // 充电完成
    DISCHARGE_READY,   // 放电准备
    DISCHARGING,      // 放电中
    DISCHARGE_COMPLETE, // 放电完成
    RESERVED,         // 预约
    FAULT;            // 故障

    @Override
    public String getValue() {
        return name();
    }
}