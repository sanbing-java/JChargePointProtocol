/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.app.dal.config.ibatis.typehandlers;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;
import sanbing.jcpp.infrastructure.util.jackson.JacksonUtil;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Slf4j
@MappedTypes({JsonNode.class})
public class JsonbTypeHandler extends JacksonTypeHandler {

    public JsonbTypeHandler(Class<?> type) {
        super(type);
    }

    public JsonbTypeHandler(Class<?> type, Field field) {
        super(type, field);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        if (ps != null) {
            PGobject jsonObject = new PGobject();
            jsonObject.setType("jsonb");
            jsonObject.setValue(JacksonUtil.toString(parameter));
            ps.setObject(i, jsonObject);
        }
    }
}
