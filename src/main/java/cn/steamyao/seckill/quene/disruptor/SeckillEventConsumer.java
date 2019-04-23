package cn.steamyao.seckill.quene.disruptor;

import cn.steamyao.seckill.common.utils.SpringContextUtils;
import cn.steamyao.seckill.service.SeckillService;
import com.lmax.disruptor.EventHandler;

/**
 * 消费者(秒杀处理器)
 */
public class SeckillEventConsumer implements EventHandler<SeckillEvent> {
	
	private SeckillService seckillService = (SeckillService) SpringContextUtils.getBeanByName("seckillService");

	@Override
	public void onEvent(SeckillEvent seckillEvent, long seq, boolean bool) throws Exception {
		seckillService.startKilled(seckillEvent.getSeckillId(), seckillEvent.getUserId());
	}


}
