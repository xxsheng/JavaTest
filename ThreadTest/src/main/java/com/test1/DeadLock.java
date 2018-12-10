/**
 * 
 */
package com.test1;

/**
 * @author xxq_1
 *
 */
public class DeadLock implements Runnable{

	private int i=0;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		while(true) {
			
			
			if(i%2 == 0) {
				System.out.println("true开始");
				synchronized (LockA.lockA) {
					System.out.println(Thread.currentThread().getName());
					System.out.println("if...lockA");
					System.out.println(i);
					System.out.println(i%2 == 0);
					System.out.println("-----------------");
					synchronized (LockB.lockB) {
						System.out.println("if...lockB");
					}
				}
			}else {
				System.out.println("false开始");
				synchronized (LockB.lockB) {
					System.out.println(Thread.currentThread().getName());
					System.out.println("else...lockB");
					System.out.println(i);
					System.out.println(i%2 == 0);
					System.out.println("**************");
					synchronized (LockA.lockA) {
						System.out.println("else...lockA");
					}
				}
			}
			System.out.println("111");
			i++;
		}
	}
	
	
}
