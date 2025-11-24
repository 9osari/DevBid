package org.devbid.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.devbid.auction.infrastructure.messaging.AuctionRedisSubscriber;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration  //클래스를 자동으로 빈으로 등록
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.password}")
    private String redisPassword;

    //기본 RedisTemplate은 직렬화가 이상하게 되어서, 커스텀 설정 필수
    //Redis에 데이터를 저장하고 꺼내는 도구
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        //Key는 String으로 저장
        //Redis는 문자열만 저장 그래서 자바 객체를 문자열로 변환해야함
        template.setKeySerializer(new StringRedisSerializer()); //키는 그냥 문자열로 저장
        template.setHashKeySerializer(new StringRedisSerializer());

        //Value는 JSON으로 저장
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  //LocalDateTime 같은 시간 데이터를 JSON으로 변환
        objectMapper.disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        template.setValueSerializer(serializer); //값은 JSON 형태로 저장 (읽기 쉬움)
        template.setHashValueSerializer(serializer);

        return template;
    }

    //2. RedissonClient 설정 (분산락 용)
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + redisHost + ":" + redisPort);
                /*.setPassword(redisPassword);*/
        return Redisson.create(config);
    }

    @Bean
    public ChannelTopic auctionTopic() {
        //경매 관련 이벤트를 발행할 채널
        return new ChannelTopic("auction.events");  //채널 이름
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            AuctionRedisSubscriber subscriber,
            ChannelTopic auctionTopic) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(subscriber, auctionTopic); //구독 등록
        return container;
    }
}
