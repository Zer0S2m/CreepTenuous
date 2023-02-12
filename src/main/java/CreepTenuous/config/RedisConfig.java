package CreepTenuous.config;

import CreepTenuous.providers.redis.domain.JwtRedisData;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
public class RedisConfig {
    @Bean
    public ReactiveRedisTemplate<String, JwtRedisData> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        return new ReactiveRedisTemplate<String, JwtRedisData>(
                factory,
                RedisSerializationContext.fromSerializer(new Jackson2JsonRedisSerializer(JwtRedisData.class))
        );
    }
}
