/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.app.dal.config.ibatis.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * @author baigod
 */
public enum PileStatusEnum implements IEnum<String> {
    IDLE,         // 空闲
    WORKING,     // 工作中
    FAULT,        // 故障
    MAINTENANCE,  // 维护中
    OFFLINE,      // 离线
    ;

    @Override
    public String getValue() {
        return name();
    }
}