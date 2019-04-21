package cn.steamyao.seckill.common.aop;

import com.google.common.util.concurrent.RateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * @author steamyao
 * @Package cn.steamyao.seckill.common.aop
 * @date 2019/4/13 10:55
 * @description  令牌桶算法限流拦截器
 */
@Component
@Scope
@Aspect
public class ServiceLimiterInterceptor {

    ////每秒只发出5个令牌，此处是单进程服务的限流,内部采用令牌捅算法实现
    private static  RateLimiter rateLimiter = RateLimiter.create(5.0);

    //Service层切点  限流
    @Pointcut("@annotation(cn.steamyao.seckill.common.aop.ServiceLimit)")
    public void ServiceAspect() {

    }

    @Around("ServiceAspect()")
    public  Object around(ProceedingJoinPoint joinPoint) {
        Boolean flag = rateLimiter.tryAcquire();
        System.out.println(flag);
        Object obj = null;
        try {
            if(flag){
                obj = joinPoint.proceed();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return obj;
    }
}
