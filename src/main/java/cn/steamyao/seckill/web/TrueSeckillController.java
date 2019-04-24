package cn.steamyao.seckill.web;

import cn.steamyao.seckill.common.pojo.Result;
import cn.steamyao.seckill.common.utils.RedisUtil;
import cn.steamyao.seckill.quene.kafka.KafkaSender;
import cn.steamyao.seckill.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author steamyao
 * @Package cn.steamyao.seckill.web
 * @date 2019/4/23 10:24
 * @description     配合JMester 模拟 秒杀场景
 *                http://localhost:8081/seckill/swagger-ui.html
 *
 *
 *    Jmeter教程：https://blog.csdn.net/yanghan1222/article/details/80289576
 */

@Api(tags = "模拟真实秒杀")
@RestController
@RequestMapping("/true")
public class TrueSeckillController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrueSeckillController.class);
    //标志位  1表示 redis 里库存还有   0 表示没有
    private AtomicInteger state  = new AtomicInteger(1);
     @Autowired
     private RedisUtil redisUtil;
     @Autowired
     private KafkaSender kafkaSender;
     @Autowired
     private ProductService service;


     @PostConstruct
     public void serNum(){
         redisUtil.set("1000",100);
         redisUtil.set("1001",100);
         redisUtil.set("1002",100);
         redisUtil.set("1003",100);
         redisUtil.set("userId",0);
     }
     /**
      *问题： 1  同一个账号，一次性发出多个请求
      *       2. 多个账号，一次性发送多个请求         弹出验证码
      *       3. 多个账号，不同IP发送不同请求         排除掉一些僵尸账户
      *
      *
      */
    @ApiOperation(value = "真实秒杀")
    @PostMapping("/seckill")
    public Result seckill(long seckillId,long userId){
        if (state.intValue()==0){
            return  Result.ok("很抱歉！  用户：   "+userId+"   没有抢到");
        }else {
            long  num =  redisUtil.decr(String.valueOf(seckillId),1);
            if (num<0){
                state.set(0);
                service.updateCountById(seckillId,0);
                return  Result.ok("很抱歉！  用户：   "+userId+"   没有抢到");
            }
            LOGGER.info(" "+userId);
            kafkaSender.sendMsg("trueseckill",userId+":"+seckillId);
            return  Result.ok("恭喜！ 用户: "+userId+"       抢到商品了！");
        }

    }

    //用来写入txt 设置线程个数 通过Jmeter 来读取txt文件 设置每个并发独立的userId
    //具体可以查看上面的教程
    @ApiOperation(value = "写入线程数")
    @PostMapping("/write")
    public void write(int number) throws IOException {
        //线程数  1---number
        //文件 在 当前 project 里面
        FileOutputStream fos = new FileOutputStream("a.txt");
        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
        for (int i = 1; i < number+1; i++) {
            osw.write(i+"\r\n");
        }
        osw.flush(); // 把缓存区内容压入文件
        osw.close(); // 最后记得关闭文件
        System.out.println("写入数据成功");
    }



    @ApiOperation(value = "还原商品数据")
    @PostMapping("/zzzzzz")
    public Result run(){
         state.set(1);
        service.reBackKillCountAll();
        service.delectAllSuccess();
        redisUtil.set("1000",100);
        redisUtil.set("1001",100);
        redisUtil.set("1002",100);
        redisUtil.set("1003",100);
        LOGGER.info("还原商品数据成功！");
        return Result.ok("还原商品数据成功");
    }
}
