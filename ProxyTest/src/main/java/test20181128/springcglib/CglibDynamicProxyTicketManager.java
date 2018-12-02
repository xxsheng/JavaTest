package test20181128.springcglib;

import java.lang.reflect.Method;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

public class CglibDynamicProxyTicketManager implements MethodInterceptor {

	// 目标对象
	private Object targetObject;
	
	/**
	 * 
	 * 创建代理对象
	 * @param targetObject
	 * @return
	 */
	public Object getInstance(Object targetObject) {
		this.targetObject = targetObject;
		Enhancer enhancer = new Enhancer(); //用这个类来创建代理对象（被代理类的子类）
		enhancer.setSuperclass(this.targetObject.getClass()); //设置被代理类作为其父类
		
		//回调方法
		enhancer.setCallback(this);
		
		//创建代理对象
		return enhancer.create();
		
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.cglib.proxy.MethodInterceptor#intercept(java.lang.Object, java.lang.reflect.Method, java.lang.Object[], org.springframework.cglib.proxy.MethodProxy)
	 */
	@Override
	public Object intercept(Object arg0, Method arg1, Object[] arg2, MethodProxy arg3) throws Throwable {
		// TODO Auto-generated method stub
		Object result = null;
		checkIdentity(); //前置增强
		result = arg3.invokeSuper(arg0, arg2);
		log();
		return result;
	}

	public void checkIdentity() {
		System.out.println("身份验证");
	}
	
	public void log() {
		System.out.println("日志输出");
	}
}
