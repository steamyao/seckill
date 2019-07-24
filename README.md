
## 项目名称
秒杀项目
地址：https://github.com/steamyao/seckill   
用途：为了学习多线程并发的知识，模拟多线程秒杀环境
## 上手指南
##### 开发环境
springboot 2.1   
IDEA   
maven 3.6   
kafka 2.1   
redis 3.0   
zookeeper    
jdk 8.0    
mysql8.0（这个不重要，换一个mysql-connection的jar包就行）
##### 你将学到
+ 搭建springboot 2.1框架
+ 整合redis、kafka
+ 数据库乐观。悲观锁、
+ 线程池及多线程相关的知识
+ 整合 swagger 2.0
+ redis与zookeeper 分布式锁的使用


#### 安装步骤
1.在 application.properties 修改服务端口、redis、zookeeper。kafka、mysql地址。\        
2 在 lock 包中修改 redis、zookeeper 地址，这个包是分布式锁的，做成了Utils类。       
   
#### 项目展示
1.前端页面展示
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190508194336891.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM2NjQ3MTc2,size_16,color_FFFFFF,t_70)
2.后台页面
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190508194356658.png)
3.数据库
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190724155056597.png)
#### 补充
1. 在悲观锁一的实现中，由于加了事务，会出现死锁与事务回退（不会超卖），不加会出现超卖，所有会报出  Deadlock found when trying to get lock; try restarting transaction 异常      
2. 数据库乐观锁二 超卖严重，这个属于乐观锁的问题。     
3. 秒杀时将成功的用户加入队列，然后用定时任务写入数据库。要经过一定的时间（10s）,才会将秒杀成功的用户信息写回数据库。
4. 最后一个秒杀，是通过 Jmeter 模拟多个线程来访问，最高可以承受 100000 个线程，也不会出错，基本功能实现了。   
#### 鸣谢   
该项目参考了 **小柒2012** 的 [springboot-seckill](https://gitee.com/52itstyle/spring-boot-seckill)\
感谢阅读！  

