/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.util.jackson;

import java.time.format.DateTimeFormatter;

/**
 * Instant 序列化
 *
 * @author baigod
 */
public class InstantSerializer extends com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer {
    public static final InstantSerializer INSTANCE = new InstantSerializer();

    private InstantSerializer() {
        super(com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer.INSTANCE, true,false, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }

}
