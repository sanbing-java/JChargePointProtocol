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