1、redis 项目练习
https://blog.csdn.net/lupengfei1009/article/details/73662432#commentBox
2、redis 相关博客网站学习
https://blog.csdn.net/xingna37/article/details/79362305 redis命令
http://doc.redisfans.com/ redis命令
https://mp.weixin.qq.com/s/QA8zGHIFPf128lk9IJutDQ redis博客
http://www.cnblogs.com/cunkouzh/p/9242292.html 哨兵模式
https://blog.csdn.net/kity9420/article/details/53571718
https://blog.csdn.net/Mars13889146832/article/details/79534981
https://blog.csdn.net/Mars13889146832/article/details/79534981
3、redis缓存雪崩 缓存击穿 缓存穿透
http://www.cnblogs.com/raichen/p/7750165.html
4、redis作为分布式锁
https://blog.csdn.net/qq_33666373/article/details/78870294
https://www.cnblogs.com/SUNSHINEC/p/8302540.html
https://www.cnblogs.com/linjiqin/p/8003838.html
https://blog.csdn.net/wgwgnihao/article/details/52098805

2、springmvc源码解析
https://www.cnblogs.com/yangzhilong/p/3725849.html
https://blog.csdn.net/abc997995674/article/details/80511545

3、mysql和redis结合方案
https://www.cnblogs.com/daydaynobug/p/6649960.html

输入输出流案例
http://bbs.itheima.com/thread-272992-1-1.html

java面试
https://mp.weixin.qq.com/s/r44WVE1v5uXAPA0axDbP8Q
java需要掌握的技术
https://mp.weixin.qq.com/s/ZJQJRDpWw2KvS6vJZf_PyQ
mysql需要掌握的技术
https://mp.weixin.qq.com/s/F4GwkemXWoZQyEimdJYflQ

1，待学习点 了解redis原理
2，redis怎么做缓存
https://blog.csdn.net/liuxiao723846/article/details/52424802
https://www.cnblogs.com/fuchuanzhipan1209/p/7204756.html
https://www.cnblogs.com/fqwsndc1314-5207788/p/7594924.html
https://www.cnblogs.com/springlight/p/6374372.html
3，redis怎么做队列


java高级开发

⚪虚拟机内存优化 
⚪数据库性能调优 
⚪分布式高并发架构 
	分布式事务
	不做事务，延后校对
	用消息中间件
⚪一些热门组件，比如redis,nginx等 
⚪大数据方向的用法 
⚪java core（比如集合或多线程）方面的底层实现代码 
⚪Spring系列（比如IOC, AOP, MVC,Spring Boot, Spring Cloud）方面的底层实现代码。

1、SQL高级方面，比如group by, having，左连接，子查询（带in），行转列等高级用法。
2、建表方面，你可以考虑下，你项目是用三范式还是反范式，理由是什么？
3、尤其是优化，你可以准备下如何通过执行计划查看SQL语句改进点的方式，或者其它能改善SQL性能的方式（比如建索引等）。
4、如果你感觉有能力，还可以准备些MySQL集群，MyCAT分库分表的技能。比如通过LVS+Keepalived实现MySQL负载均衡，MyCAT的配置方式。同样，如果可以，也看些相关的底层代码。

String a = "123"; String b = "123"; a==b的结果是什么？ 这包含了内存，String存储方式等诸多知识点。

HashMap里的hashcode方法和equal方法什么时候需要重写？如果不重写会有什么后果？对此大家可以进一步了解HashMap（甚至ConcurrentHashMap）的底层实现。
ArrayList和LinkedList底层实现有什么差别？它们各自适用于哪些场合？对此大家也可以了解下相关底层代码。
volatile关键字有什么作用？由此展开，大家可以了解下线程内存和堆内存的差别。
CompletableFuture，这个是JDK1.8里的新特性，通过它怎么实现多线程并发控制？
JVM里，new出来的对象是在哪个区？再深入一下，问下如何查看和优化JVM虚拟机内存。
Java的静态代理和动态代理有什么差别？最好结合底层代码来说。

Linux
1、能通过less命令打开文件，通过Shift+G到达文件底部，再通过?+关键字的方式来根据关键来搜索信息。
2、能通过grep的方式查关键字，具体用法是, grep 关键字 文件名，如果要两次在结果里查找的话，就用grep 关键字1 文件名 | 关键字2 --color。最后--color是高亮关键字。
3、能通过vi来编辑文件。
4、能通过chmod来设置文件的权限。

Java底层
1、ArrayList,LinkedList的底层代码里，包含着基于数组和链表的实现方式，如果大家能以此讲清楚扩容，“通过枚举器遍历“等方式，绝对能证明自己。
2、HashMap直接对应着Hash表这个数据结构，在HashMap的底层代码里，包含着hashcode的put，get等的操作，甚至在ConcurrentHashMap里，还包含着Lock的逻辑。我相信，如果大家在面试中，看看而言ConcurrentHashMap，再结合在纸上边说边画，那一定能征服面试官。
3、可以看下静态代理和动态代理的实现方式，再深入一下，可以看下Spring AOP里的实现代码。
4、或许Spirng IOC和MVC的底层实现代码比较难看懂，但大家可以说些关键的类，根据关键流程说下它们的实现方式。 
其实准备的底层代码未必要多，而且也不限于在哪个方面，比如集合里基于红黑树的TreeSet，基于NIO的开源框架，甚至分布式组件的Dubbo，都可以准备。而且准备时未必要背出所有的底层（事实上很难做到），你只要能结合一些重要的类和方法，讲清楚思路即可（比如讲清楚HashMap如何通过hashCode快速定位）。

Treeset hashset

解答 hashmap
https://blog.csdn.net/wangxin1982314/article/details/51225566
采用数组加链表结构
能够在快速查找的同时，快速增加和删除 兼用arraylist和linkedlist
采用key的hashcode比对内部类entry的size的余数进行存取。如果即将到达满，扩容长度的俩倍

几种数据库对比
http://www.mamicode.com/info-detail-1242126.html

git 
https://blog.csdn.net/u011534057/article/details/77505770
https://www.cnblogs.com/wangmingshun/p/5425150.html
https://www.cnblogs.com/mengdd/p/3585038.html