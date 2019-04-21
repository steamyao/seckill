package cn.steamyao.seckill.service;

import cn.steamyao.seckill.common.pojo.Result;
import cn.steamyao.seckill.common.pojo.Seckill;
import cn.steamyao.seckill.common.pojo.SuccessKilled;

import java.util.List;

/**
 * @author steamyao
 * @Package cn.steamyao.seckill.service
 * @date 2019/4/13 9:56
 * @description
 */
public interface SeckillService {


    /**
     * 加锁的秒杀  从数据库读入商品数量       难以实现----------
     */
    Result startSeckil(long seckillId,long userId);

    /**
     *加锁的秒杀  要求预先设定商品数量
     */
    Result startKilled(long seckillId,long userId);


    /**
     * 普通的秒杀，不加锁
     */
    Result simpelSeckill(long seckillId,long userId);


    /**
     * 秒杀 二、程序锁
     * @param seckillId
     * @param userId
     * @return
     */
    Result startSeckilLock(long seckillId,long userId);

    /**
     * 秒杀 二、程序锁AOP
     * @param seckillId
     * @param userId
     * @return
     */
    Result startSeckilAopLock(long seckillId,long userId);

    /**
     * 数据库悲观锁 一
     */
    Result seckilDBPCCOne(long seckillId,long userId);


    /**
     * 数据库悲观锁 二
     */
    Result seckilDBPCCTwo(long seckillId,long userId);

    /**
     *  数据库乐观锁一
     */
    Result seckilDBOCCOne(long seckillId,long userId);


    /**
     *  数据库乐观锁 二
     */
    Result seckilDBOCCTwo(long seckillId,long userId);






    /**
     * 功能描述: 秒杀成功的用户写入数据库
     * @date: 2019/4/18 18:56
     */
    void writeSuccessDb();

    /**
     * 功能描述: 将商品数据写入数据库
     * @date: 2019/4/18 18:56
     */
    void writeCountDb();

    /**
     * 功能描述: 还原商品数据
     * @date: 2019/4/18 18:56
     */
    void setNumber();
}
