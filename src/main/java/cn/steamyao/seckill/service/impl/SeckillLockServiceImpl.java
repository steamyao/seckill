package cn.steamyao.seckill.service.impl;

import cn.steamyao.seckill.common.pojo.Result;
import cn.steamyao.seckill.lock.Redis.RedisLockUtils;
import cn.steamyao.seckill.lock.zookeeper.ZookeeperLock;
import cn.steamyao.seckill.service.SeckillLockService;
import cn.steamyao.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author steamyao
 * @Package cn.steamyao.seckill.service.impl
 * @date 2019/4/16 9:47
 * @description
 */
@Service
class SeckillLockServiceImpl implements SeckillLockService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeckillLockServiceImpl.class);
    private static ZookeeperLock zk = new ZookeeperLock("39.108.0.169","node");

    @Autowired
    private SeckillService seckillService;

    @Override
    public Result seckillRedisLock(long seckillId, long userId) {
        try {
            RedisLockUtils.lock();
            seckillService.simpelSeckill(seckillId,userId);
        } catch (InterruptedException e) {
            e.printStackTrace();
            LOGGER.info("很抱歉! "+userId+"您没有抢到！");
        }finally {
           RedisLockUtils.release();
        }
        return null;
    }



    @Override
    public Result seckillZkLock(long seckillId, long userId) {
        try {
           zk.lock();
            seckillService.simpelSeckill(seckillId,userId);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("很抱歉! "+userId+"您没有抢到！");
        }finally {
           zk.unlock();
        }
        return null;
    }



}
