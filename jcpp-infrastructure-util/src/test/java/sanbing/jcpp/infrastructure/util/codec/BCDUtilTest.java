/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.util.codec;

import cn.hutool.core.util.HexUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class BCDUtilTest {

    @Test
    void toBytesTest() {
        String pileCodeHex = "20231212000010";

        byte[] bytes = HexUtil.decodeHex(pileCodeHex);

        String pileCode = BCDUtil.toString(bytes);

        assert pileCodeHex.equals(pileCode);

        byte[] pileCodeBytes = BCDUtil.toBytes(pileCodeHex);

        assertArrayEquals(pileCodeBytes, bytes);
    }
}