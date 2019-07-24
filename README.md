### 项目名称
这个一个模拟秒杀场景的小 Demo（为了学习使用，非商业用途）
### 上手指南
#### 开发环境
springboot 2.1   
IDEA   
maven 3.6   
kafka 2.1   
redis 3.0   
zookeeper    
jdk 8.0    
mysql8.0（这个不重要，换一个mysql-connection的jar包就行）
#### 你将学到
+ 搭建springboot 2.1框架
+ 整合redis、kafka
+ 数据库乐观。悲观锁、
+ 线程池及多线程相关的知识
+ 整合 swagger 2.0
+ redis与zookeeper 分布式锁的使用


#### 安装步骤
1.在 application.properties 修改服务端口、redis、zookeeper。kafka、mysql地址。\        
2 在 lock 包中修改 redis、zookeeper 地址，这个包是分布式锁的，做成了Utils类。       
3.好像没有了吧！    
#### 项目展示
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190508194336891.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM2NjQ3MTc2,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190508194356658.png)
#### bug
1. 在悲观锁一的实现中，由于加了事务，会出现死锁与事务回退（不会超卖），不加会出现超卖，所有会报出  Deadlock found when trying to get lock; try restarting transaction 异常      
2. 乐观锁二 超卖严重，这个不算 bug 吧，呵呵。     
3. 秒杀时将成功的用户加入队列，然后用定时任务写入数据库，所有有时数据库操作成功，但后台显示秒杀成功为0，就是这个原因，可以通过直接写入数据库解决。     
#### 未完成
最后一个秒杀，是通过 Jmeter 模拟多个线程来访问，最高可以接受 100000 个线程，也不会出错，基本功能实现了，不算完成的很好。   
#### 鸣谢   
该项目参考了 **小柒2012** 的 [springboot-seckill](https://gitee.com/52itstyle/spring-boot-seckill)\
水平有限，感谢阅读！  

