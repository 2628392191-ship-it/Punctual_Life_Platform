package com.sky.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class SpringRedisConfiguration {
    /*
        1.RedisConnectionFactory 是获取RedisConnection对象的，RedisConnection相当于
        jdbc中的连接对象Connection表示Redis进行连接
        */
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        //创建Redis模板对象
        //TODO：分析spring默认的转换器JdkSerializationRedisSerializer
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();

        //解决乱码问题和字符串序列化问题
        //默认的Key序列化器为：JdkSerializationRedisSerializer
        //StringRedisSerializer支持字符串类型的转化，而且默认使用UTF-8编码
        //下面代码的意思是使用StringRedisSerializer序列化器替换默认的Key序列化器JdkSerializationRedisSerializer
        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new StringRedisSerializer());
//        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        //解决自动转换类型问题（Value值转为其他类型）
        // 设置 value 序列化器为支持对象自动序列化
        // 创建自定义 ObjectMapper 支持时间格式处理
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


//        GenericJackson2JsonRedisSerializer serializer=new GenericJackson2JsonRedisSerializer();
//        redisTemplate.setValueSerializer(serializer);
//        redisTemplate.setHashValueSerializer(serializer);


        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }
}
