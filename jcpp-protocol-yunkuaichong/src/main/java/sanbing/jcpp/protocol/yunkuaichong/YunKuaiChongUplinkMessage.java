/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.yunkuaichong;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class YunKuaiChongUplinkMessage implements Serializable {
    // 消息ID
    private final UUID id;

    // 起始域
    private int head;

    // 数据长度
    private int dataLength;

    // 序列号
    private int sequenceNumber;

    // 加密标识
    private int encryptionFlag;

    // 指令
    private int cmd;

    // 消息体
    private byte[] msgBody;

    // 校验和
    private int checkSum;

    // 真实报文
    private byte[] rawFrame;

    public YunKuaiChongUplinkMessage(UUID id) {
        this.id = id;
    }

    public YunKuaiChongUplinkMessage() {
        this(UUID.randomUUID());
    }

}
