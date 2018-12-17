package staticClass;

/**
 * 对于静态类总结是：
 * 1.如果类的构造器或静态工厂中有多个参数，设计这样类时，最好使用Builder模式，特别是当大多数参数都是可选的时候。
 * 2.如果现在不能确定参数的个数，最好一开始就使用构建器即Builder模式。
 * https://www.cnblogs.com/KingIceMou/p/7823918.html
 * https://blog.csdn.net/zzh920625/article/details/75127399 ******
 * @author xxq_1
 *
 */
class Outer {

	private String name;
	private int age;
	
	public static class Builder {
		private String name;
		private int age;
		
		public Builder(int age) {
			this.age = age;
		}
		
		public Builder withName(String name) {
			this.name = name;
			return this;
		}
		
		public Builder withAge(int age) {
			this.age = age;
			return this;
		}
		
		public Outer build() {
			return new Outer(this);
		}
	}
	
	private Outer(Builder b) {
		this.age = b.age;
		this.name = b.name;
	}

	@Override
	public String toString() {
		return "Outer [name=" + name + ", age=" + age + "]";
	}
	
	
}

public class StaticClass{
	
	public static void main(String[] args) {
		Outer outer = getOuter();
		System.out.println(outer.toString());
	}

	public static Outer getOuter() {
		// TODO Auto-generated method stub
		Outer outer = new Outer.Builder(25).withName("xxq").build();
		return outer;
	}
	
}
