package test20181128.staticproxy;

import test20181128.staticproxy.proxysubject.LogProxy;
import test20181128.staticproxy.proxysubject.StaticProxyTicketManager;
import test20181128.staticproxy.realsubject.TicketManagerImpl;

public class Test {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		
		TicketManager ticketManager = new LogProxy(new StaticProxyTicketManager(new TicketManagerImpl()));
		
		Thread tr = new Thread();
		ticketManager.soldTicket();
	
		ticketManager.changeTicket();
	
		ticketManager.returnTicket();
		
	}

}
