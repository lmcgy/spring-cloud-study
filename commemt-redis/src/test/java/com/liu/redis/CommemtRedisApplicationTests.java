package com.liu.redis;

import com.liu.redis.entity.User;
import com.liu.redis.utills.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CommemtRedisApplicationTests {


    @Autowired
    RedisService redisService;


    @Test
    void contextLoads() {
    }


    @Test
    void testRedis(){

        User liumiao = new User("liumiao", 26);
        redisService.set("user:name:1",liumiao);


        Object o = redisService.get("user:name:1");
        System.out.println(o);
    }




}
