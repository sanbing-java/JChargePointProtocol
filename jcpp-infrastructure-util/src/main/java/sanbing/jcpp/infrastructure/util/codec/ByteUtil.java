/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.util.codec;

import cn.hutool.core.io.checksum.crc16.CRC16Modbus;
import io.netty.buffer.ByteBuf;
import sanbing.jcpp.infrastructure.util.JCPPPair;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @author baigod
 */
public class ByteUtil {

    public static byte[] uuidToBytes(UUID uuid) {
        ByteBuffer buf = ByteBuffer.allocate(16);
        buf.putLong(uuid.getMostSignificantBits());
        buf.putLong(uuid.getLeastSignificantBits());
        return buf.array();
    }

    public static UUID bytesToUuid(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long firstLong = bb.getLong();
        long secondLong = bb.getLong();
        return new UUID(firstLong, secondLong);
    }

    public static byte[] stringToBytes(String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    public static String bytesToString(byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }

    public static byte[] longToBytes(long x) {
        ByteBuffer longBuffer = ByteBuffer.allocate(Long.BYTES);
        longBuffer.putLong(0, x);
        return longBuffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getLong();
    }

    /**
     * 计算校验和
     */
    public static int crcSum(byte[] data) {
        CRC16Modbus crc16Modbus = new CRC16Modbus();
        crc16Modbus.update(data);
        return (int) crc16Modbus.getValue();
    }

    /**
     * 验证校验和
     */
    public static JCPPPair<Boolean, Integer> checkCrcSum(byte[] data, int checkSum) {
        int expectedCs = crcSum(data);
        return JCPPPair.of(expectedCs == checkSum, expectedCs);
    }

    /**
     * ByteBuf转byte数组
     *
     * @param byteBuf
     * @return
     */
    public static byte[] toBytes(ByteBuf byteBuf) {
        int msgLength = byteBuf.readableBytes();
        byte[] bytes = new byte[msgLength];
        byteBuf.readBytes(bytes);
        return bytes;
    }
}