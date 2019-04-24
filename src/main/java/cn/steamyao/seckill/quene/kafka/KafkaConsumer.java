package cn.steamyao.seckill.quene.kafka;

import cn.steamyao.seckill.common.pojo.Result;
import cn.steamyao.seckill.common.pojo.SuccessKilled;
import cn.steamyao.seckill.service.ProductService;
import cn.steamyao.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;


/**
 * @author steamyao
 * @Package cn.steamyao.seckill.quene
 * @date 2019/4/13 16:06
 * @description  kafka 任务处理
 */
@Component
public class KafkaConsumer {

    private Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @Autowired
    private SeckillService seckillService;
    @Autowired
    private ProductService service;

    /**
     * 功能描述: 监听消息 并处理  模拟请求
     * @auther: steamyao
     * @date: 2019/4/13 16:10
     */
    @KafkaListener(topics = {"seckill"})
    public void receiveMessageOne(String msg){
        String[] array = msg.split(":");
        Result result = seckillService.startKilled(Long.parseLong(array[0]), Long.parseLong(array[1]));
        //logger.info("用户："+array[0]+"    抢到了ID为"+array[1]+"的商品");
    }

    /**
     * 功能描述: 监听消息 并处理  真实请求
     * @auther: steamyao
     * @date: 2019/4/13 16:10
     */
    @KafkaListener(topics = {"trueseckill"})
    public void receiveMessageTwo(String msg){
        String[] array = msg.split(":");
        long seckillId = Long.parseLong(array[1]);
        long userId =  Long.parseLong(array[0]);
        //todo 测试一下 时间好像有错
        service.save(new SuccessKilled(seckillId, userId, (short) 0, new Timestamp(System.currentTimeMillis())));
        logger.info("用户： "+userId+"已经成功加入数据库！");
    }
}
