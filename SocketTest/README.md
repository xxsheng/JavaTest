知识点：
ServerSocket:Java中一个专门用来建立Socket服务器的类,可以用服务器需要使用的端口号作为参数来创建服务器对象。model:ServerSocket serverSocket=new ServerSocket(port);
socket=serverSocket.accept();// 监听,等待连接,一旦有client端连接便创建socket实例.
然后通过socket交互数据.
serverSocket.accept();//serverSocket.accept();的这一方法可以说是阻塞式的,没有client端连接就一直监听着,等待连接.直到有client端连接进来才通过socket实例与client端进行交互,一个server端可以被多个client端连接,每连接一次都会创建一个socket实例,派发服务线程.
如果把监听写进while()循环里便可实现不断的监听.

1 https://blog.csdn.net/acmman/article/details/80039159 netty入门