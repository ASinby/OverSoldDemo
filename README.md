# 一、工程简介

+ 阅读《深入浅出SpringBoot2.0x》实操
    + 分别使用悲观锁、乐观锁以及Redis解决超卖问题

# 二、延伸阅读

## 1、悲观锁
    优点：实现简单，只需要在读取库存的SQL后面加上FOR UPDATE 即可。
    缺点：由于加FOR UPDATE后，若多线程同时查询数据，先抢占到资源的进程会独占锁，直至其操作完成后释放锁，其他线程才会继续。故此，就会导致性能低下。这样就有了乐观锁。

## 2、乐观锁
    优点：高效。
    缺点：由于使用版本号去控制库存的减少，这就会出现有大量请求失败。故此，就需要在原有乐观锁加上限制重入次数或是限制重入时间。

## 3、Redis

+ 1、在Linxu系统中使用docker 拉取一个Redis容器
+ 2、进入redis客户端，初始化库存即可
    ```shell script
       ## 进入redis镜像   docker exec -it <镜像id或者名称> redis-cli -a <登录密码>
       docker exec -it 874693c1 redis-cli -a 123456

       ## 初始化redis产品库存
       hmset product_1 id 1 stock 100 price 5.00
    ```
    
# 三、其他笔记

+ 1、Mybatis的 `*Mapper.xml`文件必须放在`resource`文件夹下面
+ 2、非前后端分离项目 html、js等文件必须放在`static`文件夹下面
+ 3、之前不明白的——为什么非得把主要业务代码写在`Service`中，而不是`Controller`中。现在明白啦——在不更改对外接口的情况下，只需修改接口中service，即可完成不同业务代码之间的切换。
+ 4、Mybatis-puls的使用
+ 5、注解`@Configuration`和`@ConditionalOnProperty`的配合使用——`@ConditionalOnProperty`加载配置文件中的配置，判断`@Configuration`是否生效。

# 四、参考

+ 1、《深入浅出SpringBoot2.0x》
+ 2、[Redis操作指令](https://www.redis.net.cn/order/3528.html)
+ 3、[解决跨域请求失败问题](https://baijiahao.baidu.com/s?id=1701075171555228173&wfr=spider&for=pc)
+ 4、[接口测试hoppscotch](https://hoppscotch.io/cn)
+ 5、[Springboot项目连接IBM DB2数据库（手动导入驱动）](https://www.hangge.com/blog/cache/detail_2832.html)