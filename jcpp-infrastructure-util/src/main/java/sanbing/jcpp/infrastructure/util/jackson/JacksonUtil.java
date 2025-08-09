/**
 * 开源代码，仅供学习和交流研究使用，商用请联系三丙
 * 微信：mohan_88888
 * 抖音：程序员三丙
 * 付费课程知识星球：https://t.zsxq.com/aKtXo
 */
package sanbing.jcpp.infrastructure.util.jackson;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Arrays;
import java.util.TimeZone;

/**
 * @author baigod
 */
public class JacksonUtil {

    public static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
            .configure(Feature.ALLOW_SINGLE_QUOTES, true)
            .configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true)
            .configure(JsonWriteFeature.WRITE_NUMBERS_AS_STRINGS, false)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .defaultTimeZone(TimeZone.getTimeZone("GMT+8"))
            .build()
            .registerModules(DataTypeModule.INSTANCE);

    public static final ObjectMapper PRETTY_SORTED_JSON_MAPPER = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
            .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
            .configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true)
            .serializationInclusion(Include.NON_NULL)
            .defaultTimeZone(TimeZone.getTimeZone("GMT+8"))
            .build()
            .registerModules(DataTypeModule.INSTANCE);


    public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
        try {
            return fromValue != null ? OBJECT_MAPPER.convertValue(fromValue, toValueType) : null;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The given object value: "
                    + fromValue + " cannot be converted to " + toValueType, e);
        }
    }

    public static <T> T convertValue(Object fromValue, TypeReference<T> toValueTypeRef) {
        try {
            return fromValue != null ? OBJECT_MAPPER.convertValue(fromValue, toValueTypeRef) : null;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The given object value: "
                    + fromValue + " cannot be converted to " + toValueTypeRef, e);
        }
    }

    public static <T> T fromString(String string, Class<T> clazz) {
        try {
            return string != null ? OBJECT_MAPPER.readValue(string, clazz) : null;
        } catch (IOException e) {
            throw new IllegalArgumentException("The given string value: "
                    + string + " cannot be transformed to Json object", e);
        }
    }

    public static <T> T fromString(String string, TypeReference<T> valueTypeRef) {
        try {
            return string != null ? OBJECT_MAPPER.readValue(string, valueTypeRef) : null;
        } catch (IOException e) {
            throw new IllegalArgumentException("The given string value: "
                    + string + " cannot be transformed to Json object", e);
        }
    }

    public static <T> T fromBytes(byte[] bytes, Class<T> clazz) {
        try {
            return bytes != null ? OBJECT_MAPPER.readValue(bytes, clazz) : null;
        } catch (IOException e) {
            throw new IllegalArgumentException("The given string value: "
                    + Arrays.toString(bytes) + " cannot be transformed to Json object", e);
        }
    }

    public static JsonNode fromBytes(byte[] bytes) {
        try {
            return OBJECT_MAPPER.readTree(bytes);
        } catch (IOException e) {
            throw new IllegalArgumentException("The given byte[] value: "
                    + Arrays.toString(bytes) + " cannot be transformed to Json object", e);
        }
    }

    public static String toString(Object value) {
        try {
            return value != null ? OBJECT_MAPPER.writeValueAsString(value) : null;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("The given Json object value: "
                    + value + " cannot be transformed to a String", e);
        }
    }

    public static String toPrettyString(Object o) {
        try {
            return PRETTY_SORTED_JSON_MAPPER.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T treeToValue(JsonNode node, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.treeToValue(node, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException("Can't convert value: " + node.toString(), e);
        }
    }

    public static JsonNode toJsonNode(String value) {
        return toJsonNode(value, OBJECT_MAPPER);
    }

    public static JsonNode toJsonNode(String value, ObjectMapper mapper) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return mapper.readTree(value);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static ObjectNode newObjectNode() {
        return newObjectNode(OBJECT_MAPPER);
    }

    public static ObjectNode newObjectNode(ObjectMapper mapper) {
        return mapper.createObjectNode();
    }

    public static ArrayNode newArrayNode() {
        return newArrayNode(OBJECT_MAPPER);
    }

    public static ArrayNode newArrayNode(ObjectMapper mapper) {
        return mapper.createArrayNode();
    }

    public static <T> T clone(T value) {
        @SuppressWarnings("unchecked")
        Class<T> valueClass = (Class<T>) value.getClass();
        return fromString(toString(value), valueClass);
    }

    public static <T> JsonNode valueToTree(T value) {
        return OBJECT_MAPPER.valueToTree(value);
    }

    public static <T> byte[] writeValueAsBytes(T value) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("The given Json object value: "
                    + value + " cannot be transformed to a String", e);
        }
    }

    public static JsonNode getSafely(JsonNode node, String... path) {
        if (node == null) {
            return null;
        }
        for (String p : path) {
            if (!node.has(p)) {
                return null;
            } else {
                node = node.get(p);
            }
        }
        return node;
    }

}
