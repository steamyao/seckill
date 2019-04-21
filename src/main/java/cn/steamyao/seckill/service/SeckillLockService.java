package cn.steamyao.seckill.service;

import cn.steamyao.seckill.common.pojo.Result;

/**
 * @author steamyao
 * @Package cn.steamyao.seckill.service
 * @date 2019/4/16 9:44
 * @description
 */
public interface SeckillLockService {

    /**
     * 功能描述: redis 分布式锁秒杀
     */
    Result seckillRedisLock(long seckillId,long userId);

    /**
     * 功能描述: zookeeper  分布式锁秒杀
     */
    Result seckillZkLock(long seckillId,long userId);
}
