/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.app.dal.config.ibatis.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * @author baigod
 */
public enum StationStatusEnum implements IEnum<String> {
    OPERATIONAL,      // 正常运营
    PARTIAL_FAILURE,   // 部分故障
    FULLY_LOADED,      // 满载
    MAINTENANCE,      // 维护中
    CLOSED,           // 关闭
    WAITING_FOR_OPEN; // 待开放

    @Override
    public String getValue() {
        return name();
    }


}