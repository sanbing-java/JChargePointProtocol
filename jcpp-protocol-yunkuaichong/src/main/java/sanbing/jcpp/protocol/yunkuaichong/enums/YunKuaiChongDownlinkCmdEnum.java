/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.yunkuaichong.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author baigod
 */
@AllArgsConstructor
@Getter
public enum YunKuaiChongDownlinkCmdEnum {

    LOGIN_ACK(0x02),

    SYNC_TIME(0x56),

    HEARTBEAT(0x04),

    VERIFY_PRICING_ACK(0x06),

    QUERY_PRICING_ACK(0X0A),

    SET_PRICING(0x58),

    REMOTE_START_CHARGING(0x34),

    REMOTE_STOP_CHARGING(0x36),

    TRANSACTION_RECORD(0x40),

    REMOTE_PARALLEL_START_CHARGING(0xA4);

    private final Integer cmd;

}