/**
 * 
 */
package com.test3;

/**
 * @author xxq_1
 *
 */
public class Test3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Account account = new Account();
		
		Card card1 = new Card("card1", account);
		Card card2 = new Card("card2", account);
		Card card3 = new Card("card3", account);
		
		Paper pp1 = new Paper("pp1", account);
		Paper pp2 = new Paper("pp2", account);
		
		Thread thread1 = new Thread(card1, "card1");
		Thread thread2 = new Thread(card2, "card2");
		Thread thread3 = new Thread(card3, "card3");
		
		Thread thread4 = new Thread(pp1, "pp1");
		Thread thread5 = new Thread(pp2, "pp2");
		
		thread1.start();
		thread2.start();
		thread3.start();
		
		thread4.start();
		thread5.start();
	}

}
