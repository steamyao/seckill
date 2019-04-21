package cn.steamyao.seckill.test;

import cn.steamyao.seckill.repository.SeckillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author steamyao
 * @Package cn.steamyao.seckill.test
 * @date 2019/4/13 20:21
 * @description
 */
@Service
public class DbTest {

    @Autowired
    private SeckillRepository sr;


    public void test(){

    }
}
