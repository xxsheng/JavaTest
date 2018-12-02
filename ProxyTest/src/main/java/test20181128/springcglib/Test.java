package test20181128.springcglib;

import test20181128.staticproxy.TicketManager;
import test20181128.staticproxy.realsubject.TicketManagerImpl;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		CglibDynamicProxyTicketManager cdpt = new CglibDynamicProxyTicketManager();
		
		TicketManager tm = (TicketManager) cdpt.getInstance(new TicketManagerImpl());
		
		tm.soldTicket();
		tm.changeTicket();
		tm.returnTicket();
	}

}
