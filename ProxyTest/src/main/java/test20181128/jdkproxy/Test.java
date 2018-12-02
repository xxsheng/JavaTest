package test20181128.jdkproxy;

import test20181128.staticproxy.TicketManager;
import test20181128.staticproxy.realsubject.TicketManagerImpl;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		DynamicProxyTicketManager dynamicProxyTicketManager = new DynamicProxyTicketManager();
		TicketManager tm = (TicketManager) dynamicProxyTicketManager.newProxyInstance(new TicketManagerImpl());
		
		tm.soldTicket();
		tm.changeTicket();
		tm.returnTicket();
	}

}
