/**
 * 
 */
package com.test3;

/**
 * 银行卡负责存钱
 * @author xxq_1
 *
 */
public class Card implements Runnable {

	private String name;
	private Account account = new Account();

	public Card (String name, Account account) {
		this.name = name;
		this.account = account;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		while(true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			account.addCount(name, 100);
		}
	}

}
