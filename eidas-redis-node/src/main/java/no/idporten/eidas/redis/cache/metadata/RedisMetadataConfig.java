package no.idporten.eidas.redis.cache.metadata;

import eu.eidas.auth.engine.metadata.EidasMetadataParametersI;
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
public class RedisMetadataConfig {



    @Bean(name = "redisMetadataTemplate")
    public RedisTemplate<String, EidasMetadataParametersI> redisMetadataTemplate(RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate<String, EidasMetadataParametersI> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JdkSerializationRedisSerializer());

        return template;
    }

}