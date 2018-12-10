/**
 * 
 */
package com.test3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xxq_1
 *
 */
public class Account2 {

	private boolean flag = false; //默认为false，true才可以取钱
	private int count = 0;
	
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition condition = lock.newCondition();
	
	public void addCount (String name, int money) {
		lock.lock();
		if(flag) {
			try {
				condition.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			count += money;
			System.out.println(name + "...存入..." + money +"..." + Thread.currentThread().getName());
			getAccount(name);
			flag = true;
			condition.signalAll();
		}
		lock.unlock();
	}
	
	public void subCount(String name, int money) {
		lock.lock();
		if(!flag) {
			try {
				condition.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			if(count-money<0) {
				return;
			}
			count -=money;
			System.out.println(name +"...取出..."+ money +"..." +Thread.currentThread().getName());
			getAccount(name);
			flag = false;
			condition.signalAll();
		}
		lock.unlock();
	}
	
	public void getAccount(String name) {
		System.out.println(name +"...余额..." + count);
	}
}
