/**
 * 
 */
package com.test1;

/**
 * @author xxq_1
 *
 */
public class Testmain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		DeadLock deadlock = new DeadLock();
		
		Thread thread = new Thread(deadlock);
		Thread thread1 = new Thread(deadlock);
		
		thread.start();
		thread1.start();
	}

}
