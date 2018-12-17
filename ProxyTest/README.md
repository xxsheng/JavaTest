设计模式

1、代理模式 
https://blog.csdn.net/tanggao1314/column/info/javadesignmodel
总结：
 
静态代理 事先写好代理类 
优点：容易理解，编译期前写好类，可以进行修改等操作。
缺点：每一个需要代理的类都要写代理类，太繁琐，太浪费资源。

jdk动态代理 实现InvocationHandler接口
优点：jdk动态代理，无需针对于每个类编写代理类，编译期自动生成代理类，高效。
缺点：需要有和目标对象共同的接口才可以用jdk代理。有一定的局限性。

spring cglib代理 实现MethodInterceptor接口
优点：spring动态代理，无需有共同的接口，局限性较小，只需将目标类作为父类就可以进行代理。

静态类 和普通类的区别
