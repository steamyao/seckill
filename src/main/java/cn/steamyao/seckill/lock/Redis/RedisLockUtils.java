package cn.steamyao.seckill.lock.Redis;

import redis.clients.jedis.Jedis;

/**
 * @author steamyao
 * @Package cn.steamyao.seckill.lock.Redis
 * @date 2019/4/17 20:04
 * @description
 */
public class RedisLockUtils {

    private static Jedis jedis = new Jedis("39.108.0.169",6379);
    private static JedisLock redislock = new JedisLock(jedis,"seckill",10,500);

    public  static void lock() throws InterruptedException {
        redislock.acquire();
    }

    public static void release(){
        redislock.release();
    }

}
