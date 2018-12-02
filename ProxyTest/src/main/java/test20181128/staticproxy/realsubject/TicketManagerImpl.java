package test20181128.staticproxy.realsubject;

import test20181128.staticproxy.TicketManager;

public class TicketManagerImpl implements TicketManager {

	@Override
	public void soldTicket() {
		// TODO Auto-generated method stub

		System.out.println("售票");
	}

	@Override
	public void changeTicket() {
		// TODO Auto-generated method stub

		System.out.println("改签");
	}

	@Override
	public void returnTicket() {
		// TODO Auto-generated method stub

		System.out.println("退票");
	}

}
