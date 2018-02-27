package com.imooc.base;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author gusuchen
 * Created in 2018-01-18 12:25
 * Description: sesson会话
 * Modified by:
 */
//@Configuration
//@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 86400) // 生效时间 86400s = 1天
public class RedisSessionConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }
}
