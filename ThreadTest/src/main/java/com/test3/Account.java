/**
 * 
 */
package com.test3;

/**
 * @author xxq_1
 *
 */
public class Account {

	private int count=0;
	private boolean flag = false;//false 表示可以存款
	
	/**
	 * 存钱
	 * @param name
	 * @param money
	 */
	public void addCount(String name, int money) {
		
		synchronized (this) {
			if(flag) {
				try {
					System.out.println("****" + Thread.currentThread().getName());
					this.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				count += money;
				System.out.println(name + "...存入余额：" + money + "..." +Thread.currentThread().getName());
				getAccount(name);
				flag = true;
				this.notifyAll();
			}
		}
	}
	
	public void subCount(String name, int money) {
		synchronized (this) {
			if(!flag) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				if(count-money<0) {
					return;
				}
				count -= money;
				System.out.println(name +"...取出余额：" + money + "..." +Thread.currentThread().getName());
				getAccount(name);
				flag = false;
				this.notifyAll();
			}
		}
	}
	
	public void getAccount(String name) {
		System.out.println(name + "...查询余额..." + count);
	}
}
