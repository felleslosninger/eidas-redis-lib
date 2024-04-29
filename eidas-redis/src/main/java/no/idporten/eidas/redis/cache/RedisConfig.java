package no.idporten.eidas.redis.cache;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
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

    public int getRedisPort() {
        return Integer.parseInt(redisPort);
    }
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisHost, getRedisPort());
        if(StringUtils.hasText(redisPassword)) {
            lettuceConnectionFactory.setPassword(redisPassword);
        }
        return lettuceConnectionFactory;
    }


}