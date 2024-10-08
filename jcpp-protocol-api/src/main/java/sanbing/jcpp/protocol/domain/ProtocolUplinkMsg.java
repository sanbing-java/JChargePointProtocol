/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol.domain;

import io.netty.buffer.ByteBufUtil;

import java.net.SocketAddress;
import java.util.UUID;

public record ProtocolUplinkMsg<T>(SocketAddress address, UUID id, T data, int size) {

    @Override
    public String toString() {
        if (data instanceof byte[]) {
            return ByteBufUtil.hexDump((byte[]) data);
        } else {
            return data.toString();
        }
    }
}