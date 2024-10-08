/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.util.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * LocalDateTime类型反序列化
 * 需要用到的字段上加 @JsonDeserialize(using = LocalDateTimeDeserializer.class)
 */
public class LocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {
    public static final LocalDateTimeDeserializer INSTANCE = new LocalDateTimeDeserializer();

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_TIME_FORMATTER_MS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private LocalDateTimeDeserializer() {
        super(LocalDateTime.class);
    }

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext context)
            throws IOException {

        String dateString = jsonParser.getText();
        return dateString.length() > 19
                ? LocalDateTime.parse(dateString, DATE_TIME_FORMATTER_MS)
                : LocalDateTime.parse(dateString, DATE_TIME_FORMATTER);
    }

}
