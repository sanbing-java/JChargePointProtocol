/**
 * 开源代码，仅供学习和交流研究使用，商用请联系三丙
 * 微信：mohan_88888
 * 抖音：程序员三丙
 * 付费课程知识星球：https://t.zsxq.com/aKtXo
 */
package sanbing.jcpp.protocol.lvneng.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author baigod
 */
@AllArgsConstructor
@Getter
public enum LvnengDownlinkCmdEnum {

    LOGIN_ACK((short) 105),

    SYNC_TIME((short) 3)
   ;

    private final short cmd;

}