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
        if(StringUtils.isEmpty(sentinelNodes)) {
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
        List<String> nodes = Arrays.asList(sentinelNodes.split(","));
        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
                .master(sentinelMaster);

        for (int i = 1; i < nodes.size(); i++) {
            String[] parts = nodes.get(i).split(":");
            sentinelConfig.sentinel(parts[0], Integer.parseInt(parts[1]));
        }

        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(sentinelConfig);
        if (StringUtils.hasText(redisPassword)) {
            lettuceConnectionFactory.setPassword(redisPassword);
        }
        return lettuceConnectionFactory;
    }

}