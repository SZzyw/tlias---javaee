package com.way_ne.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude="
                + "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration"
})
public class JedisTest {
    @Autowired
    private JedisPool jedisPool;

    @Test
    public void testJedis() {
        try (Jedis jedis = jedisPool.getResource()) { // 获取redis操作对象
            jedis.set("name", "way_ne");
            System.out.println(jedis.get("name"));
        } // 释放资源
    }
}
