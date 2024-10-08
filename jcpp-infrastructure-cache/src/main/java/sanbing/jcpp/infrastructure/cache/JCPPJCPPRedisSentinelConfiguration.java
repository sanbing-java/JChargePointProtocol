/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

@Configuration
@ConditionalOnMissingBean(JCPPCaffeineCacheConfiguration.class)
@ConditionalOnProperty(prefix = "redis.connection", value = "type", havingValue = "sentinel")
@Slf4j
public class JCPPJCPPRedisSentinelConfiguration extends JCPPRedisCacheConfiguration {

    @Value("${redis.sentinel.master:}")
    private String master;

    @Value("${redis.sentinel.sentinels:}")
    private String sentinels;

    @Value("${redis.sentinel.password:}")
    private String sentinelPassword;

    @Value("${redis.sentinel.useDefaultPoolConfig:true}")
    private boolean useDefaultPoolConfig;

    @Value("${redis.db:}")
    private Integer database;

    @Value("${redis.password:}")
    private String password;

    @Override
    public LettuceConnectionFactory loadFactory() {
        log.info("Initializing Redis Sentinel on {}, sentinels: {}", master, sentinels);
        RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
        redisSentinelConfiguration.setMaster(master);
        redisSentinelConfiguration.setSentinels(getNodes(sentinels));
        redisSentinelConfiguration.setSentinelPassword(sentinelPassword);
        redisSentinelConfiguration.setPassword(password);
        redisSentinelConfiguration.setDatabase(database);
        return new LettuceConnectionFactory(redisSentinelConfiguration, buildClientConfig());
    }

    private LettucePoolingClientConfiguration buildClientConfig() {
        var clientConfigurationBuilder = LettucePoolingClientConfiguration.builder();
        if (!useDefaultPoolConfig) {
            clientConfigurationBuilder.poolConfig(buildPoolConfig());
        }
        return clientConfigurationBuilder.build();
    }
}
