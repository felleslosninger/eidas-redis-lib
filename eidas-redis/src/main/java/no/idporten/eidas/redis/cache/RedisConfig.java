package no.idporten.eidas.redis.cache;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.util.StringUtils;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Data
public class RedisConfig {

    private String redisHost;

    private String redisPort;

    private String redisPassword;

    private String issuerName = "eidas-proxy";

    private String sentinelMaster;
    private String sentinelNodes;


    public int getRedisPort() {
        return Integer.parseInt(redisPort);
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        if (!StringUtils.hasText(sentinelNodes)) {
            log.info("Creating lettuce connection factory for standalone redis");
            if (!StringUtils.hasText(redisPort) || !StringUtils.hasText(redisHost)) {
                throw new IllegalArgumentException(String.format("Missing redis configuration redisHost: %s redisPort: %s  in standalone mode", redisHost, redisPort));
            }
            RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration(redisHost, getRedisPort());
            if (StringUtils.hasText(redisPassword)) {
                standaloneConfig.setPassword(redisPassword);
            }
            return new LettuceConnectionFactory(standaloneConfig);
        } else {
            return createSentinelConnectionFactory();
        }
    }

    private LettuceConnectionFactory createSentinelConnectionFactory() {
        log.info("Creating lettuce connection factory for redis sentinel");
        if (!StringUtils.hasText(sentinelMaster) || !StringUtils.hasText(sentinelNodes) || !StringUtils.hasText(redisPassword)) {
            throw new IllegalArgumentException(String.format("Missing sentinel configuration sentinelMaster: %s, sentinelNodes: %s, redisPassword is set: %s",
                    sentinelMaster,
                    sentinelNodes,
                    StringUtils.hasText(redisPassword)));
        }
        String[] nodes = sentinelNodes.split(",");
        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
                .master(sentinelMaster);

        for (String node : nodes) {
            String[] parts = node.trim().split(":");
            if(parts.length != 2) {
                throw new IllegalArgumentException(String.format("Invalid sentinel node configuration %s", node.trim()));
            }
            sentinelConfig.sentinel(parts[0].trim(), Integer.parseInt(parts[1].trim()));
        }

        if (StringUtils.hasText(redisPassword)) {
            sentinelConfig.setPassword(redisPassword);
        }
        return new LettuceConnectionFactory(sentinelConfig);
    }

}