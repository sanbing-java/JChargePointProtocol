/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.protocol;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;

/**
 * @author baigod
 */
@SpringBootApplication(scanBasePackages = {"sanbing.jcpp.protocol",
        "sanbing.jcpp.infrastructure.stats",
        "sanbing.jcpp.infrastructure.queue",
        "sanbing.jcpp.infrastructure.util"})
@EnableAsync
@EnableScheduling
public class JCPPProtocolServiceApplication {

    private static final String SPRING_CONFIG_NAME_KEY = "--spring.config.name";
    private static final String DEFAULT_SPRING_CONFIG_PARAM = SPRING_CONFIG_NAME_KEY + "=" + "protocol-service";

    public static void main(String[] args) {
        new SpringApplicationBuilder(JCPPProtocolServiceApplication.class).bannerMode(Banner.Mode.LOG).run(updateArguments(args));
    }

    private static String[] updateArguments(String[] args) {
        if (Arrays.stream(args).noneMatch(arg -> arg.startsWith(SPRING_CONFIG_NAME_KEY))) {
            String[] modifiedArgs = new String[args.length + 1];
            System.arraycopy(args, 0, modifiedArgs, 0, args.length);
            modifiedArgs[args.length] = DEFAULT_SPRING_CONFIG_PARAM;
            return modifiedArgs;
        }
        return args;
    }
}