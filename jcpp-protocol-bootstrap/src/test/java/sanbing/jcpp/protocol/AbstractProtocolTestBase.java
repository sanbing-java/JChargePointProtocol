package sanbing.jcpp.protocol; /**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author baigod
 */
@ActiveProfiles("test")
@SpringBootTest(classes = JCPPProtocolServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class AbstractProtocolTestBase {

    static {
        System.setProperty("spring.config.name", "protocol-service");
    }

    protected final Logger log = LoggerFactory.getLogger(this.getClass());


    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected Environment environment;
}