package cn.steamyao.seckill.common.pojo;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author steamyao
 * @Package cn.steamyao.seckill.common.pojo
 * @date 2019/4/13 20:52
 * @description    由于 SuccessKilledRepository 有两个主键 所以要用到主键复合
 * https://blog.csdn.net/qq_35056292/article/details/77892012
 */
public class MultiKeysClass implements Serializable {

    private long userId;

    private long seckillId;

    public MultiKeysClass() {
    }

    public MultiKeysClass(long userId, long seckillId) {
        this.userId = userId;
        this.seckillId = seckillId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MultiKeysClass)) return false;
        MultiKeysClass that = (MultiKeysClass) o;
        return userId == that.userId &&
                seckillId == that.seckillId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, seckillId);
    }
}
