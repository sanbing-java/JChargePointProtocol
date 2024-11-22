/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.util.codec;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;

class CP56Time2aUtilTest {

    @Test
    void encodeTest() {
        Instant time = Instant.ofEpochMilli(1727798453000L);

        byte[] bytes = CP56Time2aUtil.encode(time);

        System.out.println(Arrays.toString(bytes));

        Instant decode = CP56Time2aUtil.decode(bytes);

        assert time.equals(decode);
    }

}