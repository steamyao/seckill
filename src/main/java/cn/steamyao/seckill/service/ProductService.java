package cn.steamyao.seckill.service;

import cn.steamyao.seckill.common.pojo.Seckill;

import java.util.List;

/**
 * @author steamyao
 * @Package cn.steamyao.seckill.service
 * @date 2019/4/17 21:03
 * @description  商品服务
 */
public interface ProductService {
    /**
     * 查询全部的秒杀商品
     */
    List<Seckill> getSeckillList();

    /**
     * 查询单个秒杀商品
     */
    Seckill getById(long seckillId);

    /**
     * 还原 单个商品数量和版本
     * @param seckillId
     * @return
     */
    void reBackKillCount(long seckillId);

    /**
     * 功能描述: 还原所有商品的数量和版本
     */
    void reBackKillCountAll();

    /**
     * 功能描述: 查询成功秒杀的记录
     * @param seckillId
     * @date: 2019/4/14 9:23
     */
    Long getSuccessKillCount(long seckillId);

    /**
     * 功能描述: 指定商品的数量减一
     * @date: 2019/4/14 9:23
     */
    void descCount(long seckillId);


    /**
     * 功能描述: 删除所有秒杀成功的记录
     * @date: 2019/4/14 9:23
     */
    void delectAllSuccess();

   /**
    * 功能描述: 根据ID查询商品数量
    * @date: 2019/4/18 19:53
    */
    int countById(long seckillId);

    /**
     * 功能描述: 保存商品
     * @date: 2019/4/18 19:53
     */
    void save(Object o);

    /**
     * 功能描述: 更新商品数量
     * @date: 2019/4/18 19:53
     */
    void updateCountById(long seckillId,int count);

    /**
     * 功能描述: 获取版本号与商品数量
     *    这里本来想写一个只查询数量和版本号的SQL，但JPA自定义QUERY只能返回int、INTEGER类型，
     *    两次查询的话又不太合适，所以直接查询对应id的所有字段
     * @date: 2019/4/19 20:53
     */
    Seckill getVsCount(long seckillId);

     /**
      *  乐观锁 一
      * @date: 2019/4/19 20:53
      */
    void updateDBOCCOne(long seckillId,int version);

    /**
     *  乐观锁 二
     * @date: 2019/4/19 20:53
     */
    void updateDBOCCTwo(long seckillId);


    /**
     *  用数据库悲观锁  之共享锁来查询数量
     * @date: 2019/4/20 20:26
     */
    Integer selectPCCOne(long seckillId);


    /**
     *  用数据库悲观锁  之排他锁来更新数量 对主键（即seckillId上行锁）
     * @date: 2019/4/20 20:26
     */
    Integer selectPCCTwo(long seckillId);
}
