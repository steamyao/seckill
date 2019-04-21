package cn.steamyao.seckill.test;

import cn.steamyao.seckill.common.pojo.Seckill;
import cn.steamyao.seckill.repository.SeckillRepository;
import cn.steamyao.seckill.repository.SuccessKilledRepository;
import cn.steamyao.seckill.service.SeckillLockService;
import cn.steamyao.seckill.service.SeckillService;
import org.hibernate.annotations.AttributeAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author steamyao
 * @Package cn.steamyao.seckill.web
 * @date 2019/4/12 17:33
 * @description
 */

@RestController
public class WebTest {

    @Autowired
    ServiceTest serviceTest;

    @Autowired
    SeckillRepository seckillRepository;

    @Autowired
    private SeckillLockService lockService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private SuccessKilledRepository repository;

    @RequestMapping("/test/kafka")
    public String send(){
        kafkaTemplate.send("Hello-Kafka","测试kafka");
        return "success";
    }

    @RequestMapping("/test/redis")
    public void test1(){
     serviceTest.test();
    }

    @RequestMapping("/test/db")
    public void test2(){
    System.out.println(seckillRepository.count());
    seckillRepository.descCount(1001);
    seckillRepository.reBackCount(1001);
    System.out.println(seckillRepository.count());

    //System.out.println(successKilledRepository.count());;

    }

    @RequestMapping("/test/zk")
    public void test3(){
        lockService.seckillZkLock(1001,234);
    }


}
