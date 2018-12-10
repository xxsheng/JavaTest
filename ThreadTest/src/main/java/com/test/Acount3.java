/**
 * 
 */
package com.test;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 银行账户
 * @author xxq_1
 *
 */
public class Acount3 {
	
	private int count;
	private ReentrantLock lock = new ReentrantLock();

	
	/**
	 * 存钱
	 * @param name
	 * @param money
	 */
	public void addAccount (String name, int money) {
		lock.lock();
		try {
			count += money;
			System.out.println(name + "...存入...：" + money);
		} finally {
			// TODO: handle finally clause
			lock.unlock();
		}
	}
	
	/**
	 * 取钱
	 * @param name
	 * @param money
	 */
	public void subAccount(String name, int money) {
		lock.lock();
		try {
			//先判断余额足够
			if(count - money < 0) {
				System.out.println("当前余额不足");
				return;
			}
			count -= money;
			System.out.println(name + "...取出...：" + money);
		} finally {
			// TODO: handle finally clause
			lock.unlock();
		}
	}
	
	public void getAccount(String name) {
		System.out.println(name +"...余额:" + count);
	}
}
