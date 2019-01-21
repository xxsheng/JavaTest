/**
 * 
 */
package com.test;

/**
 * @author xxq_1
 *
 */
public class ThreadDemoTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Acount account = new Acount();
		System.err.println(account);
		Card card = new Card("card",account);
		Paper pp = new Paper("paper", account);
		
		Thread thread1 = new Thread(card);
		Thread thread2 = new Thread(pp);
		
		thread1.start();
		thread2.start();
		
	}

}
