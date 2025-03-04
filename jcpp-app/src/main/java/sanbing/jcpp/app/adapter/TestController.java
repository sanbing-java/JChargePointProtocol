/**
 * 开源代码，仅供学习和交流研究使用，商用请联系三丙
 * 微信：mohan_88888
 * 抖音：程序员三丙
 * 付费课程知识星球：https://t.zsxq.com/aKtXo
 */
package sanbing.jcpp.app.adapter;

import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sanbing.jcpp.app.service.PileProtocolService;

import java.math.BigDecimal;

/**
 * @author baigod
 */
@RestController
public class TestController {

    @Resource
    private PileProtocolService pileProtocolService;

    @GetMapping("/api/startCharge")
    public ResponseEntity<String> startCharge() {

        pileProtocolService.startCharge("20231212000010", "01", new BigDecimal("50"), "12345678901234567890");

        return ResponseEntity.ok("success");
    }
}