package sanbing.jcpp.protocol.yunkuaichong.v150;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import sanbing.jcpp.protocol.yunkuaichong.v150.cmd.YunKuaiChongV150BmsAbortULCmd;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class BmsAbortULCmdTest {

    @Test
    public void test() {
        YunKuaiChongV150BmsAbortULCmd bmsAbortULCmd = new YunKuaiChongV150BmsAbortULCmd();

        /*
         解析中止原因
         0x05 -> 00000101
         */
        System.out.println("BMS中止充电原因: " + bmsAbortULCmd.parseAbortReasons((byte) 0x05));

        /*
         解析故障原因
         0x0005 -> 00000000 00000101
         */
        System.out.println("BMS中止充电故障原因: " + bmsAbortULCmd.parseFaultReasons(new byte[]{(byte)0x00, (byte)0x05}));

        /*
         解析错误原因
         0x01 -> 00000001
         */
        System.out.println("BMS中止充电错误原因: " + bmsAbortULCmd.parseErrorReasons((byte) 0x01));
    }

}
