package cn.steamyao.seckill.repository;

import cn.steamyao.seckill.common.pojo.Seckill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author steamyao
 * @Package cn.steamyao.seckill.repository
 * @date 2019/4/13 9:40
 * @description   修改数据的方法要加  @Modifying 和 @Transactional
 */

public interface SeckillRepository extends JpaRepository<Seckill,Long>{

    /**
     * 功能描述:还原商品数量
     * @date: 2019/4/13 20:32
     * @param  seckillId
     */
    @Modifying@Transactional
    @Query(value = "UPDATE seckill SET number=100,version=0 WHERE seckill_id=?1",nativeQuery = true)
    void reBackCount(long seckillId);

    /**
     * 功能描述: 商品数量减一
     * @date: 2019/4/13 20:36
     * @param  seckillId
     */
    @Modifying@Transactional
    @Query(value = "UPDATE seckill  SET number=number-1 WHERE seckill_id=?1 AND number>0",nativeQuery = true)
    void descCount(long seckillId);

    /**
     * 功能描述 查询商品数量
     */
    @Query(value = "SELECT number FROM seckill WHERE seckill_id=?1",nativeQuery = true)
    int countById(long seckillId);


    /**
     * 功能描述: 还原所有商品的数量 和版本
     */
    @Modifying@Transactional
    @Query(value = "UPDATE seckill SET number=100,version=0",nativeQuery = true)
    void reBackAllCount();


    /**
     * 功能描述: 更改商品的数量
     */
    @Modifying@Transactional
    @Query(value = "UPDATE seckill SET number=?2 WHERE seckill_id=?1",nativeQuery = true)
    void updateCountById(long seckillId,int count);



    /**
     * 功能描述: 乐观锁一 版本号机制 商品数量并更新
     */
    @Modifying@Transactional
    @Query(value = "UPDATE seckill SET number=number-1,version=version+1 WHERE seckill_id=?1 AND version=?2",nativeQuery = true)
    void updateDBOCCOne(long seckillId,int version);


    /**
     * 功能描述: 乐观锁二  只要满足商品数量大于0 即可  商品数量并更新
     */
    @Modifying@Transactional
    @Query(value = "UPDATE seckill SET number=number-1 WHERE seckill_id=?1 AND number>0",nativeQuery = true)
    void updateDBOCCTwo(long seckillId);

    /**
     * 功能描述: 共享锁  悲观锁的一种   对查询不加锁 对更新 删除 加行锁
     * 在读取的行上设置一个共享锁，其他的session可以读这些行，但在你的事务提交之前不可以修改它们。
     * 如果这些行里有被其他的还没有提交的事务修改，你的查询会等到那个事务结束之后使用最新的值
     * 注意： 这里可能查询的值为null报错，所以不用int 而用Integer 在service层进行飞空判断
     */
    @Transactional
    @Query(value = "SELECT number FROM seckill WHERE seckill_id=?1 AND number>0 LOCK IN  SHARE MODE",nativeQuery = true)
    Integer selectPCCOne(long seckillId);

    /**
     * 功能描述: 排他所  悲观锁的一种   加行锁
     * 如果事务对数据加上排他锁之后，则其他事务不能对该数据加任何的锁。
     * 获取排他锁的事务既能读取数据，也能修改数据。
     * 和上一条一样
     */
    @Transactional
    @Query(value = "SELECT number FROM seckill WHERE seckill_id=?1 AND number>0 FOR UPDATE",nativeQuery = true)
    Integer selectPCCTwo(long seckillId);


}
