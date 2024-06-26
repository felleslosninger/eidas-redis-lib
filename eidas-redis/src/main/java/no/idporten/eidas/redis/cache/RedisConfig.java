package no.idporten.eidas.redis.cache;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

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
        if (StringUtils.isEmpty(sentinelNodes)) {
            log.info("Creating lettuce connection factory for standalone redis");
            if (StringUtils.isEmpty(redisPort) || StringUtils.isEmpty(redisHost)) {
                throw new IllegalArgumentException(String.format("Missing redis configuration redisHost: %s redisPort: %s  in standalone mode", redisHost, redisPort));
            }
            LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisHost, getRedisPort());
            if (StringUtils.hasText(redisPassword)) {
                lettuceConnectionFactory.setPassword(redisPassword);
            }
            return lettuceConnectionFactory;
        } else {
            return createSentinelConnectionFactory();
        }
    }

    private LettuceConnectionFactory createSentinelConnectionFactory() {
        log.info("Creating lettuce connection factory for redis sentinel");
        if (StringUtils.isEmpty(sentinelMaster) || StringUtils.isEmpty(sentinelNodes) || StringUtils.isEmpty(redisPassword)) {
            throw new IllegalArgumentException(String.format("Missing sentinel configuration sentinelMaster: %s, sentinelNodes: %s, redisPassword is set: %s", sentinelMaster, sentinelNodes, StringUtils.isEmpty(redisPassword)));
        }
        List<String> nodes = Arrays.asList(sentinelNodes.split(","));
        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
                .master(sentinelMaster);

        for (String node : nodes) {
            String[] parts = node.split(":");
            if(parts.length != 2) {
                throw new IllegalArgumentException(String.format("Invalid sentinel node configuration %s", node));
            }
            sentinelConfig.sentinel(parts[0], Integer.parseInt(parts[1]));
        }

        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(sentinelConfig);
        if (StringUtils.hasText(redisPassword)) {
            lettuceConnectionFactory.setPassword(redisPassword);
        }
        return lettuceConnectionFactory;
    }

}