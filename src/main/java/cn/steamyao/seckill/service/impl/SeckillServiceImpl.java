package cn.steamyao.seckill.service.impl;

import cn.steamyao.seckill.common.aop.ServiceLimit;
import cn.steamyao.seckill.common.aop.ServiceLock;
import cn.steamyao.seckill.common.pojo.Result;
import cn.steamyao.seckill.common.pojo.Seckill;
import cn.steamyao.seckill.common.pojo.SuccessKilled;
import cn.steamyao.seckill.quene.kafka.KafkaConsumer;
import cn.steamyao.seckill.repository.SeckillRepository;
import cn.steamyao.seckill.repository.SuccessKilledRepository;
import cn.steamyao.seckill.service.ProductService;
import cn.steamyao.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author steamyao
 * @Package cn.steamyao.seckill.service.Iml
 * @date 2019/4/13 9:56
 * @description   https://blog.csdn.net/wqh8522/article/details/79224290 定时任务 单线程
 */
@Service("seckillService")
@EnableScheduling    //定时执行任务
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private ProductService service;

    private AtomicInteger number = new AtomicInteger(100);
    private AtomicBoolean isFirst = new AtomicBoolean(true);
    private Logger logger = LoggerFactory.getLogger(SeckillServiceImpl.class);
    private Semaphore semaphore = new Semaphore(100, false);
    private CountDownLatch latch = new CountDownLatch(1);
    private  LinkedBlockingQueue<SuccessKilled> queue = new LinkedBlockingQueue<SuccessKilled>();
    private ReentrantLock lock = new ReentrantLock(false);
    //为了在 writeCountDb 对应的商品ID 设置其数目为0
    private int id = 0;

  /* @PostConstruct
   public void getCount(){
       number = new AtomicInteger(seckillRepository.countById(seckillId));
   }*/


    @Override
    public Result startSeckil(long seckillId, long userId) {
        return null;
    }

    @Transactional
    public Result startKilled(long seckillId, long userId) {
        if(id==0){
            this.id = (int)seckillId;
        }
        if (number.intValue() > 0) {
            try {
                semaphore.acquire();
                queue.add(new SuccessKilled(seckillId, userId, (short) 0, new Timestamp(System.currentTimeMillis())));
                number.getAndDecrement();
                logger.info("用户：" + userId + "    抢到了ID为" + seckillId + "的商品");
                semaphore.release();
                return Result.ok("成功");
            } catch (InterruptedException e) {
                logger.info("用户：" + userId + "  没有抢到商品");
                e.printStackTrace();
                return Result.error("失败");
            }
        } else {
            logger.info("用户：" + userId + "  没有抢到商品");
            return Result.error("失败");
        }
    }


    private  int count = 100;
    @Override
    public Result simpelSeckill(long seckillId, long userId) {
        if(id==0){
            this.id = (int)seckillId;
        }
       if(count>0){
           queue.add(new SuccessKilled(seckillId, userId, (short) 0, new Timestamp(System.currentTimeMillis())));
           logger.info("用户：" + userId + "    抢到了ID为" + seckillId + "的商品");
           count--;
       }else {
           logger.info("用户：" + userId + "没有抢到商品");
       }
        return null;
    }


    /**
     * 功能描述: 设置事务隔离级别为可重复读  避免脏读 和不可重复读
     * 只设置 Transactional 的话，会出现超卖101，百度了一下，也没找到默认的隔离级别是什么
     * https://blog.csdn.net/qq_36647176/article/details/85559873
     * @date: 2019/4/22 9:25
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Result startSeckilLock(long seckillId, long userId) {
        try {
            lock.lock();
            count = service.countById(seckillId);
            if (count>0){
                service.descCount(seckillId);
                queue.add(new SuccessKilled(seckillId, userId, (short) 0, new Timestamp(System.currentTimeMillis())));
                logger.info("用户：" + userId + "    抢到了ID为" + seckillId + "的商品");
            }else {
                logger.info("用户：" + userId + "没有抢到商品");
            }
        } catch (Exception e){
            e.printStackTrace();
            logger.info("用户：" + userId + "没有抢到商品");
        }finally {
         lock.unlock();
        }
        return null;
    }

    @Override
    @ServiceLock
    @Transactional            //加了AOP锁，就直接调用不加锁的方法
    public Result startSeckilAopLock(long seckillId, long userId) {
        return simpelSeckill(seckillId,userId);
    }


    private Integer c;
    @Override
    @Transactional   //todo 加了事务 会出现死锁与事务回退（不会超卖）   不加会出现超卖
    public Result seckilDBPCCOne(long seckillId, long userId) {
        //每次都要查数据库 所以不用院子类也可以
        c = service.selectPCCOne(seckillId);
        if (c!=null&&c.intValue() > 0) {
            //todo 这里可能会导致库存为负数
            service.descCount(seckillId);
            queue.add(new SuccessKilled(seckillId, userId, (short) 0, new Timestamp(System.currentTimeMillis())));
            logger.info("用户：" + userId + "    抢到了ID为" + seckillId + "的商品");

        }else {
            logger.info("用户：" + userId + "没有抢到商品");
        }

        return null;
    }

    @Override
    @Transactional
    public Result seckilDBPCCTwo(long seckillId, long userId) {
        c = service.selectPCCTwo(seckillId);
        if (c!=null&&c.intValue() > 0) {
            service.descCount(seckillId);
            queue.add(new SuccessKilled(seckillId, userId, (short) 0, new Timestamp(System.currentTimeMillis())));
            logger.info("用户：" + userId + "    抢到了ID为" + seckillId + "的商品");

        }else {
            logger.info("用户：" + userId + "没有抢到商品");
        }

        return null;

    }


    private int version;
    @Override
    public Result seckilDBOCCOne(long seckillId, long userId) {
           Seckill seckill = service.getVsCount(seckillId);
           if (seckill.getNumber()>0){
               try {
                   version = seckill.getVersion();
                   service.updateDBOCCOne(seckillId,version);
                   queue.add(new SuccessKilled(seckillId, userId, (short) 0, new Timestamp(System.currentTimeMillis())));
                   logger.info("用户：" + userId + "    抢到了ID为" + seckillId + "的商品");
               } catch (Exception e){
                   logger.info("用户：" + userId + "没有抢到商品");
               }
           }else {
                   logger.info("用户：" + userId + "没有抢到商品");
           }
        return null;
    }

    //todo 超卖严重
    @Override
    @Transactional
    public Result seckilDBOCCTwo(long seckillId, long userId) {
        try {
            service.updateDBOCCTwo(seckillId);
            queue.add(new SuccessKilled(seckillId, userId, (short) 0, new Timestamp(System.currentTimeMillis())));
            logger.info("用户：" + userId + "    抢到了ID为" + seckillId + "的商品");
        } catch (Exception e){
            logger.info("用户：" + userId + "没有抢到商品");
        }

        return null;
    }




    //todo 定时任务不行啊
    @Override
    @Scheduled(fixedDelay = 20000)    //20s执行一次    单线程
    public void writeSuccessDb() {
        if (queue.size()>0) {
            for (SuccessKilled o : queue) {
               service.save(o);
            }
            queue.clear();
        }

    }

    @Override
    @Scheduled(fixedDelay = 20000)    //20s执行一次
    public void writeCountDb() {
          if (number.intValue()==0 || count==0){
              service.updateCountById(id,0);
          }

    }





    @Override
    public void setNumber() {
        this.number = new AtomicInteger(100);
        this.count = 100;
        this.id = 0;
        this.c = null;
    }


}