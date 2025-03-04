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
public enum GunOptStatusEnum implements IEnum<String> {
    AVAILABLE,          // 可用状态
    IN_MAINTENANCE,     // 维护中状态
    OUT_OF_SERVICE,     // 停用状态
    RESERVED;           // 已预约状态

    @Override
    public String getValue() {
        return name();
    }
}