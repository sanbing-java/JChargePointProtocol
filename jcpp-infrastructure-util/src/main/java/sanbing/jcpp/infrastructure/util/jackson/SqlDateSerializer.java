/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.util.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.apache.commons.lang3.time.FastDateFormat;

import java.io.IOException;
import java.sql.Date;

/**
 * sqlDate序列化
 *
 * @author baigod
 */
public class SqlDateSerializer extends StdSerializer<Date> {
    public static final SqlDateSerializer INSTANCE = new SqlDateSerializer();

    private static final FastDateFormat FAST_DATE_FORMAT_MS = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSS");

    private SqlDateSerializer() {
        super(Date.class);
    }

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(FAST_DATE_FORMAT_MS.format(value));
    }
}