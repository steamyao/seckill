package cn.steamyao.seckill.web;

import cn.steamyao.seckill.common.pojo.Result;
import cn.steamyao.seckill.service.ProductService;
import cn.steamyao.seckill.service.SeckillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author steamyao
 * @Package cn.steamyao.seckill.web
 * @date 2019/4/17 21:01
 * @description  普通秒杀实现
 *    http://localhost:8081/seckill/swagger-ui.html
 */
@Api(tags = "普通秒杀")
@RestController
@RequestMapping("/simple")
public class SimpleSeckillController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSeckillController.class);
    //根据硬件 cpu核数 线程数 来返回信息
    private static int corePoolSize = Runtime.getRuntime().availableProcessors();
    private static Executor executor;
    private long seckillCount;


    @Autowired
    private SeckillService seckillService;
    @Autowired
    private ProductService productService;

   //可能是由于  SingleThreadExecutor()  线程池特殊的原因，所以并发还没有出错
    @ApiOperation(value = "乐观锁 一")
    @PostMapping("/DbOOCOne")
    public Result dbOocOne(long  seckillId){
        LOGGER.info("开始乐观锁 一   秒杀");
        //只有一个线程  队列是最大是无穷大的 有任务时将任务加入队列
        executor = Executors.newSingleThreadExecutor();
        for (int i = 0; i < 500; i++) {
            final int userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                  seckillService.seckilDBOCCOne(seckillId,userId);
                }
            };

                executor.execute(task);
        }
        try {
            Thread.sleep(10000);
            getSeckillCount(seckillId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok("请求正确");
    }


    //这个乐观锁实现 没有完成预期的功能
    @ApiOperation(value = "乐观锁 二")
    @PostMapping("/DbOOCTwo")
    public Result dbOocTwo(long seckillId){
        LOGGER.info("开始乐观锁 二  秒杀");
        //只有一个线程  队列是最大是无穷大的 有任务时将任务加入队列
        executor = Executors.newSingleThreadExecutor();
        for (int i = 0; i < 500; i++) {
            final int userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    seckillService.seckilDBOCCTwo(seckillId,userId);
                }
            };

            executor.execute(task);
        }
        try {
            Thread.sleep(10000);
            getSeckillCount(seckillId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok("请求正确");

    }

    //超卖 卖了107件
    @ApiOperation(value = "悲观锁一  ")
    @PostMapping("/DbPCCOne")
    public Result DbPCC(long seckillId){
        LOGGER.info("开始悲观锁 一   秒杀");
        //核心线程 与 最大线程数量为10 队列可以为无穷大 默认的拒绝策略 AbortPolicy();
         executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 500; i++) {
            final int userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    seckillService.seckilDBPCCOne(seckillId,userId);
                }
            };

            executor.execute(task);
        }
        try {
            Thread.sleep(11000);
            getSeckillCount(seckillId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.ok("请求正确");

    }


   //超卖 105件
    @ApiOperation(value = "悲观锁二  ")
    @PostMapping("/DbPCCTwo")
    public Result DbPCCTwo(long seckillId){
        LOGGER.info("开始悲观锁 二   秒杀");
        //核心线程 与 最大线程数量为10 队列可以为无穷大 默认的拒绝策略 AbortPolicy();
        executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 500; i++) {
            final int userId = i;
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    seckillService.seckilDBPCCTwo(seckillId,userId);
                }
            };

            executor.execute(task);
        }
        try {
            Thread.sleep(11000);
            getSeckillCount(seckillId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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




}
