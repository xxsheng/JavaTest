package staticClass;

/**
 * https://blog.csdn.net/zzh920625/article/details/75127399
 * @author xxq_1
 *
 */
public class StaticClass3 {
	public static void main(String[] ars){
	     A ab = new B(); //记为A。执行到此处,结果是：
	     ab = new B(); //记为B。执行到此处,结果是： 
	     B bb=new B();//记为C。执行到此处，结果是：
	   }
}
class A {
	static {
		System.out.print("1");
	}
	public A() {
		System.out.print("2");
	}
}
class B extends A {
	static {
		System.out.print("a");
	}
	
	public B() {
		System.out.print("b");
	}
}