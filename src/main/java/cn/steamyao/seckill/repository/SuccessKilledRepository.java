package cn.steamyao.seckill.repository;

import cn.steamyao.seckill.common.pojo.MultiKeysClass;
import cn.steamyao.seckill.common.pojo.SuccessKilled;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author steamyao
 * @Package cn.steamyao.seckill.repository
 * @date 2019/4/13 20:45
 * @description  踩坑：如果不加nativeQuery 则success_killed 应该改为数据库对应的实体类名
 */
public interface SuccessKilledRepository extends JpaRepository<SuccessKilled, MultiKeysClass>{

    /**
     * 功能描述: 查询数量
     * @param seckillId
     */
    @Query(value = "SELECT count(*) FROM success_killed WHERE seckill_id=?1",nativeQuery = true)
    long countById(long seckillId);

    /**
     * 功能描述  删除记录
     * @param seckillId
     */
    @Query(value = "DELECT FROM success_killed WHERE seckill_id=?1",nativeQuery = true)
    void delectCount(long seckillId);

}
