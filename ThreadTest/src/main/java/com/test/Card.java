/**
 * 
 */
package com.test;

/**
 * 银行卡负责存钱
 * @author xxq_1
 *
 */
public class Card implements Runnable {

	private String name;
	private Acount account = new Acount();

	public Card (String name, Acount account) {
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
			
			account.addAcount(name, 100);
		}
	}

}
