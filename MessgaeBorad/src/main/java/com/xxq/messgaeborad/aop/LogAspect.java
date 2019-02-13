/**
 * 
 */
package com.xxq.messgaeborad.aop;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author: xiaxiuqiang
 * @Date: 2019/06/19 14:32
 * @todo:
 */

@Aspect
@Component
public class LogAspect {

	private Logger logger = LoggerFactory.getLogger(LogAspect.class);
	
	/**
	 * @author xiaxiuqiang
	 * @Date 2019/02/12 14:39
	 * @todo 切点控制，所有的controller都要走这个切点
	 * @param * @param null	
	 * 
	 */
	@Pointcut("execution( * com.xxq..controller.*.*(..))") //俩个..代表所有子目录，最后括号里的俩个..代表所有参数
	public void logPointCut() { }
	
	/**
	 * @author xiaxiuqiang
	 * @date 2019/02/12 17:55
	 * @todo 前置通知 	在 logPointCut() 方法前进行通知
	 * @param joinPoint
	 */
	@Before("logPointCut()")
	public void doBefore(JoinPoint joinPoint) {
		//接收到请求，记录请求内容
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		
		//记录以下请求内容
		logger.info("请求地址 ：" + request.getRequestURI().toString()) ;
		logger.info("请求方式 ：" + request.getMethod());
		logger.info("请求的类和方法：" + joinPoint.getSignature().getDeclaringTypeName() + "."
				+ joinPoint.getSignature().getName());
		logger.info("参数：" + Arrays.toString(joinPoint.getArgs()));
	}
	
	/**
	 * @author xiaxiuqiang
	 * @date 2019/02/12 18:02
	 * @todo：后置通知获取返回值
	 * @param ret
	 */
	@AfterReturning(returning = "ret", pointcut = "logPointCut()") //returning的值和doAfterReturning的参数名一致
	public void doAfterReturning(Object ret) {
		//处理完请求，返回内容（返回值太过复杂时，打印的是对象物理存储空间的地址）
		logger.info("返回值：" + ret);
	}
	
	@Around("logPointCut()")
	public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
		long startTime  = System.currentTimeMillis();
		// obj为方法返回值
		Object obj = pjp.proceed();
		
		logger.info("耗时：" + (System.currentTimeMillis() - startTime));
		return obj;
		
	}
}