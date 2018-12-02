package xxq1;

import org.junit.Test;

import xxq.Test1;

public class Test2 {

	
	private Test1 t1 = new Test1();
	public void main() {
		t1.test1(4);
	}
	
	@Test
	public void test() {
		int a = 5;
		t1.test1(a);
	}
}
