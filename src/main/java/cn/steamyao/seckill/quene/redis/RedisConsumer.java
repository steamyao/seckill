package cn.steamyao.seckill.quene.redis;

import cn.steamyao.seckill.common.pojo.Result;
import cn.steamyao.seckill.common.utils.RedisUtil;
import cn.steamyao.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 消费者
 * @author 科帮网 By https://blog.52itstyle.com
 */
@Service
public class RedisConsumer {
	
	@Autowired
	private SeckillService seckillService;

    public void receiveMsg(String message) {
        //收到通道的消息之后执行秒杀操作(超卖)
    	String[] array = message.split(":");
        Result result = seckillService.startKilled(Long.parseLong(array[0]), Long.parseLong(array[1]));

    }
}