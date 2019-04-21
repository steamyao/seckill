package cn.steamyao.seckill.web;

import cn.steamyao.seckill.common.pojo.Result;
import cn.steamyao.seckill.quene.kafka.KafkaSender;
import cn.steamyao.seckill.quene.redis.RedisSender;
import cn.steamyao.seckill.service.ProductService;
import cn.steamyao.seckill.service.SeckillLockService;
import cn.steamyao.seckill.service.SeckillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author steamyao
 * @Package cn.steamyao.seckill.web
 * @date 2019/4/12 19:11
 * @description 分布式秒杀
 *   http://localhost:8081/seckill/swagger-ui.html
 */
@Api(tags = "分布式秒杀")
@RestController
@RequestMapping("/distribute")
public class DistributeSeckillController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DistributeSeckillController.class);

    //根据硬件 cpu核数 线程数 来返回信息
    private static int corePoolSize = Runtime.getRuntime().availableProcessors();
    private long seckillCount;
    private long startTime;
    private static ThreadPoolExecutor executor;


    @Autowired
    private RedisSender redisSender;
    @Autowired
    private KafkaSender kafkaSender;
    @Autowired
    private SeckillService seckillService;
    @Autowired
    private SeckillLockService lockService;
    @Autowired
    private ProductService productService;

    @ApiOperation(value = "kafka秒杀")
    @PostMapping("/kafkaSeckill")
    public Result kafkaSeckill(long seckillId){
        LOGGER.info("开始kafka秒杀");
        //每次请求都是新建线程池
        executor = getThredPool(executor);
        for (int i = 0; i < 500; i++) {
            final int userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    kafkaSender.sendMsg("seckill",userId+":"+seckillId);
                }
            };
            //对抛出线程池的任务进行处理  排名300外的直接抛出
            try{
              executor.execute(task);
            } catch (RejectedExecutionException e) {
               LOGGER.info("很抱歉! "+userId+"您没有抢到！");
            }
        }
        try {
            Thread.sleep(4000);
            getSeckillCount(seckillId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
            executor.shutdown();
        return Result.ok("请求正确");
    }

    @ApiOperation(value = "redis 订阅监听模式")
    @PostMapping("/redisSeckill")
    public Result redisSeckill(long seckillId){
        LOGGER.info("开始redis 订阅监听模式秒杀");
        //每次请求都是新建线程池
        executor = getThredPool(executor);
        for (int i = 0; i < 500; i++) {
            final int userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    redisSender.sendMsg("seckill",userId+":"+seckillId);
                }
            };
            //对抛出线程池的任务进行处理  排名300外的直接抛出
            try{
                executor.execute(task);
            } catch (RejectedExecutionException e) {
                LOGGER.info("很抱歉! "+userId+"您没有抢到！");
            }
        }
        try {
            Thread.sleep(10000);
            getSeckillCount(seckillId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        return Result.ok("请求正确");
    }

    @ApiOperation(value = "redis 分布式锁")
    @PostMapping("/redisLockSeckill")
    public Result redisLockSeckill(long seckillId){
        LOGGER.info("开始redis 分布式锁秒杀");
        //每次请求都是新建线程池
        executor = getThredPool(executor);
        for (int i = 0; i < 500; i++) {
            final int userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    lockService.seckillRedisLock(seckillId,userId);
                }
            };
            //对抛出线程池的任务进行处理  排名300外的直接抛出
            try{
                executor.execute(task);
            } catch (RejectedExecutionException e) {
                LOGGER.info("很抱歉! "+userId+"您没有抢到！");
            }
        }
        try {
            Thread.sleep(80000);
            getSeckillCount(seckillId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        return Result.ok("请求正确");
    }


    @ApiOperation(value = "zookeeper 分布式锁")
    @PostMapping("/zkLockSeckill")
    public Result zkLockSeckill(long seckillId){
        LOGGER.info("开始zookeeper  分布式锁秒杀");
        //每次请求都是新建线程池
        executor = getThredPool(executor);
        for (int i = 0; i < 500; i++) {
            final int userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    lockService.seckillZkLock(seckillId,userId);
                }
            };
            //对抛出线程池的任务进行处理  排名300外的直接抛出
            try{
                executor.execute(task);
            } catch (RejectedExecutionException e) {
                LOGGER.info("很抱歉! "+userId+"您没有抢到！");
            }
        }
        try {
            Thread.sleep(10000);
            getSeckillCount(seckillId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        return Result.ok("请求正确");
    }



    @ApiOperation(value = "还原商品数据")
    @PostMapping("/zzzzzz")
    public Result run(){
        //还原商品数据 删除所有秒杀成功记录
        productService.reBackKillCountAll();
        productService.delectAllSuccess();
        seckillService.setNumber();
        LOGGER.info("还原商品数据成功！");
        return Result.ok("还原商品数据成功！");
    }



    /**
     * 功能描述: 获取秒杀成功的数量
     */
    public void getSeckillCount(long seckillId){
        seckillCount = productService.getSuccessKillCount(seckillId);
        LOGGER.info("秒杀结束，一共秒杀出{}件商品",seckillCount);
    }


    /**
     * 功能描述: 设置线程池的核心线程池数量 最大线程数量  超时时间为 10s
     * 队列   拒绝策略：到达最大线程数量时，丢弃任务 并抛出异常   每次请求都新建线程池
     * @auther: steamyao
     * @date: 2019/4/13 15:39
     */
    private   ThreadPoolExecutor getThredPool(ThreadPoolExecutor executor){
        executor = new ThreadPoolExecutor(corePoolSize, corePoolSize + 1, 10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(300), new ThreadPoolExecutor.AbortPolicy());
        return executor;
    }

    private void timeStart(){
        startTime = System.nanoTime();
    }
    private  void timeEnd(){
        System.out.println("花费实际:"+(System.nanoTime()-startTime));
    }

}
