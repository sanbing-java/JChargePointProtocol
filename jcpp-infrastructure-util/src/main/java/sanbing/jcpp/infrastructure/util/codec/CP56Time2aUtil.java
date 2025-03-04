/**
 * 开源代码，仅供学习和交流研究使用，商用请联系三丙
 * 微信：mohan_88888
 * 抖音：程序员三丙
 * 付费课程知识星球：https://t.zsxq.com/aKtXo
 */
package sanbing.jcpp.infrastructure.util.codec;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class CP56Time2aUtil {
    /**
     * 解码 CP56Time2a 字节数组为 Instant 对象
     *
     * @param bytes 字节数组
     * @return Instant 对象
     */
    public static Instant decode(byte[] bytes) {
        // 将字节数组解释为各个时间部分
        int milliseconds = ((bytes[0] & 0xFF) + ((bytes[1] & 0xFF) << 8)); // 处理字节的无符号值
        int minutes = bytes[2] & 0x3F;
        int hours = bytes[3] & 0x1F;
        int days = bytes[4] & 0x1F;
        int months = bytes[5] & 0x0F;
        int years = bytes[6] & 0x7F;

        // 将 CP56Time2a 转换为 LocalDateTime
        LocalDateTime dateTime = LocalDateTime.of(
                years + 2000,
                months,
                days,
                hours,
                minutes,
                milliseconds / 1000  // 秒数
        );

        // 返回对应的 Instant 对象
        return dateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    /**
     * 编码 Instant 对象为 CP56Time2a 字节数组
     *
     * @param instant Instant 对象
     * @return 字节数组
     */
    public static byte[] encode(Instant instant) {
        // 将 Instant 转换到 LocalDateTime
        LocalDateTime aTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        byte[] result = new byte[7];
        int milliseconds = aTime.getSecond() * 1000; // 获取毫秒部分

        // 填充字节数组
        result[0] = (byte) (milliseconds % 256);
        result[1] = (byte) (milliseconds / 256);
        result[2] = (byte) aTime.getMinute();
        result[3] = (byte) aTime.getHour();
        result[4] = (byte) aTime.getDayOfMonth();
        result[5] = (byte) aTime.getMonthValue(); // 1-12
        result[6] = (byte) (aTime.getYear() % 100); // 00-99

        return result;
    }
}