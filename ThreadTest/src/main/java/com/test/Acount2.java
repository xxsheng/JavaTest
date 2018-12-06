/**
 * 
 */
package com.test;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 银行账号
 * 
 * @author xxq_1
 *
 */
public class Acount2 {

	private int count = 0;
	private ReentrantLock lock = new ReentrantLock();

	/**
	 * 存钱
	 * 
	 * @param name
	 *            姓名
	 * @param money
	 *            金额
	 */
	public void addAcount(String name, int money) {
		lock.lock();
		try {

			count += money;
			System.out.println(name + "...存入：" + money + "..." + Thread.currentThread().getName());
			getAcount(name);
		} finally {
			// TODO: handle finally clause
			lock.unlock();
		}

	}

	public void subAcount(String name, int money) {
		lock.lock();
		try {
			System.out.println(this.getClass().getName());
			// 先判断当前余额是否够取钱金额
			if (count - money < 0) {
				System.out.println("余额不足");
				return;
			}
			// 取钱
			count -= money;
			System.out.println(name + "...取出：" + money + "..." + Thread.currentThread().getName());
			getAcount(name);

		} finally {
			// TODO: handle finally clause
			lock.unlock();
		}

	}

	public void getAcount(String name) {
		System.out.println(name + "...余额： " + count);
	}

}
