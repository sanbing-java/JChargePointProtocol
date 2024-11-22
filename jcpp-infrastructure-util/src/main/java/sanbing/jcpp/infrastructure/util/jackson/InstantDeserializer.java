/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.util.jackson;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.FastDateFormat;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Instant 反序列化
 *
 * @author baigod
 */
public class InstantDeserializer extends com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer<Instant> {
    public static final InstantDeserializer INSTANCE = new InstantDeserializer();

    private final FastDateFormat FAST_DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
    private final FastDateFormat FAST_DATE_FORMAT_MS = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSS");

    private InstantDeserializer() {
        super(InstantDeserializer.INSTANT, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }

    @SneakyThrows
    @Override
    public Instant deserialize(JsonParser parser, DeserializationContext context) {
        String timestamp = parser.getText();

        return timestamp.length() > 19
                ? FAST_DATE_FORMAT_MS.parse(timestamp).toInstant()
                : FAST_DATE_FORMAT.parse(timestamp).toInstant();

    }
}