package cn.steamyao.seckill.quene.disruptor;

import java.io.Serializable;

/**
 * 事件对象（秒杀事件）
 * 用来传递消息
 */
public class SeckillEvent implements Serializable {
	private static final long serialVersionUID = 1L;
	private long seckillId;
	private long userId;
	
	public SeckillEvent(){
		
	}

    public SeckillEvent(long seckillId, long userId) {
        this.seckillId = seckillId;
        this.userId = userId;
    }

    public long getSeckillId() {
		return seckillId;
	}

	public void setSeckillId(long seckillId) {
		this.seckillId = seckillId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
	
}