/**
 * 
 */
package test20181128.staticproxy;

/**
 * @author xxq_1
 *抽象角色：声明真实对象和代理对象的共同接口
 */
public interface TicketManager {
	
	/**
	 *售票 
	 */
	public void soldTicket();
	
	
	/**
	 * 改签
	 */
	public void changeTicket();
	
	/**
	 * 退票
	 */
	public void returnTicket();
	
}
