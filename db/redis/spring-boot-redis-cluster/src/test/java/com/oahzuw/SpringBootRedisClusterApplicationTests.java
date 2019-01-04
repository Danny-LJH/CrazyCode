package com.oahzuw;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootRedisClusterApplicationTests {
    /**
     * 指定泛型不会出现 "\xac\xed\x00\x05t\x00\x01" 前缀
     * 项目开发过程中直接注入RedisTemplate即可
     */
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * ValueOperations 对应 redis string类型, 对string类型进行操作
     */
    private ValueOperations<String, String> valueOperations;

    /**
     * 获取 valueOperations 方便使用
     */
    @Before
    public void init() {
        valueOperations = redisTemplate.opsForValue();
    }

    /**
     * 赋值、取值操作
     */
    @Test
    public void contextLoads() {
        // 集群中赋值
        valueOperations.set("z", "y");
        // 取值输出
        System.out.println(valueOperations.get("z"));
    }
}

