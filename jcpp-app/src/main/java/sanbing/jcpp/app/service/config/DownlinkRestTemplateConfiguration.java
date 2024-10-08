/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.app.service.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

/**
 * @author baigod
 */
@Configuration
public class DownlinkRestTemplateConfiguration {

    @Bean("downlinkRestTemplate")
    public RestTemplate downlinkRestTemplate() {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.of(3, ChronoUnit.SECONDS))
                .setReadTimeout(Duration.of(3, ChronoUnit.SECONDS))
                .build();
        restTemplate.setMessageConverters(Collections.singletonList(new ProtobufHttpMessageConverter()));
        return restTemplate;
    }
}