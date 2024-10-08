/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.util.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 13位时间戳反序列化器
 * @author baigod
 */
public class LongTimestampDeserializer extends JsonDeserializer<Long> {

    @Override
    public Long deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        // 判定是否是long类型
        if ("LONG".equals(jsonParser.getNumberType().name())) {
            return jsonParser.getLongValue();
        }
        LocalDateTime localDateTime = LocalDateTime.parse(jsonParser.getValueAsString().replace(" ", "T").replace("Z", ""));
        ZoneId systemDefaultZone = ZoneId.systemDefault();
        return localDateTime.atZone(systemDefaultZone).toInstant().toEpochMilli();
    }

}
