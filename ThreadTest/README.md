一、线程并发同步概念

线程同步其核心就在于一个“同”。所谓“同”就是协同、协助、配合，“同步”就是协同步调昨，也就是按照预定的先后顺序进行运行，即“你先，我等， 你做完，我再做”。

线程同步，就是当线程发出一个功能调用时，在没有得到结果之前，该调用就不会返回，其他线程也不能调用该方法。

就一般而言，我们在说同步、异步的时候，特指那些需要其他组件来配合或者需要一定时间来完成的任务。在多线程编程里面，一些较为敏感的数据时不允许被多个线程同时访问的，使用线程同步技术，确保数据在任何时刻最多只有一个线程访问，保证数据的完整性。

多线程 sync锁线程对象
死锁的解决方案
https://blog.csdn.net/m0_38126177/article/details/78587845
我们可以使用ReentrantLock.tryLock()方法，在一个循环中，如果tryLock()返回失败，那么就释放以及获得的锁，并睡眠一小段时间。这样就打破了死锁的闭环。
 因此避免死锁的一个通用的经验法则是:当几个线程都要访问共享资源A、B、C时,保证使每个线程都按照同样的顺序去访问它们,比如都先访问A,在访问B和C。 


https://zhidao.baidu.com/question/144956417.html 

https://www.cnblogs.com/mujingyu/p/7856388.html 多线程例子

springmvc获取页面参数
https://blog.csdn.net/itguangit/article/details/78305285

1、mybatis
2、springmvc Java aio bio nio
3、mangodb redis
4、MQ dubbo zok 
5、netty

https://blog.csdn.net/u011961421/article/details/79416510 springboot
https://www.cnblogs.com/shijiaoyun/p/7458341.html spring原理
https://www.cnblogs.com/ygj0930/p/6543960.html 	nio bio aio
https://www.cnblogs.com/yyjie/p/7788413.html 	深入理解sql左连接
https://www.cnblogs.com/xiaoxi/p/6164383.html	springmvc工作原理
https://www.cnblogs.com/chengxiao/p/6528109.html 谈谈Java中的volatile
https://www.cnblogs.com/whtydn/p/4965966.html JavaEE面试

ThreadLocal
https://www.iteye.com/topic/103804 详细解析
线程池解析
jdk8以前 https://www.cnblogs.com/dolphin0520/p/3932921.html
jdk8 https://www.cnblogs.com/cjsblog/p/8214921.html