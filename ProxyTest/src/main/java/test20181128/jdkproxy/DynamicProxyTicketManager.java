package test20181128.jdkproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DynamicProxyTicketManager implements InvocationHandler {
	
	private Object targetObject;
	
	/**
	 * 目标的初始化方法，根据目标生成代理类
	 * 
	 * @param targetobject
	 * @return
	 */
	public Object newProxyInstance(Object targetobject) {
		this.targetObject = targetobject;
		
		//第一个参数，目标对象的装载器
		//第二个参数，目标对象已经实现的接口，而这些是动态代理类要实现的接口列表
		//第三个参数，调用实现了InvocationHandler的对象生成动态代理实例，当你一调用代理，代理就会调用InvocationHandler的invoke方法。
		
		return Proxy.newProxyInstance(targetobject.getClass().getClassLoader(), targetobject.getClass().getInterfaces()	, this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// TODO Auto-generated method stub
		//前置增强
		checkIdentity();
		Object ret = null;
		try {
			//调用目标方法
			ret = method.invoke(targetObject, args);
			//执行成功，打印成功信息；
			log();
		} catch (Exception e) {
			e.printStackTrace();
			//失败时，打印失败信息
			System.out.println("error-->>"+ method.getName());
		}
		
		return ret;
		
	}

	/**
	 * 身份认证
	 */
	public void checkIdentity() {
		System.out.println("身份认证----------");
	}
	
	/**
	 * 日志输出
	 */
	public void log() {
		System.out.println("日志-----------");
	}
}
