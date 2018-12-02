/**
 * 
 */
package test20181128.staticproxy.proxysubject;

import test20181128.staticproxy.TicketManager;

/**
 * @author xxq_1
 *
 */
/**
 * @author xxq_1
 *
 */
public class LogProxy implements TicketManager {

	TicketManager ticketManager;
	
	public LogProxy(TicketManager ticketManager) {
		this.ticketManager = ticketManager;
	}

	/* (non-Javadoc)
	 * @see test20181128.TicketManager#soldTicket()
	 */
	@Override
	public void soldTicket() {
		// TODO Auto-generated method stub
		ticketManager.soldTicket();
		log();//后置增加
	}

	/* (non-Javadoc)
	 * @see test20181128.TicketManager#changeTicket()
	 */
	@Override
	public void changeTicket() {
		// TODO Auto-generated method stub
		ticketManager.changeTicket();
		log();
	}

	/* (non-Javadoc)
	 * @see test20181128.TicketManager#returnTicket()
	 */
	@Override
	public void returnTicket() {
		// TODO Auto-generated method stub
		ticketManager.returnTicket();
		log();
	}

	
	/**
	 * 后置增强
	 */
	private void log() {
		// TODO Auto-generated method stub
		System.out.println("日志-------");
	}

}
