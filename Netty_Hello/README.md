1.netty学习
https://blog.csdn.net/acmman/article/category/7611573 完了
http://www.cnblogs.com/applerosa/p/7141684.html 进行中
2.Java nio
https://mp.weixin.qq.com/s?__biz=MzI4Njg5MDA5NA==&mid=2247484235&idx=1&sn=4c3b6d13335245d4de1864672ea96256&chksm=ebd7424adca0cb5cb26eb51bca6542ab816388cf245d071b74891dd3f598ccd825f8611ca20c&token=1834317504&lang=zh_CN#rd 
1）
可简单认为：IO是面向流的处理，NIO是面向块(缓冲区)的处理
面向流的I/O 系统一次一个字节地处理数据。
一个面向块(缓冲区)的I/O系统以块的形式处理数据。
2）
NIO主要有三个核心部分组成：
·buffer缓冲区
·Channel管道
·Selector选择器
3)
Channel管道比作成铁路，buffer缓冲区比作成火车(运载着货物)
4)
要时刻记住：Channel不与数据打交道，它只负责运输数据。与数据打交道的是Buffer缓冲区
Channel-->运输
Buffer-->数据
相对于传统IO而言，流是单向的。对于NIO而言，有了Channel管道这个概念，我们的读写都是双向的(铁路上的火车能从广州去北京、自然就能从北京返还到广州)！
5）
其中ByteBuffer是用得最多的实现类(在管道中读写字节数据)。
6）
select/epoll的优势并不是对于单个连接能处理得更快，而是在于能处理更多的连接。

枚举类型
1.https://www.cnblogs.com/sister/p/4700702.html

java内存模型
https://blog.csdn.net/javazejian/article/details/72772461