package cn.steamyao.seckill.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author steamyao
 * @Package cn.steamyao.seckill.service
 * @date 2019/4/12 17:35
 * @description
 */
@Service
public class ServiceTest {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public void test(){
     redisTemplate.opsForValue().set("a","qwe");
     System.out.println(redisTemplate.opsForValue().get("a"));
    }
}
