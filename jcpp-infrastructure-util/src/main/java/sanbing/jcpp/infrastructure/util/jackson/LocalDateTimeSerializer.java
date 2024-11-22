/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.util.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间类型序列化工具
 *
 * @author baigod
 */
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
    public static final LocalDateTimeSerializer INSTANCE = new LocalDateTimeSerializer();

    private static final DateTimeFormatter DATE_TIME_FORMATTER_MS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private LocalDateTimeSerializer() {
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.format(DATE_TIME_FORMATTER_MS));
    }
}