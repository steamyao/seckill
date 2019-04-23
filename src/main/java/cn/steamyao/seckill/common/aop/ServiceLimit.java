package cn.steamyao.seckill.common.aop;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author steamyao
 * @Package cn.steamyao.seckill.common.aop
 * @date 2019/4/13 10:51
 * @description    令牌桶限流注解，默认流量 300
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceLimit {
    int value() default 300;
}
