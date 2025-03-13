/**
 * 开源代码，仅供学习和交流研究使用，商用请联系三丙
 * 微信：mohan_88888
 * 抖音：程序员三丙
 * 付费课程知识星球：https://t.zsxq.com/aKtXo
 */
package sanbing.jcpp.infrastructure.util.codec;

import java.time.LocalDateTime;

public class CP56Time2aUtil {
    /**
     * 高性能解码 CP56Time2a 字节数组为本地时间对象
     *
     * @param bytes 7字节的CP56Time2a数组
     * @return 解码后的本地时间（系统默认时区）
     * @throws IllegalArgumentException 当输入不符合规范时抛出异常
     */
    public static LocalDateTime decode(byte[] bytes) {
        if (bytes.length != 7) {
            throw new IllegalArgumentException("Invalid CP56Time2a format: 需要7字节");
        }

        // 预处理字节为 Unsigned Int（避免重复转换）
        final int b6 = bytes[6] & 0xFF;
        final int b5 = bytes[5] & 0xFF;
        final int b4 = bytes[4] & 0xFF;
        final int b3 = bytes[3] & 0xFF;
        final int b2 = bytes[2] & 0xFF;
        final int b1 = bytes[1] & 0xFF;
        final int b0 = bytes[0] & 0xFF;

        // 年份（2000~2127）: 7位无符号
        final int year = 2000 + (b6 & 0x7F);

        // 月份（1~12）: 低4位
        final int month = b5 & 0x0F;
        if (month < 1 || month > 12) { // 内联校验
            throw new IllegalArgumentException("非法月份值：" + month);
        }

        // 日期（1~31）: 低5位
        final int day = b4 & 0x1F;
        if (day < 1) { // 内联校验
            throw new IllegalArgumentException("非法日期值：" + day);
        }

        // 小时（0~23）: 低5位
        final int hour = b3 & 0x1F;
        if (hour > 23) { // 内联校验
            throw new IllegalArgumentException("非法小时值：" + hour);
        }

        // 分钟（0~59）: 低6位
        final int minute = b2 & 0x3F;
        if (minute > 59) { // 内联校验
            throw new IllegalArgumentException("非法分钟值：" + minute);
        }

        // 合并秒和毫秒（小端序优化）
        final int combined = (b1 << 8) | b0;
        final int second = combined / 1000;
        if (second > 59) { // 内联校验
            throw new IllegalArgumentException("非法秒数值：" + second);
        }

        return LocalDateTime.of(year, month, day, hour, minute, second, (combined % 1000) * 1_000_000);
    }

    /**
     * 高性能编码本地时间对象为 CP56Time2a 字节数组
     *
     * @param dateTime 本地时间对象（需保证为系统默认时区）
     * @return 7字节的CP56Time2a数组
     */
    public static byte[] encode(LocalDateTime dateTime) {
        final byte[] cp56Time2a = new byte[7];

        // 年份（2000~2127）
        final int year = dateTime.getYear();
        cp56Time2a[6] = (byte) ((year - 2000) & 0x7F); // 7位掩码优化

        // 月份（1~12）
        final int month = dateTime.getMonthValue();
        cp56Time2a[5] = (byte) month; // 直接赋值，协议层保证低4位有效

        // 日期（1~31）
        cp56Time2a[4] = (byte) dateTime.getDayOfMonth(); // 协议层保证低5位有效

        // 时间字段（直接赋值，协议层保证掩码）
        cp56Time2a[3] = (byte) dateTime.getHour();
        cp56Time2a[2] = (byte) dateTime.getMinute();

        // 合并秒和毫秒（避免中间变量）
        final int nano = dateTime.getNano();
        final int combined = dateTime.getSecond() * 1000 + (nano / 1_000_000);
        cp56Time2a[1] = (byte) (combined >>> 8);  // 小端序高字节
        cp56Time2a[0] = (byte) combined;         // 小端序低字节

        return cp56Time2a;
    }
}