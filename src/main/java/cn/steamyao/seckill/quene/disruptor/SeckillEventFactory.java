package cn.steamyao.seckill.quene.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * 事件生成工厂（用来获取事件对象）
 */
public class SeckillEventFactory implements EventFactory<SeckillEvent> {

    @Override
	public SeckillEvent newInstance() {
		return new SeckillEvent();
	}
}
