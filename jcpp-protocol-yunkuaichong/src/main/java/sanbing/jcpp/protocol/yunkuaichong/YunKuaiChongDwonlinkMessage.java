/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.yunkuaichong;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import sanbing.jcpp.proto.gen.ProtocolProto.DownlinkRestMessage;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author baigod
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class YunKuaiChongDwonlinkMessage implements Serializable {
    public static final byte SUCCESS_BYTE = 0x00;
    public static final byte FAILURE_BYTE = 0x01;

    // 消息ID
    private UUID id;

    // 请求ID（如有）
    private UUID requestId;

    // 指令
    private int cmd;

    // 消息体
    private DownlinkRestMessage msg;

    // 上行消息
    private YunKuaiChongUplinkMessage requestData;

}