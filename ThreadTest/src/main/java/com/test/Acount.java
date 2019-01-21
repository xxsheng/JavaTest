/**
 * 
 */
package com.test;

/**
 * 银行账号
 * 
 * @author xxq_1
 *
 */
public class Acount {

	private int count = 0;

	/**
	 * 存钱
	 * 
	 * @param name
	 *            姓名
	 * @param money
	 *            金额
	 */
	public void addAcount(String name, int money) {
		synchronized (this) {
			System.out.println(this);
			count += money;
			System.out.println(name + "...存入：" + money + "..." + Thread.currentThread().getName());
			getAcount(name);
		}
		
	}

	public void subAcount(String name, int money) {
		synchronized (this) {
			System.out.println(this);
			// 先判断当前余额是否够取钱金额
			if (count - money < 0) {
				System.out.println("余额不足");
				return;
			}
			// 取钱
			count -= money;
			System.out.println(name + "...取出：" + money + "..." + Thread.currentThread().getName());
			getAcount(name);	
		}
		
	}

	public void getAcount(String name) {
		System.out.println(name + "...余额： " + count);
	}

}
