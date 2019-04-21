package cn.steamyao.seckill.before;

import cn.steamyao.seckill.repository.SeckillRepository;
import cn.steamyao.seckill.repository.SuccessKilledRepository;
import cn.steamyao.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author steamyao
 * @Package cn.steamyao.seckill.before
 * @date 2019/4/14 19:51
 * @description   在项目启动完成时，自动运行
 */
@Component
@Order(1)
public class BeforeSeckill implements ApplicationRunner {

    @Autowired
    private SeckillRepository seckillRepository;

    @Autowired
    private SuccessKilledRepository repository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //还原商品数据 删除所有秒杀成功记录
        seckillRepository.reBackAllCount();
        repository.deleteAll();
    }
}
