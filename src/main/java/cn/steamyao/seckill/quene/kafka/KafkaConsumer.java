package cn.steamyao.seckill.quene.kafka;

import cn.steamyao.seckill.common.pojo.Result;
import cn.steamyao.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


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

    /**
     *
     * 功能描述: 监听消息 并处理
     *
     * @auther: steamyao
     * @date: 2019/4/13 16:10
     */
    @KafkaListener(topics = {"seckill"})
    public void receiveMessage(String msg){
        String[] array = msg.split(":");
        Result result = seckillService.startKilled(Long.parseLong(array[0]), Long.parseLong(array[1]));
        //logger.info("用户："+array[0]+"    抢到了ID为"+array[1]+"的商品");
    }
}
