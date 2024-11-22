/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.app.dal.config.ibatis.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

public enum OrderStatusEnum implements IEnum<String> {
    PENDING,
    IN_CHARGING,
    COMPLETED,
    CANCELLED,
    TERMINATED,
    FAILED,
    REFUNDED;


    @Override
    public String getValue() {
        return name();
    }

}