/**
 * 
 */
package com.test;

/**
 * @author xxq_1
 *
 */
public class Paper implements Runnable {
	
	private String name;
	private Acount account = new Acount();
	
	public Paper (String name, Acount account) {
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
			account.subAcount(name, 50);
		}
	}

}
