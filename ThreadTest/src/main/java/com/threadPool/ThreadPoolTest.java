package com.threadPool;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class ThreadPoolTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		FutureTask<Integer> result = new FutureTask<>(new CallableThreadTest());
		
		ExecutorService es = Executors.newCachedThreadPool();
		//es.submit(new TestThread());
		//es.submit(new TestThread());
		es.submit(result);
		
		
		try {
			Integer sum = result.get();
			System.out.println(sum);
			System.out.println("------------------");
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		es.shutdown();
	}

}

class TestThread implements Runnable{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		for(int i=0; i<100; i++) {
			System.out.println(Thread.currentThread().getName() + ": " + i);
			
			try {
				Thread.sleep((long) (1000L * Math.random()));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}

class CallableThreadTest implements Callable<Integer> {

	@Override
	public Integer call() throws Exception {
		// TODO Auto-generated method stub
		int sum = 0;
		
		for(int i=0; i<=2; i++) {
			sum += i;
		}
		return sum;
	}
	
}
