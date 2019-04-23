package cn.steamyao.seckill.service.impl;

import cn.steamyao.seckill.common.pojo.Seckill;
import cn.steamyao.seckill.common.pojo.SuccessKilled;
import cn.steamyao.seckill.repository.SeckillRepository;
import cn.steamyao.seckill.repository.SuccessKilledRepository;
import cn.steamyao.seckill.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author steamyao
 * @Package cn.steamyao.seckill.service.impl
 * @date 2019/4/17 21:04
 * @description
 */
@Service("productService")
public class ProductServiceImpl implements ProductService{

    @Autowired
    private SeckillRepository seckillRepository;
    @Autowired
    private SuccessKilledRepository successKilledRepository;
    private Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);


    @Override
    public List<Seckill> getSeckillList() {
        return seckillRepository.findAll();
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillRepository.findById(seckillId).get();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reBackKillCount(long seckillId) {
        seckillRepository.reBackCount(seckillId);

    }

    @Override
    public void reBackKillCountAll() {
        seckillRepository.reBackAllCount();
    }

    @Override
    public Long getSuccessKillCount(long seckillId) {
        return successKilledRepository.countById(seckillId);
    }

    @Override
    public void descCount(long seckillId) {
        seckillRepository.descCount(seckillId);
    }

    @Override
    public void delectAllSuccess() {
        successKilledRepository.deleteAll();
    }

    @Override
    public int countById(long seckillId) {
        return seckillRepository.countById(seckillId);
    }

    @Override
    public void save(Object o) {
        if (o instanceof SuccessKilled){
            successKilledRepository.save((SuccessKilled)o);
        }else{
            logger.info("存入数据库失败");
        }
    }

    @Override
    public void updateCountById(long seckillId,int count) {
        seckillRepository.updateCountById(seckillId,count);
    }

    @Override
    public Seckill getVsCount(long seckillId) {
        return seckillRepository.findById(seckillId).get();
    }

    @Override
    public void updateDBOCCOne(long seckillId, int version) {
        seckillRepository.updateDBOCCOne(seckillId,version);
    }

    @Override
    public void updateDBOCCTwo(long seckillId) {
        seckillRepository.updateDBOCCTwo(seckillId);
    }

    @Override
    public Integer selectPCCOne(long seckillId) {
        return seckillRepository.selectPCCOne(seckillId);
    }

    @Override
    public Integer selectPCCTwo(long seckillId) {
        return seckillRepository.selectPCCTwo(seckillId);
    }

}
