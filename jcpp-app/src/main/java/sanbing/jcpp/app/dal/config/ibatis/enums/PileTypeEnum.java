/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.app.dal.config.ibatis.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * @author baigod
 */
public enum PileTypeEnum implements IEnum<String> {
    AC,         // 交流充电桩
    DC,         // 直流充电桩
    ;

    @Override
    public String getValue() {
        return name();
    }
}