package test20181128.staticproxy.proxysubject;

import test20181128.staticproxy.TicketManager;

/**
 * @author xxq_1
 *
 */
public class StaticProxyTicketManager implements TicketManager {
	TicketManager ticketManager;//目标对象的引用
	
	public StaticProxyTicketManager(TicketManager ticketManager) {
		this.ticketManager = ticketManager;
	}

	@Override
	public void soldTicket() {
		// TODO Auto-generated method stub
		checkIdentity();
		ticketManager.soldTicket();
	}

	@Override
	public void changeTicket() {
		// TODO Auto-generated method stub
		checkIdentity();
		ticketManager.changeTicket();
	}

	@Override
	public void returnTicket() {
		// TODO Auto-generated method stub
		checkIdentity();
		ticketManager.returnTicket();
	}

	
	/**
	 *前置增强 
	 */
	private void checkIdentity() {
		// TODO Auto-generated method stub
		System.out.println("身份验证----------------");
	}

}
