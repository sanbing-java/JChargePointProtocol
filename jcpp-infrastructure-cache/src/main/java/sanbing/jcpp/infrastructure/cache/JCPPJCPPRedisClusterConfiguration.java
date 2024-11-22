/**
 * 抖音关注：程序员三丙
 * 知识星球：https://t.zsxq.com/j9b21
 */
package sanbing.jcpp.infrastructure.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

@Configuration
@ConditionalOnExpression("'${cache.type:null}'=='redis' && '${redis.connection.type:null}'=='cluster'")
@Slf4j
public class JCPPJCPPRedisClusterConfiguration extends JCPPRedisCacheConfiguration {

    @Value("${redis.cluster.nodes:}")
    private String clusterNodes;

    @Value("${redis.cluster.max-redirects:12}")
    private Integer maxRedirects;

    @Value("${redis.cluster.useDefaultPoolConfig:true}")
    private boolean useDefaultPoolConfig;

    @Value("${redis.password:}")
    private String password;


    @Override
    public LettuceConnectionFactory loadFactory() {
        log.info("Initializing Redis Cluster on {}", clusterNodes);
        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();
        clusterConfiguration.setClusterNodes(getNodes(clusterNodes));
        clusterConfiguration.setMaxRedirects(maxRedirects);
        clusterConfiguration.setPassword(password);
        return new LettuceConnectionFactory(clusterConfiguration, buildClientConfig());
    }

    private LettucePoolingClientConfiguration buildClientConfig() {

        var clientConfigurationBuilder = LettucePoolingClientConfiguration.builder();

        if (!useDefaultPoolConfig) {
            clientConfigurationBuilder
                    .poolConfig(buildPoolConfig());
        }
        return clientConfigurationBuilder
                .build();
    }
}
