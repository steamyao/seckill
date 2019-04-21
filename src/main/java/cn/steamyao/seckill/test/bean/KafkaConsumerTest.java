package cn.steamyao.seckill.test.bean;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author steamyao
 * @Package cn.steamyao.seckill.test.bean
 * @date 2019/4/12 18:01
 * @description
 */
@Component
public class KafkaConsumerTest {

    @KafkaListener(topics = "Hello-Kafka")
    public void listen (ConsumerRecord<?, ?> record) throws Exception {
        System.err.printf("topic = %s, offset = %d, value = %s \n", record.topic(), record.offset(), record.value());
    }

}
