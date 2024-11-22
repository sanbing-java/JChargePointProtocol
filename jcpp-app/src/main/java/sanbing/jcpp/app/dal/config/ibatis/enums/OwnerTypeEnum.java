/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.app.dal.config.ibatis.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * @author baigod
 */
public enum OwnerTypeEnum implements IEnum<String> {
    C,
    B,
    G;

    @Override
    public String getValue() {
        return name();
    }
}