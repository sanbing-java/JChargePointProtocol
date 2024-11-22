/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
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