package com.test;

public class ThreadA {  
    public static void main(String[] args) {  
        ThreadB b = new ThreadB();  
        b.start();//主线程中启动另外一个线程  
        System.out.println("b is start....");  
        //括号里的b是什么意思,应该很好理解吧  
        synchronized(b) {  
            try {  
                System.out.println("Waiting for b to complete...");  
                System.out.println("666");
                b.notify();
                b.wait();//这一句是什么意思，究竟谁等待?  
                System.out.println("ThreadB is Completed. Now back to main thread");  
                }catch (InterruptedException e){}  
        }  
        System.out.println("Total is :" + b.total);  
    }  
}  
   
class ThreadB extends Thread {  
        int total;  
        public void run() {  
            synchronized(this) { 
            	try {
					this.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                System.out.println("ThreadB is running..");  
                for (int i=0; i<=1000000; i++ ) {  
                    total += i;  
                }  
                System.out.println("total is " + total);  
                notify();  
            }  
        }  
}

