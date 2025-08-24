package com.springboot.staking.shared.config;

import com.springboot.staking.shared.constant.RedisKey;
import java.time.Duration;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  private static final String REDISSON_HOST_PREFIX = "redis://";
  @Value("${spring.data.redis.host}")
  private String host;
  @Value("${spring.data.redis.port}")
  private int port;
  @Value("${spring.cache.redis.key-prefix}")
  private String keyPrefix;


  @Bean
  public LettuceConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory(host, port);
  }


  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory());
    GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();

    redisTemplate.setDefaultSerializer(serializer);
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(serializer);
    redisTemplate.setHashKeySerializer(new StringRedisSerializer());
    redisTemplate.setHashValueSerializer(serializer);

    return redisTemplate;
  }

  @Bean
  public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
    RedisCacheConfiguration defaults = RedisCacheConfiguration.defaultCacheConfig()
        .computePrefixWith(n -> keyPrefix + n + "::")
        .serializeKeysWith(
            RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
            new GenericJackson2JsonRedisSerializer()))
        .entryTtl(Duration.ofMinutes(1))
        .disableCachingNullValues();

    // 이름(또는 접두사) → TTL 매핑
    Map<String, Duration> ttlMap = Map.of(
        RedisKey.NODE.BALANCE, Duration.ofMinutes(1),
        RedisKey.NODE.TX, Duration.ofMinutes(1)
        // "price::" 같은 prefix 규칙을 함께 둘 수도 있음
    );

    return new RedisCacheManager(RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory),
        defaults) {
      @Override
      protected RedisCache createRedisCache(String name, RedisCacheConfiguration config) {
        Duration ttl = resolveTtl(name, ttlMap);
        RedisCacheConfiguration finalCfg = (ttl != null ? (config == null ? defaults
            : config).entryTtl(ttl)
            : (config == null ? defaults : config));
        return super.createRedisCache(name, finalCfg);
      }

      private Duration resolveTtl(String name, Map<String, Duration> map) {
        if (map.containsKey(name)) {
          return map.get(name);          // 완전 일치
        }
        return map.entrySet().stream()                            // 접두사 규칙
            .filter(e -> name.startsWith(e.getKey()))
            .map(Map.Entry::getValue)
            .findFirst().orElse(null);
      }
    };
  }
}
