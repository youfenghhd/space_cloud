package com.hhd;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

@SpringBootTest
class SpaceCloudApplicationTests {
    @Test
    void contextLoads() {
    }

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Test
    void test(){
        redisTemplate.opsForValue().set("123456","143526412",1, TimeUnit.HOURS);
        String s = redisTemplate.opsForValue().get("123456");

        System.out.println("1:"+(s == null));
    }

}
