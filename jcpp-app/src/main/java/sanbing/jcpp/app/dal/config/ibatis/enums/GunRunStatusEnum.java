/**
 * 开源代码，仅供学习和交流研究使用，商用请联系三丙
 * 微信：mohan_88888
 * 抖音：程序员三丙
 * 付费课程知识星球：https://t.zsxq.com/aKtXo
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