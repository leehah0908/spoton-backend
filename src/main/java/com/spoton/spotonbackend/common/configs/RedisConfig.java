package com.spoton.spotonbackend.common.configs;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.host}")
    private String redisHost;

    // 리프레시 토큰 (이메일 - 리프레시 토큰)
    @Bean
    @Qualifier("refresh-token-db")
    public RedisConnectionFactory refreshTokenFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisHost, redisPort);
        redisStandaloneConfiguration.setDatabase(0);
        return new LettuceConnectionFactory(redisStandaloneConfiguration);

    }

    // 이메일 인증 (이메일 - 인증번호)
    @Bean
    @Qualifier("email-certification-db")
    public RedisConnectionFactory emailCertificationFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisHost, redisPort);
        redisStandaloneConfiguration.setDatabase(1);
        return new LettuceConnectionFactory(redisStandaloneConfiguration);

    }

    @Bean
    @Qualifier("baseball-game-db")
    public RedisConnectionFactory baseballGameFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisHost, redisPort);
        redisStandaloneConfiguration.setDatabase(1);
        return new LettuceConnectionFactory(redisStandaloneConfiguration);

    }

    // ======================================================== 템플릿 ========================================================
    @Bean
    @Qualifier("redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(@Qualifier("refresh-token-db") RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setConnectionFactory(factory);

        return template;
    }

    @Bean
    @Qualifier("emailCertificationTemplate")
    public RedisTemplate<String, Integer> emailCertificationTemplate(@Qualifier("email-certification-db") RedisConnectionFactory factory) {
        RedisTemplate<String, Integer> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setConnectionFactory(factory);

        return template;
    }
}
