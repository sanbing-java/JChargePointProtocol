/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.util.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间类型序列化工具
 *
 * @author baigod
 */
public class LocalTimeSerializer extends JsonSerializer<LocalTime> {
    public static final LocalTimeSerializer INSTANCE = new LocalTimeSerializer();

    private LocalTimeSerializer() {
    }

    private static final DateTimeFormatter DATE_TIME_FORMATTER_MS = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    @Override
    public void serialize(LocalTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.format(DATE_TIME_FORMATTER_MS));
    }
}