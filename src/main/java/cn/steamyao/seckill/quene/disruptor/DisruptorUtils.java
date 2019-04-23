package cn.steamyao.seckill.quene.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author steamyao
 * @Package cn.steamyao.seckill.quene.disruptor
 * @date 2019/4/22 10:59
 * @description     https://blog.csdn.net/wangshuminjava/article/details/81046963
 *                  http://www.importnew.com/27652.html
 */
//TODO 不能实例化 工具类  已解决：在消费者中需要通过spring.getbean()获取 servicebean  而spring工具类没有配置扫描
public class DisruptorUtils {

    private  static Disruptor<SeckillEvent> disruptor;
    static{
        SeckillEventFactory factory = new SeckillEventFactory();
        // RingBuffer 大小，必须是 2 的 N 次方；
        int ringBufferSize = 1024;
        Executor executors = Executors.newSingleThreadExecutor();
        //没有指定等待策略  废弃了？
        disruptor = new Disruptor<SeckillEvent>(factory,ringBufferSize,executors);
        disruptor.handleEventsWith(new SeckillEventConsumer());
        disruptor.start();
    }

    public static void producer(SeckillEvent event){
        RingBuffer<SeckillEvent> ringBuffer = disruptor.getRingBuffer();
        SeckillEventProducer producer = new SeckillEventProducer(ringBuffer);
        producer.seckill(event.getSeckillId(),event.getUserId());
    }
}
