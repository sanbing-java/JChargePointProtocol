/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.util.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * timestamp 反序列化
 * @author baigod
 */
public class TimestampDeserializer extends JsonDeserializer<Timestamp> {

    public static final TimestampDeserializer INSTANCE = new TimestampDeserializer();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_TIME_FORMATTER_MS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private TimestampDeserializer() {
    }

    @Override
    public Timestamp deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        String dateString = p.getText();

        return dateString.length() > 19
                ? Timestamp.from(LocalDateTime.parse(dateString, DATE_TIME_FORMATTER_MS).atZone(ZoneOffset.systemDefault()).toInstant())
                : Timestamp.from(LocalDateTime.parse(dateString, DATE_TIME_FORMATTER).atZone(ZoneOffset.systemDefault()).toInstant());
    }
}